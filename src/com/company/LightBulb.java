package com.company;

import java.util.ArrayList;
import java.util.List;

public class LightBulb extends Component {

    Pin pin1;
    Pin pin2;

    Double getBrightness() {
        //TODO getBrightness based on the current flowing through
        return 1.0;
    }

    @Override
    String getType() {
        return "LightBulb";
    }

    @Override
    List<Pin> getPins() {
        List<Pin> out = new ArrayList<>();

        out.add(pin1);
        out.add(pin2);

        return out;
    }

    @Override
    boolean hasPin(int id) {
        return pin1.ID == id || pin2.ID == id;
    }

    @Override
    Pin getPin(int id) {
        if (pin1.ID == id) return pin1;
        if (pin2.ID == id) return pin2;
        return null;
    }
}
