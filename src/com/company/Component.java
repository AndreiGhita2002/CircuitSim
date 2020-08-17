package com.company;

import java.util.List;

public abstract class Component  {

    Double current;
    Double resistance = 0.0;

    abstract String getType();
    abstract List<Pin> getPins();
    abstract boolean hasPin(int id);
    abstract Pin getPin(int id);

    Double getPotentialDifference() {
        return current * resistance;   // V = I * R
    }

}
