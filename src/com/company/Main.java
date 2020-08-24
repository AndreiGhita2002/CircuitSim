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

    enum Placing {
        NOTHING, WIRE, BATTERY, LIGHT, RESISTOR, SWITCH
    }

    Placing placingNow = Placing.NOTHING;

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
            if (placingNow.equals(Placing.NOTHING)) {
                for (VisualComponent vc : visualComponents) {
                    if (vc.clickedOn) {

                        vc.clickedOn = false;
                        int eventX = (int) (event.getX());
                        int eventY = (int) (event.getY());

                        int cX = (eventX / gridCellWidth) * gridCellWidth;
                        int cY = (eventY / gridCellHeight) * gridCellHeight;

                        boolean validPosition = true;
                        for (VisualComponent vc2 : visualComponents) {
                            if (!vc2.equals(vc) && (vc2.X == cX) && (vc2.Y == cY)) {
                                validPosition = false;
                                System.out.println("Invalid position with " + vc.toString());
                                break;
                            }
                        }
                        if (validPosition) {
                            vc.X = cX;
                            vc.Y = cY;

                            vc.refresh();
                            System.out.println("Released on " + vc.toString());
                        }
                        break;
                    }
                }
            } else {
                int eventX = (int) (event.getX());
                int eventY = (int) (event.getY());

                int cX = (eventX / gridCellWidth) * gridCellWidth;
                int cY = (eventY / gridCellHeight) * gridCellHeight;

                boolean validPosition = true;
                for (VisualComponent vc2 : visualComponents) {
                    if ((vc2.X == cX) && (vc2.Y == cY)) {
                        validPosition = false;
                        System.out.println("Invalid position with on X:" + cX + " Y:" + cY);
                        break;
                    }
                }
                if (validPosition) newComponent(root, getCurrentComponentSelection(), cX, cY);

                System.out.println("New component at X:" + cX + " Y:" + cY);
                placingNow = Placing.NOTHING;
            }
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case R:
                    for (VisualComponent vc : visualComponents) {
                        if (vc.clickedOn) {
                            vc.rotateProperty().setValue(vc.rotateProperty().get() + 90);
                            vc.refresh();
                        }
                    }
                    break;
//                case A:
//                    System.out.println(scene.cursorProperty().toString());
//
////                    newComponent(root, getComponentFromEnum(placingNow), );
//                    break;
                case X:
                    System.out.println("you pressed X");
                    break;
                case DIGIT0:
                    placingNow = Placing.NOTHING;
                    break;
                case DIGIT1:
                    placingNow = Placing.WIRE;
                    break;
                case DIGIT2:
                    placingNow = Placing.BATTERY;
                    break;
                case DIGIT3:
                    placingNow = Placing.LIGHT;
                    break;
                case DIGIT4:
                    placingNow = Placing.RESISTOR;
                    break;
                case DIGIT5:
                    placingNow = Placing.SWITCH;
                    break;

            }
        });

        // Testing things:
        newComponent(root, new Battery(12.0), 200, 200);
        newComponent(root, new LightBulb(5.0), 500, 300);
    }

    Component getCurrentComponentSelection() {
        switch (placingNow) {
            case NOTHING:
                return null;
            case WIRE:
                //TODO place wire
                return null;
            case BATTERY:
                return new Battery(12.0);
            case LIGHT:
                return new LightBulb(0.0);
            case RESISTOR:
                return new Resistor(3.0);
            case SWITCH:
                return new Switch();
        }
        System.out.println("this shouldn't have happened in getComponentFromEnum()");
        return null;
    }

    void newComponent(Group group, Component component, int x, int y) {
        VisualComponent vc = new VisualComponent(component, x, y);
        visualComponents.add(vc);
        circuit.addComponent(vc.component);
        group.getChildren().add(vc);
        vc.refresh();
        updateCircuit();

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

    void updateCircuit() {

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
