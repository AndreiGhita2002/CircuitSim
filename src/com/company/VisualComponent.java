package com.company;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;

public class VisualComponent extends VisualEntity {

    Component component;
    private  int orientation; // the direction in which pin1 is going
    // 0 - north
    // 1 - east
    // 2 - south
    // 3 - west

    VisualComponent(Component component, int x, int y) {
        this(component, x, y, 0);
    }

    VisualComponent(Component component, int x, int y, int orientation) {
        this.component = component;
        this.X = x;
        this.Y = y;
        this.orientation = orientation;
        setImage(component.getType() + ".png");
        refresh();

        setOnMouseEntered((EventHandler<Event>) event -> {
            getScene().setCursor(Cursor.HAND); //Change cursor to hand
            Editor.infoLabel.setText(component.toLongString());
        });

        setOnMouseExited((EventHandler<Event>) event -> {
            getScene().setCursor(Cursor.DEFAULT); //Change cursor to pointer
            Editor.infoLabel.setText("");
        });

        setOnMouseClicked((EventHandler<Event>) event -> {
            if (Editor.placingNow.equals(Editor.Placing.NOTHING)) {
                clickedOn = true;
            } else if (Editor.placingNow.equals(Editor.Placing.ROTATING)) {
                rotate();
            } else if (Editor.placingNow.equals(Editor.Placing.DELETE)) {
                toDelete = true;
            }
        });
    }

    @Override
    void rotate() {
        this.rotateProperty().setValue(this.rotateProperty().get() + 90);
        orientation++;
        if (orientation > 3) {
            orientation = 0;
        }
        refresh();
    }

    @Override
    String toSaveFormat() {
        return X + " " + Y + " " + orientation + " " + component.toSaveFormat();
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
