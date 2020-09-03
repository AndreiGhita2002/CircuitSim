package com.company;

public class VisualComponent extends VisualEntity {

    Component component;
    private  int orientation = 0; // the direction in which pin1 is going
    // 0 - north
    // 1 - east
    // 2 - south
    // 3 - west

    VisualComponent(Component component, int x, int y) {
        this.component = component;
        this.X = x;
        this.Y = y;
        this.entityType = "component";

        setImage(component.getType() + ".png");
        refresh();
    }

    @Override
    void rotate() {
        orientation++;
        if (orientation > 3) {
            orientation = 0;
        }
    }

    @Override
    int orientation() {
        return  orientation;
    }

    @Override
    public String toString() {
        return "Visual Component of " + component.getType() + " X:" + X + " Y:" + Y;
    }
}
