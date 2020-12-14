package com.company;

import javafx.scene.Cursor;
import javafx.scene.effect.Glow;
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

        setOnMouseEntered(event -> {
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
        setOnMouseClicked(event -> {
            // if it gets right clicked

            if (event.getButton().equals(MouseButton.SECONDARY)) {
                // special behaviour for Switches:
                if (component instanceof Switch) {
                    // the switch gets flipped
                    component.closed = !component.closed;
                    Editor.updateVisual();
                } else {
                    // for all other components, the modify menu will become visible
                    Editor.modify(this);
                }

            } else switch (Editor.placingNow) {
                // when it's left clicked
                case MOVE:
                    clickedOn = true;
                    break;
                case ROTATING:
                    rotateOnce();
                    Editor.updateVisual();
                    break;
                case DELETE:
                    toDelete = true;
                    Editor.updateVisual();
                    break;
            }
        });
        refresh();
    }

    @Override
    void refresh() {
        // special behaviour for Switches
        if (component instanceof Switch) {
            String imageName;
            if (this.component.closed) {
                imageName = "Switch1.png";
            } else {
                imageName = "Switch0.png";
            }
            setImage(imageName);
        }

        // rotating the VisualComponent until it's the correct way
        this.rotateProperty().setValue(90 * orientation);

        // setting the glow
        Glow glow = new Glow();
        glow.setLevel(component.getBrightness());
        this.setEffect(glow);

        super.refresh();
    }

    @Override
    void rotateOnce() {
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
