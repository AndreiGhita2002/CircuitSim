package com.company;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Editor extends Application {

    static final int W = 800;
    static final int H = 800;
    static final int gridCellNumber = 8;
    static final int gridCellWidth  = W / gridCellNumber;
    static final int gridCellHeight = H / gridCellNumber;
    static final String examplesPath = "./resources/examples/";

    public static Circuit circuit = new Circuit();
    public static ArrayList<VisualEntity> entityList = new ArrayList<>();
    public static CircuitBuilder builder = new CircuitBuilder(entityList, circuit, gridCellWidth, gridCellHeight);

    enum Placing {
        WIRE, BATTERY, LIGHT, RESISTOR, SWITCH, ROTATING, DELETE, MOVE
    }
    static Placing placingNow = Placing.MOVE;
    static Label infoLabel = new Label("");
    static Group editorRoot = new Group();

    @Override
    public void start(Stage stage) {
        SplitPane splitPane = new SplitPane();
        Scene scene = new Scene(splitPane, W, H + 50);
        Canvas editorCanvas = new Canvas(W, H);
        HBox menu = new HBox();

        // initializing the buttons
        ArrayList<Button> buttonList = initButtons(stage);

        // initialising the menu
        splitPane.setOrientation(Orientation.VERTICAL);
        menu.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        infoLabel.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        infoLabel.setBorder(new Border(new BorderStroke(Color.WHEAT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        infoLabel.setLayoutX(500);
        infoLabel.setLayoutY(500);

        // adding all the elements and making the stage visible
        splitPane.getItems().addAll(editorRoot, menu);
        editorRoot.getChildren().add(editorCanvas);
        editorRoot.getChildren().add(infoLabel);
        menu.getChildren().addAll(buttonList);
        stage.setTitle("CircuitSim");
        stage.setScene(scene);
        stage.show();

        drawGrid(editorCanvas.getGraphicsContext2D());
        builder.updateCircuit();

        editorCanvas.setOnMouseReleased(event -> {
            if (placingNow.equals(Placing.MOVE)) {
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
            }
            updateVisual();
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case R:
                    for (VisualEntity ve : entityList) {
                        if (ve.clickedOn && ve instanceof VisualComponent) {
                            ve.rotate();
                        }
                    }
                    break;
                case X:
                    solveCircuit();
                    break;
                case S:
                    save(stage);
                    break;
                case L:
                    load(stage);
                    break;
                case DIGIT0:
                    placingNow = Placing.MOVE;
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
            updateVisual();
        });
        splitPane.setOnMouseEntered(event -> scene.setCursor(Cursor.DEFAULT));
        splitPane.setOnMouseExited(event -> scene.setCursor(Cursor.DEFAULT));

        // closing the application
        stage.setOnCloseRequest(event -> {
            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // loading the starting circuit
        load(new File(examplesPath + "start_example.circuit"));
        //TODO add an option to choose from the other examples
        // maybe like in the menu bar
    }

    Component getCurrentComponentSelection() {
        switch (placingNow) {
            case MOVE:
            case DELETE:
            case ROTATING:
            case WIRE:
                return null;
            case BATTERY:
                return new Battery(12.0, 1.0);
            case LIGHT:
                return new LightBulb(2.0);
            case RESISTOR:
                return new Resistor(4.0);
            case SWITCH:
                return new Switch(true);
        }
        System.out.println("this shouldn't happened in getCurrentComponentSelection()");
        return null;
    }

    static VisualEntity getVisualEntity(int x, int y) {
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
        updateVisual();
    }

    void newWireNode(int x, int y) {
        newWireNode(new VisualWireNode(x, y));

    }

    void newWireNode(VisualWireNode ve) {
        entityList.add(ve);
        editorRoot.getChildren().add(ve);
        updateVisual();
    }

    static void updateVisual() {
        ArrayList<VisualEntity> delete = new ArrayList<>();

        // deleting all entities marked for deletion
        for (VisualEntity ve : entityList) {
            if (ve.toDelete) delete.add(ve);
        }
        editorRoot.getChildren().removeAll(delete);
        entityList.removeAll(delete);

        for (VisualEntity ve : entityList) {
            // updating the wireNodes
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

            // updating components
            if (ve instanceof VisualComponent) {
                ((VisualComponent) ve).updateImage();
            }
            ve.refresh();
        }
    }

    void drawGrid(GraphicsContext gc) {
        // drawing the background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, W, H);

        // drawing the grid
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);

        // for vertical lines
        for (int i = 0; i < W; i += gridCellWidth) {
            gc.strokeLine(i, 0, i, H);
        }

        // for horizontal lines
        for (int i = 0; i < H; i += gridCellHeight) {
            gc.strokeLine(0, i, W, i);
        }
    }

    ArrayList<Button> initButtons(Stage stage) {
        ArrayList<Button> list = new ArrayList<>();

        // initialising placing buttons
        for (int i = 0; i < Placing.values().length; i++) {
            String str = Placing.values()[i].toString();

            // turning the string to lowercase, then capitalising the first char
            str = str.toLowerCase();
            String firstChar = "" + str.charAt(0);
            str = str.replaceFirst(firstChar, firstChar.toUpperCase());

            Button button = new Button(str);

            int ci = i;
            button.setOnAction(e -> placingNow = Placing.values()[ci]);
            list.add(button);
        }

        // creating the save/load/clear/solve buttons
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> save(stage));
        list.add(saveButton);

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> load(stage));
        list.add(loadButton);

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clear());
        list.add(clearButton);

        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solveCircuit());
        list.add(solveButton);

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

    void save(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose save location");
        fileChooser.setInitialFileName("untitled.circuit");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        builder.saveToFile(file);
    }

    void load(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select circuit file");
        File file = fileChooser.showOpenDialog(stage);
        load(file);
    }

    void load(File file) {
        if (file == null) return;

        clear();
        builder.buildFromFile(file);

        List<VisualEntity> listCopy = builder.getEntityListCopy();
        System.out.println(entityList);
        entityList.clear();

        for (VisualEntity ve : listCopy) {
            if (ve instanceof VisualComponent) {
                newComponent((VisualComponent) ve);
            } else if (ve instanceof VisualWireNode) {
                newWireNode((VisualWireNode) ve);
            }
        }
        updateVisual();
    }

    void clear() {
        ArrayList<Node> toRemove = new ArrayList<>();

        for (Node node : editorRoot.getChildren()) {
            if (node instanceof VisualEntity) {
                toRemove.add(node);
            }
        }

        editorRoot.getChildren().removeAll(toRemove);
        entityList.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
