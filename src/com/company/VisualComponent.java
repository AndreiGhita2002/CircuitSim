package com.company;

public class VisualComponent extends VisualEntity {

    Component component;

    VisualComponent(Component component, int x, int y) {
        this.component = component;
        this.X = x;
        this.Y = y;
        this.entityType = "component";

        setImage(component.getType() + ".png");
        refresh();
    }

    @Override
    public String toString() {
        return "Visual Component of " + component.getType() + " X:" + X + " Y:" + Y;
    }
}
