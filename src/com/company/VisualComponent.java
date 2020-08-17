package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualComponent extends ImageView {

    Component component;
    Integer X;
    Integer Y;

    private static final String folderURL = "file:resources/textures/";
    private static final String defaultImage = "whales.png";

    protected void setImage(String imageName) {
        this.setImage(new Image(folderURL + imageName));
    }

    void refresh() {
        relocate(X, Y);
    }

    VisualComponent(Component component, int x, int y) {
        this.component = component;
        this.X = x;
        this.Y = y;

        try {
            setImage(component.getType() + ".png");
        } catch (Exception e) {
            setImage(defaultImage);
            System.out.println("got exception in VisualComponent()");
        }
    }
}
