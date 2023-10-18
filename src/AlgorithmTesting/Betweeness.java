package AlgorithmTesting;

import Graph.Graph;

import java.util.*;

import static Tools.Tools.neighbourhoodLayers;

public class Betweeness {

    public static List<IBetweeness> getCalculators() {
        List<IBetweeness> calculators = new ArrayList<>();
        calculators.add(new Betweeness.Brandes());
        calculators.add(new DistanceLayersBetweeness());
        return calculators;
    }

    public static class Brandes implements IBetweeness {

        @Override
        public double[] compute(Graph G) {
            double[] centralities = new double[G.vertecies];
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
                    P.clear();
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
                    for (int v : P.get(w)) {
                        delta[v] += (1 + delta[w])*(sigma[v]/((double)sigma[w]));
                    }
                    if (w != i) {
                        centralities[w] += delta[w];
                    }
                }

            }

            return centralities;
        }

        @Override
        public String getName() {
            return "BRA";
        }
    }

    public static class DistanceLayersBetweeness implements IBetweeness {

        @Override
        public List<Double> compute(Graph G) {
            List<BitSet[]> layers;
            BitSet intersection = new BitSet(G.vertecies);

            layers = neighbourhoodLayers(G);


            List<Double> centralities = new ArrayList<>();
            for (int i = 0; i < G.vertecies; i++ ) {
                centralities.add(0.0);
            }

            List<Double> workingC = new ArrayList<>();
            for (int n = 0; n < G.vertecies; n++ ) {
                for (int i = 0; i < G.vertecies; i++ ) {
                    workingC.add(0.0);
                }
                for (int i = layers.size()-1; i > 1; i--) {
                    if (layers.get(i)[n] == null) continue;
                    BitSet thisLayer = layers.get(i)[n];
                    BitSet nextLayer = layers.get(i-1)[n];

                    for (int v = thisLayer.nextSetBit(0); v >= 0; v = thisLayer.nextSetBit(v+1)) {
                        //if (!thisLayer.get(v)) continue;
                        intersection.clear();
                        double cardinality = 0.0;
                        for (int u : G.getNeighbours(v)) {
                            if (nextLayer.get(u)) {
                                intersection.set(u);
                                cardinality++;
                            }
                        }
                        double inverseCardinality = 1.0 / cardinality;

                        for (int u = intersection.nextSetBit(0); u >= 0; u = intersection.nextSetBit(u+1)) {
                            //if (!intersection.get(u)) continue;
                            double val = (workingC.get(v) + 1) * inverseCardinality;
                            centralities.set(u, centralities.get(u) + val);
                            workingC.set(u, workingC.get(u) + val);
                        }
                    }
                }
            }

            return centralities;
        }

        @Override
        public String getName() {
            return "DLB";
        }
    }
}
