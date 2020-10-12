package com.company;

import org.jgrapht.graph.DefaultEdge;

public abstract class Component extends DefaultEdge {

    Double PD;
    Double resistance = 0.0;
    String name;
    static int nextID = 0;

    protected WireNode pin1; // input
    protected WireNode pin2; // output

    abstract String getType();

    WireNode pin(int x) {
        if (x == 1) return pin1;
        if (x == 2) return pin2;

        System.out.println("Component.getPin() has been called with the argument: " + x);
        return null;
    }

    void addConnection(WireNode wireNode, int pin) {
        if (pin == 1) { pin1 = wireNode; return; }
        if (pin == 2) { pin2 = wireNode; return; }

        System.out.println("Component.addConnection() has been called with the argument: " + pin);
    }

    int whichPin(WireNode wire) {
        if (wire.equals(pin1)) return 1;
        if (wire.equals(pin2)) return 2;
        return -1;
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    @Override
    public Object getTarget() {
        return super.getTarget();
    }

    @Override
    public String toString() {
        return this.name;
    }

    Component() {
        this.name = this.getType() + " " + nextID;
        nextID++;
    }
}
