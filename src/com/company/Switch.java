package com.company;

public class Switch extends Component {

    boolean closed;
    //TODO add support for switches in the rest of the program

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
