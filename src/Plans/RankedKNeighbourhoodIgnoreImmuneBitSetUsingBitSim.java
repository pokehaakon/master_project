package Plans;

import Graph.Graph;
import Simulation.Report;
import Simulation.BitSetSimulation;
import Tools.KNeighbourhoodConstructor;
import Tools.Tools;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim extends Plan {
    private final int k;
    private final BitSetSimulation sim;


    public RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim(BitSetSimulation sim, int k, Lock centralityLock) {
        super(sim, centralityLock);
        this.sim = sim;
        this.k = k;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Graph graph = sim.getGraph();
        int size = sim.graph.vertecies;
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<Integer> chosen, samplePop;

        BitSet[] centralities;
        KNeighbourhoodConstructor constructor = new KNeighbourhoodConstructor(graph, this.k);
        BitSet ignore = new BitSet(graph.vertecies);
        BitSet immune = new BitSet(graph.vertecies);
        BitSet infected = new BitSet(graph.vertecies);

        List<int[]> keys = new ArrayList<>(graph.vertecies);
        for (int i = 0; i< graph.vertecies; i++) {
            keys.add(new int[2]);
        }


        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            centralities = constructor.bitSetConstructor(ignore);
            infected = sim.getPrev();
            immune = sim.getIsImmune();
            ignore = immune;
//            if (ignore.size() > graph.vertecies) {
//                throw new ArrayIndexOutOfBoundsException("WTF got: " + ignore.size() + ", wanted " + graph.vertecies);
//            }

            for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                BitSet temp = (BitSet) centralities[n].clone();
                int centCardinality = centralities[n].cardinality();
                temp.and(infected);
                centralities[n].and(immune);
                int infectedCount = temp.cardinality();
                int immuneCount = centralities[n].cardinality();

                int[] key = keys.get(n);
                key[0] = infectedCount;
                key[1] = centCardinality - immuneCount;
                keys.set(n, key);
            }

            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(ignore);

            samplePop.sort((a, b) -> - Tools.arrayCompare(keys.get(a), (keys.get(b))));

            vaccinationGoal += numberToVaccinateEachStep;
            int numberToVaccinate = (int) vaccinationGoal - totalVaccinated;
            totalVaccinated += numberToVaccinate;
            chosen = Tools.getFirstN(samplePop, numberToVaccinate);

            for (int p : chosen) {
                sim.vaccinate(p);

            }
        }
        return sim.simulate(finalSteps);
    }


    List<Integer> toVaccinate(BitSet ignore) {
        List<Integer> r = new ArrayList<>();
        for (int n = ignore.nextClearBit(0); n >= 0; n = ignore.nextClearBit(n+1)) {
            r.add(n);
        }
        return r;
    }
}
