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

public class Main extends Application {

    private final int W = 1000;
    private final int H = 1000;
    private final int gridCellNumber = 10;
    private final int gridCellWidth  = W / gridCellNumber;
    private final int gridCellHeight = H / gridCellNumber;

    public Circuit circuit = new Circuit();
    public ArrayList<VisualEntity> entityList = new ArrayList<>();
    public CircuitBuilder builder = new CircuitBuilder(entityList, circuit, gridCellWidth, gridCellHeight);

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
        builder.updateCircuit();

        scene.setOnMouseReleased(event -> {
            if (placingNow.equals(Placing.NOTHING)) {
                for (VisualEntity ve : entityList) {
                    if (ve.clickedOn) {

                        ve.clickedOn = false;
                        int eventX = (int) (event.getX());
                        int eventY = (int) (event.getY());

                        int cX = (eventX / gridCellWidth) * gridCellWidth;
                        int cY = (eventY / gridCellHeight) * gridCellHeight;

                        boolean validPosition = true;
                        for (VisualEntity ve2 : entityList) {
                            if (!ve2.equals(ve) && (ve2.X == cX) && (ve2.Y == cY)) {
                                validPosition = false;
//                                System.out.println("Invalid position with " + ve.toString());
                                break;
                            }
                        }
                        if (validPosition) {
                            ve.X = cX;
                            ve.Y = cY;

                            ve.refresh();
                            updateVisual();
//                            System.out.println("Released on " + ve.toString());
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
                for (VisualEntity ve2 : entityList) {
                    if ((ve2.X == cX) && (ve2.Y == cY)) {
                        validPosition = false;
//                        System.out.println("Invalid position with on X:" + cX + " Y:" + cY);
                        break;
                    }
                }
                if (validPosition) {
                    Component comp = getCurrentComponentSelection();
                    if (comp == null) {
                        newWireNode(root, cX, cY);
                    } else {
                        newComponent(root, comp, cX, cY);
                    }
                }
                updateVisual();
//                System.out.println("New component at X:" + cX + " Y:" + cY);
                placingNow = Placing.NOTHING;
            }
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case R:
                    for (VisualEntity ve : entityList) {
                        if (ve.clickedOn && ve.entityType.equals("component")) {
                            ve.rotateProperty().setValue(ve.rotateProperty().get() + 90);
                            ve.rotate();
                            ve.refresh();
                        }
                    }
                    break;
                case X:
                    System.out.println("you pressed X");
                    updateVisual();
                    builder.updateCircuit();
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
            case WIRE:
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

    VisualEntity getVisualEntity(int x, int y) {
        for (VisualEntity ve : entityList) {
            if (ve.X == x && ve.Y == y) {
//                System.out.println("getVisualEntity returned " + ve.toString());
                return ve;
            }
        }
//        System.out.println("No visualEntity with X:" + x + " Y:" + y + " found.");
        return null;
    }

    void newComponent(Group group, Component component, int x, int y) {
        VisualComponent vc = new VisualComponent(component, x, y);
        entityList.add(vc);
        group.getChildren().add(vc);
        vc.refresh();
        updateVisual();

        Scene scene = group.getScene();

        vc.setOnMouseEntered((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.HAND); //Change cursor to hand
        });

        vc.setOnMouseExited((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.DEFAULT); //Change cursor to pointer
        });

        vc.setOnMouseClicked((EventHandler<Event>) event -> {
            vc.clickedOn = true;
            System.out.println("Clicked on " + vc.toString());
        });
    }

    void newWireNode(Group group, int x, int y) {
        VisualWireNode ve = new VisualWireNode(x, y);
        entityList.add(ve);
        group.getChildren().add(ve);
        ve.refresh();
        updateVisual();

        Scene scene = group.getScene();

        ve.setOnMouseEntered((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.HAND); //Change cursor to hand
        });

        ve.setOnMouseExited((EventHandler<Event>) event -> {
            scene.setCursor(Cursor.DEFAULT); //Change cursor to pointer
        });

        ve.setOnMouseClicked((EventHandler<Event>) event -> {
            ve.clickedOn = true;
            System.out.println("Clicked on " + ve.toString());
        });
    }

    void updateVisual() {
        for (VisualEntity ve : entityList) {
            if (ve instanceof VisualWireNode) {
                ve.north = getVisualEntity(ve.X, ve.Y - gridCellHeight) != null;
                ve.east  = getVisualEntity(ve.X + gridCellWidth, ve.Y)  != null;
                ve.south = getVisualEntity(ve.X, ve.Y + gridCellHeight) != null;
                ve.west  = getVisualEntity(ve.X - gridCellWidth, ve.Y)  != null;
            }
            ve.refresh();
        }
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
