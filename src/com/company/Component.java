package com.company;

import org.jgrapht.graph.DefaultEdge;

public abstract class Component extends DefaultEdge {

    Double PD;
    Double resistance = 0.0;
    String name;
    static int nextID = 0;

    abstract String getType();

    @Override
    public Object getSource() {
        return super.getSource();
    }

    @Override
    public Object getTarget() {
        return super.getTarget();
    }

    String toLongString() {
        String out = getType() + " " + name + "\n";
        out += "Potential Difference: " + PD + " V\n";
        if (resistance > 0) {
            out += "Resistance: " + resistance + " Ω\n";
        }
        return out;
    }

    @Override
    public String toString() {
        return this.name;
    }

    Component() {
        this.name = this.getType() + "_" + nextID;
        nextID++;
    }
}
