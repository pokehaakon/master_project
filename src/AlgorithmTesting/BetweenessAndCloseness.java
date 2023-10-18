package AlgorithmTesting;

import Graph.Graph;
import jdk.incubator.vector.IntVector;

import java.util.*;

import static Tools.Tools.neighbourhoodLayers;
import static Tools.Tools.nextSetBit;

public class BetweenessAndCloseness {

    public static List<IBetweenessAndCloseness> getCalculators() {
        List<IBetweenessAndCloseness> calculators = new ArrayList<>();
        calculators.add(new BetweenessAndCloseness.Brandes());
        calculators.add(new BetweenessAndCloseness.DistanceLayersBetweeness());
        calculators.add(new BetweenessAndCloseness.VectorBetweeness());
        return calculators;
    }

    public static class Brandes implements IBetweenessAndCloseness {

        @Override
        public double[] compute(Graph G) {
            double[] betweeness = new double[G.vertecies];
            double[] closeness = new double[G.vertecies];
            int[] sigma = new int[G.vertecies];
            List<List<Integer>> P = new ArrayList<>(G.vertecies);
            for (int v = 0; v < G.vertecies; v++) {
                P.add(new ArrayList<>());
            }
            int[] dist = new int[G.vertecies];
            Queue<Integer> Q = new ArrayDeque<>(G.vertecies);
            Deque<Integer> S = new ArrayDeque<>(G.vertecies);
            int[] neighbours = G.getAllNeighbours();

            for (int i = 0; i < G.vertecies; i++) {
                for (int j = 0; j < G.vertecies; j++) {
                    dist[j] = 1000_000;
                    P.get(j).clear();
                    sigma[j] = 0;
                }
                sigma[i] = 1;
                dist[i] = 0;
                Q.clear();
                Q.add(i);

                while (!Q.isEmpty()) {
                    int v = Q.poll();
                    S.push(v);
                    for (int j = G.getStart(v); j < G.getEnd(v); j++) {
                        int w = neighbours[j];

                        if (dist[w] == 1000_000) {
                            Q.add(w);
                            dist[w] = dist[v] + 1;
                        }
                        if (dist[w] == dist[v] + 1) {
                            sigma[w] += sigma[v];
                            P.get(w).add(v);
                        }
                    }
                }

                double[] delta = new double[G.vertecies];

                while (!S.isEmpty()) {
                    int w = S.pop();
                    if (dist[w] != 0) closeness[i] += 1.0/dist[w];
                    for (int v : P.get(w)) {
                        delta[v] += (1 + delta[w])*(sigma[v]/((double)sigma[w]));
                    }
                    if (w != i) {
                        betweeness[w] += delta[w];
                    }
                }

            }

            return betweeness;
        }

        @Override
        public String getName() {
            return "BRA";
        }
    }

    public static class DistanceLayersBetweeness implements IBetweenessAndCloseness {

        @Override
        public double[] compute(Graph G) {
            List<BitSet[]> layers;
            //BitSet intersection = new BitSet(G.vertecies);
            List<Integer> intersection = new ArrayList<>();

            layers = neighbourhoodLayers(G);


            double[] betweeness = new double[G.vertecies];
            double[] closeness = new double[G.vertecies];


            double[] workingC = new double[G.vertecies];
            for (int n = 0; n < G.vertecies; n++ ) {
                for (int i = 0; i < G.vertecies; i++ ) {
                    workingC[i] = 0.0;
                }
                for (int i = layers.size()-1; i > 1; i--) {
                    if (layers.get(i)[n] == null) continue;
                    BitSet nextLayer = layers.get(i-1)[n];
                    BitSet thisLayer = layers.get(i)[n];

                    int crd = 0;
                    for (int v = thisLayer.nextSetBit(0); v >= 0; v = thisLayer.nextSetBit(v+1)) {
                        crd++;
                        intersection.clear();
                        double cardinality = 0.0;
                        for (int u : G.getNeighbours(v)) {
                            if (nextLayer.get(u)) {
                                intersection.add(u);
                                cardinality++;
                            }
                        }
                        double inverseCardinality = 1.0 / cardinality;

                        for (int u : intersection) {
                            double val = (workingC[v] + 1) * inverseCardinality;
                            betweeness[u] += val;
                            workingC[u] += val;
                        }
                    }
                    closeness[n] += crd/(double)i;
                }
            }

            return betweeness;
        }

        @Override
        public String getName() {
            return "DLB";
        }
    }

    public static class VectorBetweeness implements IBetweenessAndCloseness {

        @Override
        public double[] compute(Graph G) {
            List<IntVector[]> layers;
            //BitSet intersection = new BitSet(G.vertecies);
            List<Integer> intersection = new ArrayList<>();
            DistanceLayers.VectorImplementation layer_calc = new DistanceLayers.VectorImplementation();
            layers = layer_calc.compute(G);


            double[] betweeness = new double[G.vertecies];
            double[] closeness = new double[G.vertecies];


            double[] workingC = new double[G.vertecies];
            for (int n = 0; n < G.vertecies; n++ ) {
                for (int i = 0; i < G.vertecies; i++ ) {
                    workingC[i] = 0.0;
                }
                for (int i = layers.size()-1; i > 1; i--) {
                    if (layers.get(i)[n] == null) continue;
                    IntVector nextLayer = layers.get(i-1)[n];
                    IntVector thisLayer = layers.get(i)[n];

                    int crd = 0;
                    for (int v = nextSetBit(0, thisLayer); v >= 0; v = nextSetBit(v+1, thisLayer)) {
                        crd++;
                        intersection.clear();
                        double cardinality = 0.0;
                        for (int u : G.getNeighbours(v)) {
                            if ((nextLayer.lane(u/32) & 1 << u % 32 ) > 0) {
                                intersection.add(u);
                                cardinality++;
                            }
                        }
                        double inverseCardinality = 1.0 / cardinality;

                        for (int u : intersection) {
                            double val = (workingC[v] + 1) * inverseCardinality;
                            betweeness[u] += val;
                            workingC[u] += val;
                        }
                    }
                    closeness[n] += crd/(double)i;
                }
            }

            return betweeness;
        }

        @Override
        public String getName() {
            return "VecB";
        }
    }
}
