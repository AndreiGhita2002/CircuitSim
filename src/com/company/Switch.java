package com.company;

public class Switch extends Component {

    boolean closed = false;

    @Override
    String getType() {
        return "Switch";
    }

    Switch() {
        super();
        PD = 0.0;
    }
}
