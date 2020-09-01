package com.company;

public class VisualWireNode extends VisualEntity {

    WireNode wireNode;

    VisualWireNode(WireNode wire, int x, int y) {
        this.wireNode = wire;
        this.X = x;
        this.Y = y;
        this.entityType = "wire";

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
        relocate(X, Y);
    }

    @Override
    public String toString() {
        return "Visual WireNode of wireNode " + wireNode.ID + " X:" + X + " Y:" + Y;
    }
}
