package com.company;

import org.jgrapht.graph.DefaultEdge;

public abstract class Component extends DefaultEdge {

    Double PD = 0.0;
    Double current = 0.0;
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

    void calculateVoltage() {
        // V = I * R
        if (!getType().equals("Battery"))  PD = current * resistance;
    }

    String toLongString() {
        String out = getType() + " " + name + "\n";
        out += "Voltage: " + PD + " V\n";
        out += "Current: " + current + " A\n";
        if (resistance > 0) {
            out += "Resistance: " + resistance + " Ω\n";
        }
        return out;
    }

    String toSaveFormat() {
        double v = (int)(PD * 100.0) / 100.0;
        double r = (int)(resistance * 100.0) / 100.0;
        return getType() + ":" + name + ":" + v + ":" + r;
    }

    static Component initComponent(String type, String name, Double pd, Double res) {
        Component comp = null;
        switch (type.toLowerCase()) {
            case "battery":
                comp = new Battery(pd, res);
                break;
            case "lightbulb":
                comp = new LightBulb(res);
                break;
            case "resistor":
                comp = new Resistor(res);
                break;
            case "switch":
                comp = new Switch();
                break;
        }
        if (comp != null) comp.name = name;
        return comp;
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
