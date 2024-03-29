package circuit;

import Jama.Matrix;
import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Circuit {

    // WireNodes are nodes and Components are edges
    Multigraph<WireNode, Component> graph = new Multigraph<>(Component.class);
    List<List<Component>> cycles;

    // maximum current present, or lamp purposes
    static double maxCurrent;

    void findLoops() {
        StackBFSFundamentalCycleBasis<WireNode, Component> cyclesAlg = new StackBFSFundamentalCycleBasis<>(graph);
        List<List<Component>> results = new ArrayList<>(cyclesAlg.getCycleBasis().getCycles());

        cycles = new ArrayList<>(results);
    }

    void solve() {
        findLoops();
        int n = cycles.size();

        //declaring matrices for resistance and voltage
        Matrix resistance = new Matrix(n, n);
        Matrix voltages = new Matrix(n, 1);

        for (int i = 0; i < n; i++) {
            int voltSum = 0;
            for (int j = 0; j < n; j++) {
                int tempSum = 0;
                if (i == j) {
                    for (Component comp : cycles.get(i)) {
                        tempSum += comp.resistance;
                        if (comp.getType().equals("Battery")) voltSum += comp.PD;
                    }
                } else {
                    for (Component comp : findCommonComponents(cycles.get(i), cycles.get(j))) {
                        tempSum += comp.resistance;
                    }
                }
                resistance.set(i, j, tempSum);
            }
            voltages.set(i, 0, voltSum);
        }

        // solving the matrix
        // resistance * currents = voltages
        Matrix currents = resistance.solve(voltages);

        // new we need to change the current of all components
        // each cycle has it's own current
        // so the current of a component is the sum of all the cycles that include it
        resetCurrents();
        for (int i = 0; i < cycles.size(); i++) {
            List<Component> cycle = cycles.get(i);
            for (Component comp : cycle) {
                comp.current += currents.get(i, 0);
            }
        }

        // telling all the components to calculate their voltages
        // and calculating max current
        for (VisualEntity ve : Editor.entityList) {
            if (ve instanceof VisualComponent) {
                ((VisualComponent) ve).component.calculateVoltage();

                // calculating max current
                if (((VisualComponent) ve).component.current > maxCurrent)
                    maxCurrent = ((VisualComponent) ve).component.current;
            }
        }
    }

    List<Component> findCommonComponents(List<Component> cycle1, List<Component> cycle2) {
        List<Component> out = new ArrayList<>();
        for (Component comp1 : cycle1) {
            for (Component comp2 : cycle2) {
                if (comp1.equals(comp2)) {
                    out.add(comp1);
                }
            }
        }
        return out.stream().distinct().collect(Collectors.toList());
    }

    void resetCurrents() {
        for (VisualEntity ve : Editor.entityList) {
            if (ve instanceof VisualComponent) {
                ((VisualComponent) ve ).component.current = 0.0;
            }
        }
    }

    void addWireNode(WireNode wireNode) {
        graph.addVertex(wireNode);
    }

    WireNode getNodeWithID(int id) {
        for (WireNode wire : graph.vertexSet()) {
            if (wire.ID == id) return wire;
        }
        System.out.println("Circuit.getNodeWithID() didn't find any nodes with ID: " + id);
        return null;
    }

    public String toString() {
        return graph.toString();
    }

    void reset() {
        graph = new Multigraph<>(Component.class);
        cycles = null;
    }
}
