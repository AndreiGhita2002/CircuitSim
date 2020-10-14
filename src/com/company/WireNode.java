package com.company;


public class WireNode {

    int ID;
    static int nextID = 0;

    public String toString() {
        return "wire" + ID;
    }

    WireNode() {
        ID = nextID;
        nextID++;
    }
}
