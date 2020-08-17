package com.company;

import java.util.ArrayList;
import java.util.List;

public class WireNode {

    List<Pin> pins = new ArrayList<>();

    boolean hasPin(int id) {
        for (Pin pin : pins) {
            if (pin.ID == id) {
                return true;
            }
        }
        return false;
    }

    void addPin(Pin pin) {
        pins.add(pin);
    }

    void removePin(Pin pin) {
        for (Pin p : pins) {
            if (p.equals(pin)) pins.remove(pin);
        }
    }
}
