package Tools;

import Graph.Graph;
import Simulation.Simulation;
import Simulation.IPop;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static Tools.Tools.neighbourhoodLayers;

public class Eccentricity {

    int[] eccs;
    Simulation sim;
    BitSet ignore;
    BitSet[] prevLayer, nextLayer;

    public Eccentricity (Simulation sim) {
        int size = sim.graph.vertecies;
        eccs = new int[size];
        this.sim = sim;
        ignore = new BitSet(size);
        prevLayer = new BitSet[size];
        nextLayer = new BitSet[size];
        for (int i = 0; i<size; i++) {
            prevLayer[i] = new BitSet(size);
            nextLayer[i] = new BitSet(size);
        }
    }
    private int[] bitSetAllEccentricities(Simulation sim) {
        Graph graph = sim.graph;
        int size = graph.vertecies;
        int[] nhs = graph.getAllNeighbours();

        BitSet ignore = new BitSet(size);
        for (IPop p : sim.population) {
            if (p.isImmune()) {
                ignore.set(p.getId());
            }
        }

        BitSet[] temp;
        BitSet workingSet;
        boolean[] doneWith = new boolean[size];
        for (int i = 0; i<size; i++)
            doneWith[i] = false;
        int doneWithCounter = 0;

        //setting up first run
        for (int i = ignore.nextClearBit(0); i < size && i >= 0; i = ignore.nextClearBit(i+1)) {
            nextLayer[i].clear();
            nextLayer[i].set(i);
            prevLayer[i].clear();
            prevLayer[i].set(i);
        }

        //Calculation of the nh
        int layer = 1;
        int needed = size - ignore.cardinality();
        while (true) {
            temp = prevLayer;
            prevLayer = nextLayer;
            nextLayer = temp;

            for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                if (doneWith[n]) continue;
                //n has all values where n is not immune or done calculating
                workingSet = nextLayer[n];
                for (int nei = graph.getStart(n); nei < graph.getEnd(n); nei++) {
                    if (ignore.get(nhs[nei])) continue;
                    workingSet.or(prevLayer[nhs[nei]]);
                }
                //if (prevLayer[n].equals(nextLayer[n])) {
                if (prevLayer[n].cardinality() == (nextLayer[n]).cardinality()) {
                    doneWith[n] = true;
                    doneWithCounter++;
                    eccs[n] = layer;
                }
            }
            layer++;
            if (doneWithCounter == needed) break;
            if (layer > size) {
                throw new RuntimeException("diameter too high, loop does not stop");
            }
        }
        return eccs;

    }

    public void update() {
        eccs = bitSetAllEccentricities(sim);
    }

    public int diameter() {
        int m = 0;
        for (int e : eccs) {
            m = Math.max(e, m);
        }
        return m;
    }

    public double averageEcc() {
        double sum = 0.0;
        int zeros = 0;
        for (int e : eccs) {
            sum += e;
            zeros += e == 0 ? 1 : 0;
        }
        return sum / (double)(eccs.length - zeros);
    }
}
