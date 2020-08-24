package com.company;

public class Resistor extends Component {
    @Override
    String getType() {
        return "resistor";
    }

    @Override
    boolean hasWire(WireNode wireNode) {
        return false;
    }

    Resistor(Double resistance) {
        super();
        this.resistance = resistance;
    }
}
