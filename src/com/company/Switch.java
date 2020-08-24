package com.company;

public class Switch extends Component {

    boolean closed = false;

    @Override
    String getType() {
        return "Switch";
    }

    @Override
    boolean hasWire(WireNode wireNode) {
        return pin1.equals(wireNode) || pin2.equals(wireNode);
    }

    Switch() {
        super();
        PD = 0.0;
    }
}
