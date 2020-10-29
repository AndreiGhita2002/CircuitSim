package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CircuitBuilder {

    ArrayList<VisualEntity> entityList;
    ArrayList<String> connections = new ArrayList<>();
    Circuit circuit;
    static int gridCellWidth;
    static int gridCellHeight;

    VisualEntity getVisualEntity(int x, int y) {
        for (VisualEntity ve : entityList) {
            if (ve.X == x && ve.Y == y) {
                return ve;
            }
        }
        return null;
    }

    void updateCircuit() {
        // resetting the circuit
        circuit.reset();
        connections.clear();

        // creates wire nodes
        ArrayList<VisualWireNode> wireNodesList = new ArrayList<>();

        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualWireNode) wireNodesList.add((VisualWireNode) ve);
        }

        // going through all the visualWireNodes
        // and making a single wireNode from all the visualWireNodes that are touching,
        // and then filling the connections array
        for (int i = 0; i < wireNodesList.size(); i++) {
            if (!wireNodesList.get(i).highlighted) {
                WireNode newNode = new WireNode();
                recursionStep(wireNodesList, newNode, wireNodesList.get(i));
                circuit.addWireNode(newNode);
            }
        }

        // connecting the wireNodes
        // format of connections is "<pin> <wireNode.ID> <component.name>"
        ArrayList<String> sources = new ArrayList<>();
        ArrayList<String> targets = new ArrayList<>();

        for (String str : connections) {
            if (str.charAt(0) == '1') {
                sources.add(str);
            }
            if (str.charAt(0) == '2') {
                targets.add(str);
            }
        }

        for (String s : sources) {
            String[] str = s.split(" ");
            if (str[0].equals("!")) continue;

            WireNode source = circuit.getNodeWithID(Integer.parseInt(str[1]));

            // finding the target
            WireNode target = null;
            for (String st : targets) {
                String[] t = st.split(" ");
                if (t[2].equals(str[2])) {
                    target = circuit.getNodeWithID(Integer.parseInt(t[1]));
                }
            }
            if (target == null) continue;

            // finding the component
            Component component = null;
            for (VisualEntity ve : entityList) {
                if (ve instanceof VisualComponent) {
                    if (((VisualComponent) ve).component.name.equals(str[2])) {
                        component = ((VisualComponent) ve).component;
                    }
                }
            }

            // adding the edge in the graph
            if (component != null) {
                circuit.graph.addEdge(source, target, component);
                //TODO fix this
                // (says it doesn't allow loops)
            }
        }
    }

    void recursionStep(ArrayList<VisualWireNode> wiresLeft, WireNode newNode, VisualWireNode vw) {
        if (vw.highlighted) return;
        vw.highlight();

        for(int i = 0; i < 4; i++) {
            VisualEntity ve;
            switch (i) {
                default:
                case 0: ve = getVisualEntity(vw.X, vw.Y - gridCellHeight); break;
                case 1: ve = getVisualEntity(vw.X + gridCellWidth, vw.Y); break;
                case 2: ve = getVisualEntity(vw.X, vw.Y + gridCellHeight); break;
                case 3: ve = getVisualEntity(vw.X - gridCellWidth, vw.Y); break;
            }

            if (ve instanceof VisualWireNode && wiresLeft.contains(ve)) {
                recursionStep(wiresLeft, newNode, (VisualWireNode) ve);
            } else if (ve instanceof VisualComponent) {
                if (ve.orientation() == (2 + i) % 4) {
                    connections.add("1 " + newNode.ID + " " + ((VisualComponent) ve).component.name);
                }
                if (ve.orientation() == i % 4) {
                    connections.add("2 " + newNode.ID + " " + ((VisualComponent) ve).component.name);
                }
            }
        }
    }

    void buildFromFile(String path) {
        File file = new File(path);
        try {
            if (file.createNewFile()) System.out.println("File created: " + path);
            else System.out.println("File already exists.");

            Scanner scn = new Scanner(file);
            StringBuilder str = new StringBuilder();
            while(scn.hasNextLine()) {
                str.append(scn.nextLine()).append("\n");
            }
            System.out.println(str.toString());
            buildFromString(str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void buildFromString(String str) {
        String[] lines = str.split("\n");

        boolean componentSection = true;
        for (int i = 0; i < lines.length; i++) {
            // line represents a component
            if (lines[i].charAt(0) == '+') {
                String[] words = lines[i].split(" ");
                int x = Integer.parseInt(words[1]);
                int y = Integer.parseInt(words[2]);
                int ori = Integer.parseInt(words[3]);

                String[] compWords = words[4].split(":");
                Component comp = Component.initComponent(compWords[0], compWords[1],
                        Double.parseDouble(compWords[2]), Double.parseDouble(compWords[3]));
                VisualComponent vc = new VisualComponent(comp, x, y, ori);
                entityList.add(vc);
            }

            // line is the wireNode list
            if (lines[i].charAt(0) == '!') {
                String[] words = lines[i].split(" ");
                for (int j = 0; j < words.length; j+=2) {
                    entityList.add(new VisualWireNode(Integer.parseInt(words[i]), Integer.parseInt(words[i + 1])));
                }
            }
        }
    }

    void saveToFile(String path) {
        StringBuilder str = new StringBuilder();
        ArrayList<Integer> wirePositions = new ArrayList<>();

        str.append("Components:\n");
        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualComponent)  {
                str.append("+ ");
                str.append(ve.toSaveFormat()).append("\n");
            }
            if (ve instanceof VisualWireNode)  {
                wirePositions.add(ve.X);
                wirePositions.add(ve.Y);
            }
        }

        str.append("Wire nodes:\n! ");
        for (int i = 0; i < wirePositions.size(); i+=2) {
            str.append(wirePositions.get(i));
            str.append(" ");
            str.append(wirePositions.get(i + 1));
            str.append(" ");
        }

        try {
            if (new File(path).createNewFile()) System.out.println("File created: " + path);
            else System.out.println("File already exists.");

            FileWriter writer = new FileWriter(path);
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    CircuitBuilder(ArrayList<VisualEntity> entities, Circuit c, int w, int h) {
        entityList = entities;
        circuit = c;
        gridCellWidth = w;
        gridCellHeight = h;
    }
}
