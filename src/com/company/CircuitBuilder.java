package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
                connectWiresInNode(wireNodesList, newNode, wireNodesList.get(i));
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

            // adding the edge in the graph, unless it's null or an open (off) Switch
            if (component != null || (component instanceof Switch && ((Switch) component).closed)) {
                circuit.graph.addEdge(source, target, component);
                //TODO fix this
                // (says it doesn't allow loops)
                // also make it support switches
            }
        }
    }

    void connectWiresInNode(ArrayList<VisualWireNode> wiresLeft, WireNode newNode, VisualWireNode vw) {
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
                connectWiresInNode(wiresLeft, newNode, (VisualWireNode) ve);
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

    void buildFromFile(File file) {
        try {
            Scanner scn = new Scanner(file);
            StringBuilder str = new StringBuilder();
            while(scn.hasNextLine()) {
                str.append(scn.nextLine()).append("\n");
            }
            buildFromString(str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void buildFromString(String str) {
        String[] lines = str.split("\n");

        for (String line : lines) {
            // line represents a component
            if (line.charAt(0) == '+') {
                String[] words = line.split(" ");
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
            if (line.charAt(0) == '!') {
                String[] words = line.substring(2).split(" ");
                for (int j = 0; j < words.length / 2; j++) {
                    entityList.add(new VisualWireNode(Integer.parseInt(words[j * 2]), Integer.parseInt(words[j * 2 + 1])));
                }
            }
        }
    }

    void saveToFile(File file) {
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
            if (file.createNewFile()) System.out.println("File created: " + file.toPath());
            else System.out.println("File already exists.");

            FileWriter writer = new FileWriter(file);
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<VisualEntity> getEntityListCopy() {
        List<VisualEntity> out = new ArrayList<>();
        for (VisualEntity ve : entityList) {
            VisualEntity copy = null;
            int x = ve.X;
            int y = ve.Y;
            if (ve instanceof VisualWireNode) {
                copy = new VisualWireNode(x, y);
            } else if (ve instanceof VisualComponent) {
                Component comp = copyComponent(((VisualComponent) ve).component);
                int o = ve.orientation();
                if (comp != null) {
                    copy = new VisualComponent(comp, x, y);
                    copy.setOrientation(o);
                }
            }
            if (copy != null) {
                out.add(copy);
            }
        }
        return out;
    }

    Component copyComponent(Component comp) {
        Component copy = null;
        double PD = comp.PD;
        double R = comp.resistance;
        switch (comp.getType()) {
            case "Battery":
                copy = new Battery(PD, R);
                break;
            case "LightBulb":
                copy = new LightBulb(R);
                break;
            case "Resistor":
                copy = new Resistor(R);
                break;
            case "Switch":
                copy = new Switch(true);
                break;
        }
        return copy;
    }

    CircuitBuilder(ArrayList<VisualEntity> entities, Circuit c, int w, int h) {
        entityList = entities;
        circuit = c;
        gridCellWidth = w;
        gridCellHeight = h;
    }
}
