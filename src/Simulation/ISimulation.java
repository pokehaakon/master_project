package Simulation;

import Graph.Graph;

import java.util.function.Function;

public interface ISimulation {
    int getTimePassed();
    void initialize(Function<Integer, Boolean> f);
    Report simulate(int steps);
    Graph getGraph();
    void vaccinate(int i);
    boolean isDone();
    void setGraph(Graph G);
}
