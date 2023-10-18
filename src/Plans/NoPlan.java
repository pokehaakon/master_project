package Plans;

import Simulation.Simulation;
import Simulation.Report;

import java.util.concurrent.locks.Lock;


public class NoPlan extends Plan {

    public NoPlan(Simulation sim, Lock centralityLock) {
        super(sim, centralityLock);
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        return sim.simulate(steps);
    }
}
