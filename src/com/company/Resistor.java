package com.company;

public class Resistor extends Component {
    @Override
    String getType() {
        return "Resistor";
    }

    Resistor(Double resistance) {
        super();
        this.resistance = resistance;
    }
}
