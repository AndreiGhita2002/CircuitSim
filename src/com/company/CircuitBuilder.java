package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CircuitBuilder {

    ArrayList<VisualEntity> entityList;
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
        circuit.wires.clear();
        circuit.components.clear();

        // creates wire nodes
        ArrayList<VisualWireNode> wireNodesList = new ArrayList<>();

        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualWireNode) wireNodesList.add((VisualWireNode) ve);
        }

        // VisualWireNode wire : wireNodesLeft
        for (int i = 0; i < wireNodesList.size(); i++) {
            if (!wireNodesList.get(i).highlighted) {
                WireNode newNode = new WireNode();
                recursionStep(wireNodesList, newNode, wireNodesList.get(i));
                circuit.addWireNode(newNode);
            }
        }
    }

    void recursionStep(ArrayList<VisualWireNode> wiresLeft, WireNode newNode, VisualWireNode vw) {

        if (vw.highlighted) return;

        vw.highlight();

        //TODO fix this YandereDev thing
        // hell
        if (vw.north) {
            VisualEntity ve = getVisualEntity(vw.X, vw.Y - gridCellHeight);

            if (ve instanceof VisualWireNode && wiresLeft.contains(ve)) {
                recursionStep(wiresLeft, newNode, (VisualWireNode) ve);
            } else if (ve instanceof VisualComponent) {
                if (ve.orientation() == 2) {
                    newNode.connect(((VisualComponent) ve).component, 1);
                }
                if (ve.orientation() == 0) {
                    newNode.connect(((VisualComponent) ve).component, 2);
                }
            }
        }
        if (vw.east) {
            VisualEntity ve = getVisualEntity(vw.X + gridCellWidth, vw.Y);

            if (ve instanceof VisualWireNode && wiresLeft.contains(ve)) {
                recursionStep(wiresLeft, newNode, (VisualWireNode) ve);
            } else if (ve instanceof VisualComponent) {
                if (ve.orientation() == 3) {
                    newNode.connect(((VisualComponent) ve).component, 1);
                }
                if (ve.orientation() == 1) {
                    newNode.connect(((VisualComponent) ve).component, 2);
                }
            }
        }
        if (vw.south) {
            VisualEntity ve = getVisualEntity(vw.X, vw.Y + gridCellHeight);

            if (ve instanceof VisualWireNode && wiresLeft.contains(ve)) {
                recursionStep(wiresLeft, newNode, (VisualWireNode) ve);
            } else if (ve instanceof VisualComponent) {
                if (ve.orientation() == 0) {
                    newNode.connect(((VisualComponent) ve).component, 1);
                }
                if (ve.orientation() == 2) {
                    newNode.connect(((VisualComponent) ve).component, 2);
                }
            }
        }
        if (vw.west) {
            VisualEntity ve = getVisualEntity(vw.X - gridCellWidth, vw.Y);

            if (ve instanceof VisualWireNode && wiresLeft.contains(ve)) {
                recursionStep(wiresLeft, newNode, (VisualWireNode) ve);
            } else if (ve instanceof VisualComponent) {
                if (ve.orientation() == 1) {
                    newNode.connect(((VisualComponent) ve).component, 1);
                }
                if (ve.orientation() == 3) {
                    newNode.connect(((VisualComponent) ve).component, 2);
                }
            }
        }
    }

    void buildFromFile(String path) throws IOException {

        File file = new File(path);
        throw new IOException();

        //TODO build the circuit from the file
        // throw exceptions if necessary
    }

     CircuitBuilder(ArrayList<VisualEntity> entities, Circuit c, int w, int h) {
        entityList = entities;
        circuit = c;
        gridCellWidth = w;
        gridCellHeight = h;
    }
}
