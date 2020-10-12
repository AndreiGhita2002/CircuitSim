package com.company;

import java.util.ArrayList;
import java.util.List;

public class Circuit {

    // WireNodes are nodes and Components are edges
    List<Component> components;
    List<WireNode> wires;

    void addComponent(Component component) {
        components.add(component);
    }

    void addWireNode(WireNode wireNode) {
        wires.add(wireNode);
    }

    List<Component> findLoops(Battery start) {



        return null;
    }

    void calculateCurrent() {

        // https://www.elprocus.com/kirchhoffs-laws-working-formula/
        // needs to find how many unknown currents are there
        // then find a node where they meet and a number of loops equal to number of currents

    }

    boolean checkShortCircuit() {

        // if there is a path from a cell to itself that doesn't encounter any resistance,
        // then it return true
        // this should be done for every energy cell in the circuit

        return false;
    }

    // TODO make a method that returns a graph made out of WireNodes and Components (WireNodes are nodes and Components are edges)

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (WireNode wire : wires) {
            out.append(wire);
            out.append("\n");
        }
        return out.toString();
    }

    Circuit() {
        components = new ArrayList<>();
        wires = new ArrayList<>();
    }
}
