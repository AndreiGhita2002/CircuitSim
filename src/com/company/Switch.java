package com.company;

public class Switch extends Component {

    @Override
    String getType() {
        return "Switch";
    }

    Switch(boolean closed) {
        super();
        PD = 0.0;
        this.closed = closed;
    }
}
