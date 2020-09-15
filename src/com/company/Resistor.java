package com.company;

public class Resistor extends Component {
    @Override
    String getType() {
        return "Resistor";
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
