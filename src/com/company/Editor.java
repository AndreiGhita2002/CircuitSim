package com.company;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
        WIRE, BATTERY, LIGHT, RESISTOR, SWITCH, ROTATING, DELETE, NOTHING
    }
    static Placing placingNow = Placing.NOTHING;
    static Label infoLabel = new Label("");
    static Group editorRoot = new Group();

    @Override
    public void start(Stage stage) {
        SplitPane splitPane = new SplitPane();
        Scene scene = new Scene(splitPane, W + 200, H);
        Canvas editorCanvas = new Canvas(W, H);
        GraphicsContext editorGC = editorCanvas.getGraphicsContext2D();
        VBox menu = new VBox();

        // initializing the buttons
        ArrayList<Button> buttonList = initButtons();

        // initialising the menu
        menu.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

        infoLabel.setLayoutX(500);
        infoLabel.setLayoutY(500);

        // adding all the elements and making the stage visible
        splitPane.getItems().addAll(editorRoot, menu);
        editorRoot.getChildren().add(editorCanvas);
        menu.getChildren().addAll(buttonList);
        menu.getChildren().add(infoLabel);
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
                        newWireNode(cX, cY);
                    } else {
                        newComponent(comp, cX, cY);
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
                            ve.rotate();
                        }
                    }
                    updateVisual();
                    break;
                case X:
                    solveCircuit();
                    break;
                case S:
                    save("/Users/andrei/Desktop/test.txt");
                    break;
                case L:
                    load("/Users/andrei/Desktop/test.txt");
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
        newComponent(new Battery(12.0), 200, 200);
        newComponent(new LightBulb(5.0), 400, 300);
        newWireNode(400, 200);
        newWireNode(500, 300);
        newWireNode(300, 300);
        newWireNode(200, 300);
        newWireNode(200, 500);
    }

    Component getCurrentComponentSelection() {
        switch (placingNow) {
            case NOTHING:
            case WIRE:
                return null;
            case BATTERY:
                return new Battery(12.0, 1.0);
            case LIGHT:
                return new LightBulb(2.0);
            case RESISTOR:
                return new Resistor(4.0);
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

    void newComponent(Component component, int x, int y) {
        newComponent(new VisualComponent(component, x, y));

    }

    void newComponent(VisualComponent vc) {
        entityList.add(vc);
        editorRoot.getChildren().add(vc);
        vc.refresh();
        updateVisual();
    }

    void newWireNode(int x, int y) {
        newWireNode(new VisualWireNode(x, y));

    }

    void newWireNode(VisualWireNode ve) {
        entityList.add(ve);
        editorRoot.getChildren().add(ve);
        ve.refresh();
        updateVisual();
    }

    void updateVisual() {
        ArrayList<VisualEntity> delete = new ArrayList<>();

        for (VisualEntity ve : entityList) {
            if (ve.toDelete) delete.add(ve);

            if (ve instanceof VisualWireNode) {
                VisualEntity temp = getVisualEntity(ve.X, ve.Y - gridCellHeight);
                ve.north = temp != null && (temp.orientation() == 0 || temp.orientation() == 2 || temp.orientation() == -1);
                temp = getVisualEntity(ve.X + gridCellWidth, ve.Y);
                ve.east  = temp != null && (temp.orientation() == 1 || temp.orientation() == 3 || temp.orientation() == -1);
                temp = getVisualEntity(ve.X, ve.Y + gridCellHeight);
                ve.south = temp != null && (temp.orientation() == 2 || temp.orientation() == 0 || temp.orientation() == -1);
                temp = getVisualEntity(ve.X - gridCellWidth, ve.Y);
                ve.west  = temp != null && (temp.orientation() == 3 || temp.orientation() == 1 || temp.orientation() == -1);
            }
            ve.refresh();
        }
        editorRoot.getChildren().removeAll(delete);
        entityList.removeAll(delete);
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
//        final int width  = 100;
//        final int height = 100;

        for (int i = 0; i < Placing.values().length; i++) {
            String str = Placing.values()[i].toString();
            Button button = new Button(str);

            int ci = i;
            button.setOnAction(e -> placingNow = Placing.values()[ci]);
            list.add(button);
        }
        return list;
    }

    void solveCircuit() {
        System.out.println("Solving the circuit.....");
        updateVisual();
        builder.updateCircuit();
        System.out.println(circuit);
        System.out.println();
        circuit.solve();
    }

    void save(String path) {
        builder.saveToFile(path);

    }

    void load(String path) {
        builder.buildFromFile(path);

        System.out.println(builder.entityList);

        ArrayList<Node> toRemove = new ArrayList<>();
        for (int i = 0; i < editorRoot.getChildren().size(); i++) {
            if (editorRoot.getChildren().get(i) instanceof VisualEntity) {
                toRemove.add(editorRoot.getChildren().get(i));
            }
        }
        editorRoot.getChildren().removeAll(toRemove);

        for (VisualEntity ve : entityList) {

            //TODO fix this
            // should make a copy of entity list

            if (ve instanceof VisualComponent) {
                newComponent((VisualComponent) ve);
            } else if (ve instanceof VisualWireNode) {
                newWireNode((VisualWireNode) ve);
            }
        }
        updateVisual();
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
