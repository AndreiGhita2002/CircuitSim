package com.company;

public class Pin {
    static int nextID = 0;
    int ID;

    Pin() {
        ID = nextID;
        nextID++;
    }
}
