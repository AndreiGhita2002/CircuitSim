package com.company;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Editor extends Application {

    static final int W = 1000;
    static final int H = 1000;
    static final int gridCellNumber = 10;
    static final int gridCellWidth  = W / gridCellNumber;
    static final int gridCellHeight = H / gridCellNumber;

    public static Circuit circuit = new Circuit();
    public static ArrayList<VisualEntity> entityList = new ArrayList<>();
    public static CircuitBuilder builder = new CircuitBuilder(entityList, circuit, gridCellWidth, gridCellHeight);

    enum Placing {
        WIRE, BATTERY, LIGHT, RESISTOR, SWITCH, NOTHING
    }
    Placing placingNow = Placing.NOTHING;

    @Override
    public void start(Stage stage) {
        SplitPane splitPane = new SplitPane();
        Scene scene = new Scene(splitPane, W + 200, H);
        Group editorRoot = new Group();
        Canvas editorCanvas = new Canvas(W, H);
        GraphicsContext editorGC = editorCanvas.getGraphicsContext2D();
        VBox menu = new VBox();

        // initializing the buttons
        ArrayList<Button> buttonList = initButtons();

        // initialising the menu
//        menu.backgroundProperty().setValue(new BackgroundFill());

        // adding all the elements and making the stage visible
        splitPane.getItems().addAll(editorRoot, menu);
        editorRoot.getChildren().add(editorCanvas);
        menu.getChildren().addAll(buttonList);
        stage.setTitle("CircuitSim");
        stage.setScene(scene);
        stage.show();

        drawGrid(editorGC);
        builder.updateCircuit();

        editorCanvas.setOnMouseReleased(event -> {
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
                                break;
                            }
                        }
                        if (validPosition) {
                            ve.X = cX;
                            ve.Y = cY;

                            ve.refresh();
                            updateVisual();
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
                        break;
                    }
                }
                if (validPosition) {
                    Component comp = getCurrentComponentSelection();
                    if (comp == null) {
                        newWireNode(editorRoot, cX, cY);
                    } else {
                        newComponent(editorRoot, comp, cX, cY);
                    }
                }
                updateVisual();
            }
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case R:
                    for (VisualEntity ve : entityList) {
                        if (ve.clickedOn && ve instanceof VisualComponent) {
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
                    System.out.println(circuit);
                    System.out.println();
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

        // closing the application
        stage.setOnCloseRequest(event -> {
            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Testing things:
        newComponent(editorRoot, new Battery(12.0), 200, 200);
        newComponent(editorRoot, new LightBulb(5.0), 500, 300);
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
                return ve;
            }
        }
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

    ArrayList<Button> initButtons() {
        ArrayList<Button> list = new ArrayList<>();
        final int width  = 100;
        final int height = 100;

        for (int i = 0; i < Placing.values().length; i++) {
            String str = Placing.values()[i].toString();
            Button button = new Button(str);

            int ci = i;
            button.setOnAction(e -> placingNow = Placing.values()[ci]);
            list.add(button);
        }
        return list;
    }

    public static void main(String[] args) {
        if (Arrays.asList(args).size() != 0) {
            System.out.println("launching with args: " + Arrays.toString(args));
            try {
                builder.buildFromFile(args[0]);
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }
        launch(args);
    }
}
