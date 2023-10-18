package Simulation;

import Graph.Graph;
import Tools.Eccentricity;

import java.util.Random;
import java.util.function.Function;

public class Simulation implements ISimulation {
    public final IPop[] population;
    public Graph graph;
    private final Report report;
    private final int infectionTime;
    private final float infectionChance;
    private int timePassed = 0;
    private final Random random = new Random();
    private final int[] globalTimer;
    private boolean doneFlag = false;
    private Eccentricity eccentricity;


    public Simulation(Graph graph, int infectionTime, float infectionChance) {
        this.population = new IPop[graph.vertecies];
        globalTimer = new int[1];
        for (int n = 0; n < graph.vertecies; n++) {
            population[n] = new Pop(n);
            //population[n] = new GlobalTimedPop(n, globalTimer);
        }

        this.graph = graph;
        this.report = new Report();
        this.infectionTime = infectionTime;
        this.infectionChance = infectionChance;

    }

    public int getTimePassed() {
        return timePassed;
    }

    public void initialize(Function<Integer, Boolean> f) {
        int total = 0;
        for (IPop p : population) {
            if (f.apply(p.getId())) {
                total++;
                p.setUpInfect(infectionTime);
            }
        }
        if (total == 0) {
            this.initialize(f);
        }
        report.newCases.add(total);
        report.cumulative.add(total);
        report.current.add(total);
    }

    private boolean infectPop(int n) {
        IPop p = population[n];
        if (p.isInfected() || p.isImmune()) return false;
        if (random.nextDouble() < infectionChance) {
            p.infect(infectionTime);
            return true;
        }
        return false;
    }

    private void doStep() {
        this.timePassed++;
        this.globalTimer[0]++;
        // report.realCumulative.add(population.stream().mapToInt(p -> p.isInfected() ? 1 : 0).sum());
        report.realCumulative.add(0);
        int newInfected = 0;

        int[] neighbours = graph.getAllNeighbours();
        boolean log_ecc = false;
        if (log_ecc) {
            if (eccentricity == null) {
                eccentricity = new Eccentricity(this);
            }
            eccentricity.update();
        }

        if(!isDone()) {
            for (IPop p : population) {
                p.step();
            }

            for (IPop p : population) {
                if (!p.isInfected()) continue;
                for (int i = graph.getStart(p.getId()); i < graph.getEnd(p.getId()); i++) {
                    if (!graph.hasEdge(p.getId(), neighbours[i])) continue;
                    if (infectPop(neighbours[i])) {
                        newInfected++;
                    }
                }
            }
        }
        if (!log_ecc) {
            //report.cumulative.add(report.cumulative.get(report.cumulative.size() - 1) + newInfected);
            //report.newCases.add(newInfected);
            report.cumulative.add(report.cumulative.get(report.cumulative.size() - 1) + newInfected);
            report.newCases.add(newInfected);
        } else {
            report.cumulative.add(eccentricity.diameter());
            report.newCases.add((int)Math.round(eccentricity.averageEcc()));
        }
        report.current.add(countInfected());

    }

    private int countInfected() {
        if (doneFlag) {
            return 0;
        }
        int sum = 0;
        for (IPop p : population) {
            sum += p.isInfected() ? 1 : 0;
        }
        if (sum == 0) {
            doneFlag = true;
        }
        return sum;
    }

    public Report simulate(int steps) {
        for (int i = 0; i < steps; i++) {
            doStep();
        }
        return report;
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    public void setGraph(Graph G) {
        this.graph = G;
    }

    @Override
    public void vaccinate(int i) {
        population[i].immunize();
    }

    @Override
    public boolean isDone() {
        return doneFlag;
    }
}
