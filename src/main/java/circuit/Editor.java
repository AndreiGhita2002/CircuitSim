package circuit;

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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Editor extends Application {

    static final int W = 800;
    static final int H = 800;
    static final int gridCellNumber = 12;
    static final int gridCellWidth = W / gridCellNumber;
    static final int gridCellHeight = H / gridCellNumber;
//    static final String examplesPath = "examples/";

    public static Circuit circuit;
    public static ArrayList<VisualEntity> entityList;
    public static CircuitBuilder builder;

    enum Placing {
        MOVE, WIRE, BATTERY, LIGHT, RESISTOR, SWITCH, ROTATE, DELETE, MODIFY
    }

    static Placing placingNow = Placing.MOVE;
    static Label infoLabel;
    static Label tooltipLabel;
    static TextField resistanceField;
    static TextField potentialField;
    static Label resistanceLabel;
    static Label potentialLabel;
    static Label resultLabel;
    static Group editorRoot;
    static Stage stg;

    @Override
    public void start(Stage stage) {

        circuit = new Circuit();
        entityList = new ArrayList<>();
        builder = new CircuitBuilder(entityList, circuit, gridCellWidth, gridCellHeight);

        infoLabel = new Label("");
        tooltipLabel = new Label();
        resistanceField = new TextField("");
        potentialField = new TextField("");
        resistanceLabel = new Label("Resistance:  in Ω");
        potentialLabel = new Label("EMF:         in V");
        resultLabel = new Label();
        editorRoot = new Group();

        stg = stage;
        SplitPane splitPane = new SplitPane();
        Scene scene = new Scene(splitPane, W + 100, H);
        Canvas editorCanvas = new Canvas(W, H);
        VBox menu = new VBox();

        // initializing the buttons
        ArrayList<Button> buttonList = initButtons();

        // initialising the menu
        splitPane.setOrientation(Orientation.HORIZONTAL);
        menu.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        infoLabel.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        infoLabel.setBorder(new Border(new BorderStroke(Color.WHEAT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
        infoLabel.setLayoutX(500);
        infoLabel.setLayoutY(500);
        tooltipLabel.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        tooltipLabel.setFont(Font.font(13));
        tooltipLabel.setLayoutX(200);
        tooltipLabel.setLayoutY(700);
        resistanceField.setVisible(false);
        potentialField.setVisible(false);
        resistanceLabel.setVisible(false);
        potentialLabel.setVisible(false);
        resultLabel.setLayoutX(100);
        resultLabel.setLayoutY(750);
        resultLabel.setVisible(false);

        // adding all the elements and making the stage visible
        splitPane.getItems().addAll(editorRoot, menu);
        editorRoot.getChildren().add(editorCanvas);
        editorRoot.getChildren().add(infoLabel);
        editorRoot.getChildren().add(resultLabel);
        editorRoot.getChildren().add(tooltipLabel);
        menu.getChildren().addAll(buttonList);
        menu.getChildren().add(new Label("\n\n\n"));
        menu.getChildren().add(resistanceLabel);
        menu.getChildren().add(resistanceField);
        menu.getChildren().add(potentialLabel);
        menu.getChildren().add(potentialField);
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
                            if (ve instanceof VisualComponent) {
                                ((VisualComponent) ve).component.current = 0.0;
                            }
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
                    if (comp != null) {
                        newComponent(comp, cX, cY);

                    } else if (placingNow.equals(Placing.WIRE)) {
                        newWireNode(cX, cY);
                    }
                }
            }
            updateVisual();
        });
        editorCanvas.setOnKeyReleased(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S:
                        save();
                        break;
                    case L:
                        load();
                        break;
                }
                updateVisual();
            }
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
        load("examples/start_example.circuit");
        refreshTooltip();
    }

    Component getCurrentComponentSelection() {
        switch (placingNow) {
            case BATTERY:
                return new Battery(12.0, 1.0);
            case LIGHT:
                return new Lamp(2.0);
            case RESISTOR:
                return new Resistor(4.0);
            case SWITCH:
                return new Switch(true);
        }
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
                ve.east = temp != null && (temp.orientation() == 1 || temp.orientation() == 3 || temp.orientation() == -1);
                temp = getVisualEntity(ve.X, ve.Y + gridCellHeight);
                ve.south = temp != null && (temp.orientation() == 2 || temp.orientation() == 0 || temp.orientation() == -1);
                temp = getVisualEntity(ve.X - gridCellWidth, ve.Y);
                ve.west = temp != null && (temp.orientation() == 3 || temp.orientation() == 1 || temp.orientation() == -1);
            }
            ve.refresh();
        }

        // hiding the text fields if not needed
        if (!placingNow.equals(Placing.MODIFY)) {
            resistanceField.setVisible(false);
            potentialField.setVisible(false);
            resistanceLabel.setVisible(false);
            potentialLabel.setVisible(false);
            resultLabel.setVisible(false);
        }
    }

    static void modify(VisualComponent vc) {
        // show the text field for resistance for all components
        resistanceField.setText(vc.component.resistance.toString());
        resistanceField.setVisible(true);
        resistanceLabel.setVisible(true);
        resistanceField.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                // processing the text from the text field
                vc.component.resistance = Double.parseDouble(resistanceField.getText());
            }
        });

        // also show the emf field only for batteries
        if (vc.component instanceof Battery) {
            potentialField.setText(vc.component.PD.toString());
            potentialField.setVisible(true);
            potentialLabel.setVisible(true);
            potentialField.setOnKeyReleased(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    // processing the text from the text field
                    vc.component.PD = Double.parseDouble(potentialField.getText());
                }
            });
        } else {
            potentialField.setVisible(false);
            potentialLabel.setVisible(false);
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

    ArrayList<Button> initButtons() {
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
            button.setOnAction(e -> {
                placingNow = Placing.values()[ci];
                refreshTooltip();
            });
            list.add(button);
        }

        // creating the save/load/clear/solve buttons
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> save());
        list.add(saveButton);

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> load());
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
//        System.out.println("Solving the circuit.....");
        if (builder.updateCircuit()) {
            circuit.solve();
            updateVisual();
            Editor.resultLabel.setBorder(new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS)));
            Editor.resultLabel.backgroundProperty().setValue(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
            Editor.resultLabel.setFont(Font.font(18));
            Editor.resultLabel.setText(" Solved! Mouse over a component to see its voltage and current values. ");
            Editor.resultLabel.setVisible(true);
        }
    }

    void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose save location");
        fileChooser.setInitialFileName("untitled.circuit");
        File file = fileChooser.showSaveDialog(stg);
        if (file == null) return;
        solveCircuit();
        builder.saveToFile(file);
    }

    void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select circuit file");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(stg);
        if (file == null) return;

        clear();
        builder.buildFromFile(file);

        List<VisualEntity> listCopy = builder.getEntityListCopy();
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

    void load(String fileName) {
        clear();
        builder.buildFromFile(fileName);

        List<VisualEntity> listCopy = builder.getEntityListCopy();
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

    static void refreshTooltip() {
        String str = "Current Selection: " + placingNow.toString() + "\n";
        switch (placingNow) {
            case BATTERY:
                str += "Places a BATTERY. Click on an empty space to place it. \n" +
                        "The battery can have internal resistance.";
                break;
            case LIGHT:
                str += "Places a LIGHTBULB. Click on an empty space to place it. \n" +
                        "The lightbulb lights up if current goes through it.";
                break;
            case RESISTOR:
                str += "Places a RESISTOR. Click on an empty space to place it. \n" +
                        "To modify its resistance value, you need to select it using the modify button. ";
                break;
            case SWITCH:
                str += "Places a SWITCH. Click on an empty space to place it. \n" +
                        "Right click on a placed switch to open/close it. ";
                break;
            case WIRE:
                str += "Places a WIRE. Click on an empty space to place it. \n" +
                        "Wires connect to adjacent wires or components";
                break;
            case MOVE:
                str += "MOVES a wire or a component. \n" +
                        "Click an object and then click an empty spot to place it";
                break;
            case ROTATE:
                str += "ROTATES a component. \n" +
                        "Click a component to rotate it by 90 degrees. ";
                break;
            case MODIFY:
                str += "MODIFIES a component. \n" +
                        "Click a component to modify its values. \n" +
                        "Remember to press ENTER after you've entered the new value to save it. ";
                break;
            case DELETE:
                str += "DELETES a component or a wire. \n" +
                        "Click an object to delete it. ";
                break;
            default:
                str += "Nothing selected";
        }
        tooltipLabel.setText(str);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
