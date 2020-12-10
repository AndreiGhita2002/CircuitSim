package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class VisualEntity extends ImageView {

    Integer X;
    Integer Y;
    boolean clickedOn = false;
    boolean toDelete = false;
    static int imageSize = 100;

    // if it's connected in that direction
    boolean north = false;
    boolean south = false;
    boolean west  = false;
    boolean east  = false;

    protected static final String folderURL = "file:resources/textures/";
    protected static final String defaultImage = "whales.png";

    protected void setImage(String imageName) {
        this.setImage(new Image(folderURL + imageName));
        this.setFitWidth(imageSize);
        this.setFitHeight(imageSize);
    }

    void rotate() {}

    String toSaveFormat() {return null;}

    int orientation() {
        return -1;
    }

    void setOrientation(int o) {}

    void refresh() {
        relocate(X, Y);
    }
}
