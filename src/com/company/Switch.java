package com.company;

public class Switch extends Component {

    boolean closed = false;
    //TODO add support for switches in the rest of the program

    @Override
    String getType() {
        return "Switch";
    }

    Switch() {
        super();
        PD = 0.0;
    }
}
