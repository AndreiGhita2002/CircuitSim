package com.company;

import org.jgrapht.alg.cycle.StackBFSFundamentalCycleBasis;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm;
import org.jgrapht.graph.Multigraph;

import java.util.List;
import java.util.Set;

public class Circuit {

    // WireNodes are nodes and Components are edges
    Multigraph<WireNode, Component> graph = new Multigraph<>(Component.class);
    Set<List<Component>> cycles;

    void findLoops() {

        StackBFSFundamentalCycleBasis<WireNode, Component> cyclesAlg = new StackBFSFundamentalCycleBasis(graph);
        CycleBasisAlgorithm.CycleBasis<WireNode, Component> result = cyclesAlg.getCycleBasis();

        System.out.println("Cycles found: " + result.getCycles());
        cycles = result.getCycles();
    }

    void solve() {
        findLoops();

        //TODO solve the circuit
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
