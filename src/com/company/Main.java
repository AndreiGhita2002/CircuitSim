package com.company;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private final int W = 1000;
    private final int H = 1000;

    private final int imageSize = 64;

    public Circuit circuit = new Circuit();
    public List<VisualComponent> visualComponents = new ArrayList<>();

    @Override
    public void start(Stage stage) {

        Group root = new Group();
        Scene scene = new Scene(root, W, H);
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        stage.setScene(scene);
        stage.show();

        // Testing things:
        newComponent(root, new LightBulb(), 200, 200);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
//                drawComponents(root);
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    System.out.println("\n weird error in animation timer");
//                }
            }
        };
    }

    void newComponent(Group group, Component component, int x, int y) {
        VisualComponent vc = new VisualComponent(component, x, y);
        visualComponents.add(vc);
        circuit.addComponent(vc.component);
        group.getChildren().add(vc);
        vc.refresh();
    }

    void drawComponents(Group group) {
//        for (VisualComponent vc : visualComponents) {
//
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
