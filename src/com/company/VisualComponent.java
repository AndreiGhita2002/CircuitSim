package com.company;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;

public class VisualComponent extends VisualEntity {

    Component component;
    int orientation; // the direction in which pin1 is going
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
            if (this.X >= Editor.W - Editor.gridCellWidth) {
                // if it is in the right most column; displayed to the left
                Editor.infoLabel.relocate(this.X - Editor.gridCellWidth, this.Y);
            } else if (this.X <= Editor.gridCellWidth) {
                // if it is in the left most column; displayed to the right
                Editor.infoLabel.relocate(this.X + Editor.gridCellWidth, this.Y);
            } else if (this.Y >= Editor.H - Editor.gridCellHeight) {
                // if it is in the bottom row; displayed above
                Editor.infoLabel.relocate(this.X, this.Y - Editor.gridCellHeight);
            } else {
                // anywhere else; displayed below
                Editor.infoLabel.relocate(this.X, this.Y + Editor.gridCellHeight);
            }
            Editor.infoLabel.setText(component.toLongString());
            Editor.infoLabel.toFront();
        });

        setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT); //Change cursor to pointer
            Editor.infoLabel.setText("");
        });

        // adding special behaviour for Switch components
        if (component instanceof Switch) {
            setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    component.closed = !component.closed;
                    Editor.updateVisual();
                } else {
                    if (Editor.placingNow.equals(Editor.Placing.MOVE)) {
                        clickedOn = true;
                    } else if (Editor.placingNow.equals(Editor.Placing.ROTATING)) {
                        rotate();
                        Editor.updateVisual();
                    } else if (Editor.placingNow.equals(Editor.Placing.DELETE)) {
                        toDelete = true;
                        Editor.updateVisual();
                    }
                }
            });
        } else { // default clicking behaviour
            setOnMouseClicked((EventHandler<Event>) event -> {
                if (Editor.placingNow.equals(Editor.Placing.MOVE)) {
                    clickedOn = true;
                } else if (Editor.placingNow.equals(Editor.Placing.ROTATING)) {
                    rotate();
                    Editor.updateVisual();
                } else if (Editor.placingNow.equals(Editor.Placing.DELETE)) {
                    toDelete = true;
                    Editor.updateVisual();
                }
            });
        }
    }

    void updateImage() {
        if (component instanceof Switch) {
            String imageName;
            if (this.component.closed) {
                imageName = "Switch1.png";
            } else {
                imageName = "Switch0.png";
            }
            setImage(imageName);
        }
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
        return X / Editor.gridCellWidth + " " + Y / Editor.gridCellHeight + " " + orientation + " " + component.toSaveFormat();
    }

    @Override
    int orientation() {
        return  orientation;
    }

    void setOrientation(int o) {
        orientation = o;
    }

    @Override
    public String toString() {
        return "Visual Component of " + component.getType() + " X:" + X + " Y:" + Y;
    }
}
