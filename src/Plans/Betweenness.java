package Plans;

import Simulation.Simulation;
import Simulation.IPop;
import Simulation.Report;
import Tools.KBetweennessConstructor;
import Tools.Tools;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class Betweenness extends Plan {

    private final int k;
    private final List<Double> centralities;
    private final Simulation sim;

    public Betweenness(Simulation sim, Map<String, List<Double>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        loadCentralities(new KBetweennessConstructor(sim.graph, 0), centralities, "betweenness");
        this.centralities = centralities.get("betweenness");
        k = 0;
        this.sim = sim;
    }
    public Betweenness(Simulation sim, int k, Map<String, List<Double>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        loadCentralities(new KBetweennessConstructor(sim.graph, k), centralities, k+"-betweenness");
        this.centralities = centralities.get(k + "-betweenness");
        this.k = k;
        this.sim = sim;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;

        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(samplePop);
            samplePop.sort((a, b) -> - centralities.get(a.getId()).compareTo(centralities.get(b.getId())));

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
