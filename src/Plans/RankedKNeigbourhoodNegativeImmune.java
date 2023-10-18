package Plans;

import Graph.Graph;
import Simulation.Simulation;
import Simulation.IPop;
import Simulation.Report;
import Tools.KNeighbourhoodConstructor;
import Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class RankedKNeigbourhoodNegativeImmune extends Plan {
    private final List<List<Integer>> centralities;
    private final Simulation sim;

    public RankedKNeigbourhoodNegativeImmune(Simulation sim, int k, Map<String, List<List<Integer>>> centralities, Lock centralityLock) {
        super(sim, centralityLock);
        String centralityName = k +"-Neighbourhood";
        loadCentralities(new KNeighbourhoodConstructor(sim.graph, k), centralities, centralityName);
        this.centralities = centralities.get(centralityName);
        this.sim = sim;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Graph graph = sim.graph;
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;

        List<int[]> keys = new ArrayList<>();
        int infected;
        int vaccinated;

        for (int n = 0; n < graph.vertecies; n++) {
            keys.add(new int[] {0, 0});
        }

        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(samplePop);

            for (int n = 0; n < graph.vertecies; n++) {
                infected = 0;
                vaccinated = 0;
                for (int nei : centralities.get(n)) {
                    IPop p = sim.population[nei];
                    infected += p.isInfected() ? 1 : 0;
                    vaccinated += p.isImmune() ? 1 : 0;
                }
                int[] k = keys.get(n);
                k[0] = infected;
                k[1] = centralities.get(n).size() - vaccinated;
            }

            samplePop.sort((a, b) -> - Tools.arrayCompare(keys.get(a.getId()), (keys.get(b.getId()))));

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
