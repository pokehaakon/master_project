package Plans;

import Graph.Graph;
import Simulation.IPop;
import Simulation.ISimulation;
import Simulation.Report;
import Simulation.Simulation;
import Tools.Tools;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class RemoveEdgesPlan extends Plan {
    private Simulation sim;

    public RemoveEdgesPlan(Simulation sim, Lock centralityLock) {
        super(sim, centralityLock);
        Graph g = sim.getGraph();
        g = g.makeEdgeRemovable();
        sim.setGraph(g);
        this.sim = sim;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Random random = new Random();
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;
        Graph g = sim.getGraph();
        int[] neighbours = g.getAllNeighbours();
        int pos = 0;
        for (int i = 0; i < g.vertecies; i++) {
            int atts = 0;
            while (atts < 100) {
                pos = random.nextInt(g.getStart(i), g.getEnd(i));
                if (g.hasEdge(i, neighbours[pos])) {
                    g.removeEdge(i, neighbours[pos]);
                    break;
                } else {
                    atts++;
                }
            }
        }
        numberToVaccinateEachStep = numberToVaccinateEachStep/2; //half number of vaccinations
        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(samplePop);

            Collections.shuffle(samplePop);

            vaccinationGoal += numberToVaccinateEachStep;
            int numberToVaccinate = (int) vaccinationGoal - totalVaccinated;
            totalVaccinated += numberToVaccinate;
            chosen = Tools.getFirstN(samplePop, numberToVaccinate);
            for (IPop p : chosen) {
                p.immunize();
            }
        }
        return sim.simulate(finalSteps);
    }
}
