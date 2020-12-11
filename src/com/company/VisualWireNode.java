package com.company;

import javafx.scene.Cursor;

public class VisualWireNode extends VisualEntity {

    boolean highlighted = false;

    VisualWireNode(int x, int y) {
        this.X = x;
        this.Y = y;
        refresh();

        setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND); //Change cursor to hand
        });

        setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT); //Change cursor to pointer
        });

        setOnMouseClicked(event -> {
            if (Editor.placingNow.equals(Editor.Placing.MOVE)) {
                clickedOn = true;
            } else if (Editor.placingNow.equals(Editor.Placing.DELETE)) {
                toDelete = true;
                Editor.updateVisual();
            }
        });
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
        //TODO change the texture slightly when highlighted
        highlighted = true;
    }

    @Override
    public String toString() {
        return "Visual WireNode of wireNode X:" + X + " Y:" + Y;
    }
}
