package com.company;

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

        //TODO write updateCircuit() that should connect all the components
        // finds a bunch of connected wires
        // makes a single WireNode from these wires
        // connects every adjacent component to the WireNode

        // resetting the circuit
        circuit.wires.clear();
        circuit.components.clear();

        // creates wire nodes
        ArrayList<VisualWireNode> wireNodesLeft = new ArrayList<>();

        //TODO use synchronized or something

        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualWireNode) wireNodesLeft.add((VisualWireNode) ve);
        }

        // VisualWireNode wire : wireNodesLeft
        for (int i = 0; i < wireNodesLeft.size(); i++) {
            WireNode newNode = new WireNode();
            recursionStep(wireNodesLeft, newNode, wireNodesLeft.get(i));
            circuit.addWireNode(newNode);
        }

        for (WireNode wire : circuit.wires) {
            System.out.println(wire);
        }
    }

    void recursionStep(ArrayList<VisualWireNode> wiresLeft, WireNode newNode, VisualWireNode vw) {
        wiresLeft.remove(vw);
        vw.highlight();

        // TODO debug this mess

        // TODO fix this YandereDev thing
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
     CircuitBuilder(ArrayList<VisualEntity> entities, Circuit c, int w, int h) {
        entityList = entities;
        circuit = c;
        gridCellWidth = w;
        gridCellHeight = h;
    }
}
