package Plans;

import Simulation.Simulation;
import Simulation.IPop;
import Simulation.Report;
import Tools.Tools;
import Tools.KNeighbourhoodConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class KNeighbourhood extends Plan {
    private final List<Integer> centralities;
    private final Simulation sim;

    public KNeighbourhood(Simulation sim, int k, Map<String, List<List<Integer>>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        String centralityName = k +"-Neighbourhood";
        loadCentralities(new KNeighbourhoodConstructor(sim.graph, k), centralities, centralityName);
        this.centralities = centralities.get(centralityName).stream().map(List::size).toList();
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
