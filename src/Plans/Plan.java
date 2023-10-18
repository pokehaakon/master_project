package Plans;

import Simulation.IPop;
import Simulation.Report;
import Simulation.ISimulation;
import Tools.CentralityConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public abstract class Plan {
    ISimulation sim;
    Lock centralityLock;

    public Plan(ISimulation sim, Lock centralityLock) {
        this.sim = sim;
        this.centralityLock = centralityLock;
    }

    public List<IPop> toVaccinate(List<IPop> previousList) {
        List<IPop> rList = new ArrayList<>();
        for (IPop p : previousList) {
            if (p.isImmune()) continue;
            rList.add(p);
        }
        return rList;
    }

    public <T> void loadCentralities(CentralityConstructor<T> f, Map<String, List<T>> centralities, String centralityName) {
        if (centralities.containsKey(centralityName)) return;
        centralityLock.lock();
        try {
            if (!centralities.containsKey(centralityName)) {
                centralities.put(centralityName, f.construct());
            }
        } finally {
            centralityLock.unlock();
        }
    }

    public abstract Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps);


}
