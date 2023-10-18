package Simulation;

import Graph.Graph;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class BitSetSimulation implements ISimulation {
    public Graph graph;
    private final Report report;
    private final int infectionTime;
    private final float infectionChance;
    private int timePassed = 0;
    private final Random random = new Random();
    private BitSet prev, next;
    private final BitSet  isImmune, isVaccinated, hasBeenInfected;
    private final int[] infectionCounters;



    public BitSetSimulation(Graph graph, int infectionTime, float infectionChance) {
        this.graph = graph;
        this.report = new Report();
        this.infectionTime = infectionTime;
        this.infectionChance = infectionChance;
        prev = new BitSet(graph.vertecies);
        next = new BitSet(graph.vertecies);
        isImmune = new BitSet(graph.vertecies);
        isVaccinated = new BitSet(graph.vertecies);
        hasBeenInfected = new BitSet(graph.vertecies);
        infectionCounters = new int[graph.vertecies];
    }

    public int getTimePassed() {
        return timePassed;
    }

    private void setupInfect(int p) {
        prev.set(p);
        next.set(p);
        infectionCounters[p] = infectionTime;
    }
    public void initialize(Function<Integer, Boolean> f) {
        int total = 0;
        for (int p = 0; p < graph.vertecies; p++) {
            if (f.apply(p)) {
                total++;
                setupInfect(p);
            }
        }
        report.newCases.add(total);
        report.cumulative.add(total);
        report.current.add(total);
    }

    private boolean isInfected(int p) {
        return prev.get(p);
    }

    private void infect(int p) {
        infectionCounters[p] = infectionTime;
        next.set(p);
        hasBeenInfected.set(p);
    }

    private boolean infectPop(int p) {
        if (isInfected(p) || isImmune.get(p)) return false;
        if (random.nextDouble() < infectionChance) {
            infect(p);
            return true;
        }
        return false;
    }

    private void doStep() {
        this.timePassed++;
        // report.realCumulative.add(population.stream().mapToInt(p -> p.isInfected() ? 1 : 0).sum());
        report.realCumulative.add(0);
        int newInfected = 0;
        int[] neighbours = graph.getAllNeighbours();


        for (int i = 0; i < graph.vertecies; i++) {
            infectionCounters[i]--;
        }

        for (int i = 0; i < graph.vertecies; i++) {
            isImmune.set(i, prev.get(i) && infectionCounters[i] <= 0);
        }

        BitSet temp = prev;
        prev = next;
        next = temp;

        for (int i = 0; i < graph.vertecies; i++) {
            next.set(i, infectionCounters[i] > 0);
        }

        for (int p = 0; p < graph.vertecies; p++) {
            if (isInfected(p)) continue;
            for (int i = graph.getStart(p); i < graph.getEnd(p); i++) {
                if (infectPop(neighbours[i])) {newInfected++;}
            }
        }

        report.cumulative.add(report.cumulative.get(report.cumulative.size() - 1) + newInfected);
        report.newCases.add(newInfected);
        report.current.add(countInfected());
    }

    private int countInfected() {
        return prev.cardinality();
    }

    public Report simulate(int steps) {
        for (int i = 0; i < steps; i++) {
            doStep();
        }
        return report;
    }

    public BitSet getPrev() {
        return (BitSet) prev.clone();
    }

    public BitSet getNext() {
        return (BitSet) next.clone();
    }

    public BitSet getIsImmune() {
        return (BitSet) isImmune.clone();
    }

    public BitSet getIsVaccinated() {
        return (BitSet) isVaccinated.clone();
    }

    public BitSet getHasBeenInfected() {
        return (BitSet) hasBeenInfected.clone();
    }

    public Graph getGraph() {
        return graph;
    }

    public void vaccinate(int i) {
        isImmune.set(i);
        isVaccinated.set(i);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void setGraph(Graph G) {
        this.graph = G;
    }
}
