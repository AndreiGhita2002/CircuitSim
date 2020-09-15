package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WireNode {

    List<Component> componentList = new ArrayList<>();

    int ID;
    static int nextID = 0;

    boolean hasComponent(Component component) {
        AtomicBoolean out = new AtomicBoolean(false);
        componentList.forEach((c) -> {
            if (c.equals(component)) {
                out.set(true);
            }
        });
        return out.get();
    }

    void connectComponents(Component component1, int pin1, Component component2, int pin2) {
        if (!hasComponent(component1)) {
            componentList.add(component1);
            component1.addConnection(this, pin1);
        }
        if (!hasComponent(component2)) {
            componentList.add(component2);
            component2.addConnection(this, pin2);
        }
    }

    void connect(Component component, int pin) {
        if (!hasComponent(component)) {
            componentList.add(component);
        }
        component.addConnection(this, pin);
    }

    void removeComponent(Component component) {
        for (int i = 0; i < componentList.size(); i++) {
            if (componentList.get(i).equals(component)) {
                componentList.remove(i);
                return;
            }
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder("WireNode with ID: " + ID + " connected with: ");

        for (Component component : componentList) {
            out.append("\n -");
            out.append(component);
            out.append(" pin: ");
            out.append(component.whichPin(this));
        }

        return out.toString();
    }

    WireNode() {
        ID = nextID;
        nextID++;
    }
}
