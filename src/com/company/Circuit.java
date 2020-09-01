package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    boolean findLoops(Battery start) {
        Stack<Component> stack = new Stack<>();
        Stack<Integer> pinStack = new Stack<>();
        ArrayList<Component> componentChain = new ArrayList<>();
        stack.add(start);
        componentChain.add(start);

        int nextPin = 2;

        while(!stack.empty()) {
            Component currentComponent = stack.pop();

            //TODO might not work with some loops
            // solution: make an array for all the components that you went through
            // or change the stack into an array

            for (Component c : currentComponent.pin(nextPin).componentList) {
                if (!c.equals(currentComponent)) {
                    if (c.equals(start)) return true;
                    stack.add(c);
                }
            }
        }
        return false;
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

    Circuit() {
        components = new ArrayList<>();
        wires = new ArrayList<>();
    }
}
