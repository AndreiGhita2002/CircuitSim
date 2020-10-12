package com.company;

public class VisualWireNode extends VisualEntity {

    boolean highlighted = false;

    VisualWireNode(int x, int y) {
        this.X = x;
        this.Y = y;
        refresh();
    }

    @Override
    void refresh() {
        String direction = "";
        if (north) direction += "N";
        if (south) direction += "S";
        if (east)  direction += "E";
        if (west)  direction += "W";

        setImage("Wire" + direction + ".png");
        highlighted = false;
        relocate(X, Y);
    }

    void highlight() {
        setImage("whales.png");
        highlighted = true;
    }

    @Override
    public String toString() {
        return "Visual WireNode of wireNode X:" + X + " Y:" + Y;
    }
}
