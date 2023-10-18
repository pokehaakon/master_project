package Plans;

import Simulation.Simulation;
import Simulation.IPop;
import Simulation.Report;
import Tools.KClosenessConstructor;
import Tools.Tools;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class Closeness extends Plan {

    private final int k;
    private final Simulation sim;
    private final List<Double> centralities;

    public Closeness(Simulation sim, Map<String, List<Double>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        loadCentralities(new KClosenessConstructor(sim.graph, 0), centralities, "closeness");
        this.centralities = centralities.get("closeness");
        this.sim = sim;
        k = 0;
    }
    public Closeness(Simulation sim, int k, Map<String, List<Double>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        loadCentralities(new KClosenessConstructor(sim.graph, k), centralities, k+"-closeness");
        this.centralities = centralities.get(k + "-closeness");
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
