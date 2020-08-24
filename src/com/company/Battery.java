package com.company;

public class Battery extends Component {

    @Override
    String getType() {
        return "Battery";
    }

    @Override
    boolean hasWire(WireNode wireNode) {
        return pin1.equals(wireNode) || pin2.equals(wireNode);
    }

    Battery(Double emf) {
        super();
        this.PD = emf;
    }
}
