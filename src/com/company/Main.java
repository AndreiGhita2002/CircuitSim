package com.company;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private final int W = 1000;
    private final int H = 1000;
    private final int gridCellNumber = 10;
    private final int gridCellWidth  = W / gridCellNumber;
    private final int gridCellHeight = H / gridCellNumber;

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

        drawGrid(gc);

        scene.setOnMouseReleased(event -> {
            for (VisualComponent vc : visualComponents) {
                if (vc.clickedOn) {
                    vc.clickedOn = false;
                    int eventX = (int) (event.getX());
                    int eventY = (int) (event.getY());

                    vc.X = (eventX / gridCellWidth) * gridCellWidth;
                    vc.Y = (eventY / gridCellHeight) * gridCellHeight;

                    vc.refresh();
                    System.out.println("Released on " + vc.toString());
                }
            }
        });

        // Testing things:
        newComponent(root, new Battery(), 200, 200);
        newComponent(root, new LightBulb(), 500, 300);
    }

    void newComponent(Group group, Component component, int x, int y) {
        VisualComponent vc = new VisualComponent(component, x, y);
        visualComponents.add(vc);
        circuit.addComponent(vc.component);
        group.getChildren().add(vc);
        vc.refresh();

        Scene scene = group.getScene();

        vc.setOnMouseEntered((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.HAND); //Change cursor to hand
        });

        vc.setOnMouseExited((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.DEFAULT); //Change cursor to hand
        });

        vc.setOnMouseClicked((EventHandler<Event>) event -> {
            vc.clickedOn = true;
            System.out.println("Clicked on " + vc.toString());
        });
    }

    void drawGrid(GraphicsContext gc) {
        // drawing the background
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, W, H);

        // drawing the grid
        gc.setStroke(Color.LIGHTGOLDENRODYELLOW);

        // for vertical lines
        for (int i = 0; i < W; i += gridCellWidth) {
            gc.strokeLine(i, 0, i, H);
        }

        // for horizontal lines
        for (int i = 0; i < H; i += gridCellHeight) {
            gc.strokeLine(0, i, W, i);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
