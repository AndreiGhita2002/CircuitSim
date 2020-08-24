package com.company;

public class LightBulb extends Component {


    Double getBrightness() {
        //TODO getBrightness based on the current flowing through
        return 1.0;
    }

    @Override
    String getType() {
        return "LightBulb";
    }

    @Override
    boolean hasWire(WireNode wireNode) {
        return pin1.equals(wireNode) || pin2.equals(wireNode);
    }

    LightBulb(Double resistance) {
        super();
        this.resistance = resistance;
    }
}
