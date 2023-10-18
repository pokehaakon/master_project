package Plans;

import Simulation.Simulation;
import Simulation.IPop;
import Simulation.Report;
import Tools.Tools;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class RandomPops extends Plan {
    private final Simulation sim;
    public RandomPops(Simulation sim, Lock centralityLock) {
        super(sim, centralityLock);
        this.sim = sim;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Random random = new Random();
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;
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
