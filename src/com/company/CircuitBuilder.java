package com.company;

import java.util.ArrayList;
import java.util.List;

public class CircuitBuilder {

    static List<VisualEntity> entityList;
    static Circuit circuit;
    static int gridCellWidth;
    static int gridCellHeight;

    static VisualEntity getVisualEntity(int x, int y) {
        for (VisualEntity ve : entityList) {
            if (ve.X == x && ve.Y == y) {
                return ve;
            }
        }
        return null;
    }

    static void updateCircuit() {
        //TODO write updateCircuit() that should connect all the components
        // finds a bunch of connected wires
        // makes a single WireNode from these wires
        // connects every adjacent component to the WireNode

        // creates wire nodes
        ArrayList<VisualWireNode> wireNodesLeft = new ArrayList<>();

        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualWireNode) wireNodesLeft.add((VisualWireNode) ve);
        }

        for (VisualWireNode wire : wireNodesLeft) {
            WireNode newNode = new WireNode();
            recursionStep(wireNodesLeft, newNode, wire);
            circuit.addWireNode(newNode);
        }
    }

    static void recursionStep(ArrayList<VisualWireNode> wiresLeft, WireNode newNode, VisualWireNode vw) {
        wiresLeft.remove(vw);

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
            VisualEntity ve = getVisualEntity(vw.X, vw.Y + gridCellHeight);

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

    static void setProperties(List<VisualEntity> entities, Circuit c, int w, int h) {
        entityList = entities;
        circuit = c;
        gridCellWidth = w;
        gridCellHeight = h;
    }
}
