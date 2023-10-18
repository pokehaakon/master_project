package Plans;

import Graph.Graph;
import Simulation.IPop;
import Simulation.Simulation;
import Simulation.Report;
import Tools.KNeighbourhoodConstructor;
import Tools.Tools;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import static Tools.Tools.cardinality;

public class RankedKNeighbourhoodIgnoreSomethingBitSet extends Plan {
    private final int k;
    private final Simulation sim;
    private final Function<Integer, Boolean> ignoreGetter;


    public RankedKNeighbourhoodIgnoreSomethingBitSet(Simulation sim, int k, Lock centralityLock, Function<Integer, Boolean> ignoreGetter) {
        super(sim, centralityLock);
        this.sim = sim;
        this.k = k;
        this.ignoreGetter = ignoreGetter;
    }

    @Override
    public Report runSim(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Graph graph = sim.getGraph();
        int size = graph.vertecies;
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;


        IntVector[] centralities;
        final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
        KNeighbourhoodConstructor constructor = new KNeighbourhoodConstructor(graph, this.k);
        BitSet ignore = new BitSet(graph.vertecies);
        BitSet immune = new BitSet(graph.vertecies);
        IntVector infected;

        List<int[]> keys = new ArrayList<>(graph.vertecies);
        for (int i = 0; i< graph.vertecies; i++) {
            keys.add(new int[2]);
        }

        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            if (sim.isDone()) {
                sim.simulate(timeBetweenSteps);
                continue;
            }
            int[] infectedArray = new int[(int)Math.ceil(graph.vertecies/32.0)];
            for (int n = 0; n < graph.vertecies; n++) {
                infectedArray[n/32] |= (sim.population[n].isInfected() ? 1 : 0) << n%32;
                immune.set(n, ignoreGetter.apply(n));
            }

            infected = IntVector.fromArray(SPECIES , infectedArray, 0);
            //centralities = constructor.bitSetConstructor(immune);
            centralities = constructor.vectorConstructor(immune);

            for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                int centCardinality = cardinality(centralities[n]);
                centralities[n] = centralities[n].and(infected);

                int infectedCount = cardinality(centralities[n]);
                int[] key = keys.get(n);
                key[0] = infectedCount; //became just size of neighbourhood
                key[1] = centCardinality; //equal to key[0]
                keys.set(n, key);
            }

            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(samplePop);

            samplePop.sort((a, b) -> - Tools.arrayCompare(keys.get(a.getId()), (keys.get(b.getId()))));

            vaccinationGoal += numberToVaccinateEachStep;
            int numberToVaccinate = (int) vaccinationGoal - totalVaccinated;
            totalVaccinated += numberToVaccinate;
            chosen = Tools.getFirstN(samplePop, numberToVaccinate);

            for (IPop p : chosen) {
                p.immunize();
            }
            addIgnorable(ignore);
        }
        return sim.simulate(finalSteps);
    }

    //@Override
    public Report runSim2(int steps, double numberToVaccinateEachStep, int timeBetweenSteps) {
        Graph graph = sim.getGraph();
        int size = graph.vertecies;
        int stepSteps = steps / timeBetweenSteps;
        int finalSteps = steps % timeBetweenSteps;
        List<IPop> samplePop = Arrays.asList(this.sim.population);
        List<IPop> chosen;


        BitSet[] centralities;
        KNeighbourhoodConstructor constructor = new KNeighbourhoodConstructor(graph, this.k);
        BitSet ignore = new BitSet(graph.vertecies);
        BitSet immune = new BitSet(graph.vertecies);
        BitSet infected = new BitSet(graph.vertecies);
        BitSet temp = new BitSet(graph.vertecies);

        List<int[]> keys = new ArrayList<>(graph.vertecies);
        for (int i = 0; i< graph.vertecies; i++) {
            keys.add(new int[2]);
        }

        int totalVaccinated = 0;
        double vaccinationGoal = 0;
        for (int i = 0; i < stepSteps; i++) {
            if (sim.isDone()) {
                sim.simulate(timeBetweenSteps);
                continue;
            }
            for (int n = 0; n < graph.vertecies; n++) {
                infected.set(n, sim.population[n].isInfected());
                immune.set(n, ignoreGetter.apply(n));
            }
            centralities = constructor.bitSetConstructor(immune);

            for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                int centCardinality = centralities[n].cardinality();
                centralities[n].and(infected);

                int infectedCount = centralities[n].cardinality();
                int[] key = keys.get(n);
                key[0] = infectedCount;
                key[1] = centCardinality;
                keys.set(n, key);
            }

            sim.simulate(timeBetweenSteps);
            samplePop = toVaccinate(samplePop);

            samplePop.sort((a, b) -> - Tools.arrayCompare(keys.get(a.getId()), (keys.get(b.getId()))));

            vaccinationGoal += numberToVaccinateEachStep;
            int numberToVaccinate = (int) vaccinationGoal - totalVaccinated;
            totalVaccinated += numberToVaccinate;
            chosen = Tools.getFirstN(samplePop, numberToVaccinate);

            for (IPop p : chosen) {
                p.immunize();
            }
            addIgnorable(ignore);
        }
        return sim.simulate(finalSteps);
    }
    private void addIgnorable(BitSet ignore) {
        for (IPop p : sim.population) {
            ignore.set(p.getId(), p.isImmune());
//            if (!ignore.get(p.getId()) && p.isImmune()) {
//                ignore.set(p.getId());
//            }
        }
    }
}
