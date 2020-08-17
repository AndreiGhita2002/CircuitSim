package com.company;

import java.util.ArrayList;
import java.util.List;

public class Circuit {

    List<WireNode> wireNodes;
    List<Component> components;

    // gets the component that has that pinID
    Component getComponentWithPin(int pinID) {
        for (Component component : components) {
            if (component.hasPin(pinID)) {
                return component;
            }
        }
        return null;
    }

    void addComponent(Component component) {
        components.add(component);
    }

    void connect(int pinID, WireNode wireNode) {
        wireNode.addPin(getComponentWithPin(pinID).getPin(pinID));
    }

    Circuit() {
        wireNodes = new ArrayList<>();
        components = new ArrayList<>();
    }
}
