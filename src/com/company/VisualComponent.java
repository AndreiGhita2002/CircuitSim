package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualComponent extends ImageView {

    Component component;
    Integer X;
    Integer Y;
    boolean clickedOn = false;
    static int imageSize = 100;

    private static final String folderURL = "file:resources/textures/";
    private static final String defaultImage = "whales.png";

    protected void setImage(String imageName) {
        this.setImage(new Image(folderURL + imageName));
        this.setFitWidth(imageSize);
        this.setFitHeight(imageSize);
        this.refresh();
    }

    void refresh() {
        relocate(X, Y);
    }

    VisualComponent(Component component, int x, int y) {
        this.component = component;
        this.X = x;
        this.Y = y;

        setImage(component.getType() + ".png");
    }

    @Override
    public String toString() {
        return "Visual Component of " + component.getType() + " X:" + X + " Y:" + Y;
    }
}
