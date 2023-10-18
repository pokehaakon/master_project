package Tools;

import Graph.Graph;

import java.util.*;

import static Tools.Tools.bitSetIterable;
import static Tools.Tools.neighbourhoodLayers;

public class KBetweennessConstructor implements CentralityConstructor<Double> {
    Graph graph;
    int k;

    public KBetweennessConstructor(Graph graph, int k) {
        this.k = k;
        this.graph = graph;
    }

    @Override
    public List<Double> construct() {
        //return constructBitSet();
        return constructBitSetIterable();
        //return constructHashSet();
    }


    public List<Double> constructBitSetIterable() {
        List<BitSet[]> layers;
        BitSet intersection = new BitSet(graph.vertecies);
        if (k == 0) {
            layers = neighbourhoodLayers(graph);
        } else {
            layers = neighbourhoodLayers(graph, k);
        }

        List<Double> centralities = new ArrayList<>();
        for (int i = 0; i < graph.vertecies; i++ ) {
            centralities.add(0.0);
        }

        List<Double> workingC = new ArrayList<>();
        for (int n = 0; n < graph.vertecies; n++ ) {
            for (int i = 0; i < graph.vertecies; i++ ) {
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
                    for (int u : graph.getNeighbours(v)) {
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

//    @Override
    public List<List<Double>> constructHashSet() {
        List<BitSet[]> bitLayers;
        if (k == 0) {
            bitLayers = neighbourhoodLayers(graph);
        } else {
            bitLayers = neighbourhoodLayers(graph, k);
        }

        List<Set<Integer>[]> layers = new ArrayList<>();
        for (BitSet[] bitLayer : bitLayers) {
            Set<Integer>[] layer = new HashSet[graph.vertecies];
            int i = 0;
            for (BitSet b : bitLayer) {
                layer[i] = new HashSet<>();
                if (b == null) {
                    i++;
                    continue;
                }
                for (int v = 0; v < graph.vertecies; v++) {
                    if (b.get(v)) layer[i].add(v);
                }
                i++;
            }
            layers.add(layer);
        }

        List<List<Double>> centralities = new ArrayList<>();
        for (int i = 0; i < graph.vertecies; i++ ) {
            List<Double> l = new ArrayList<>(1);
            l.add(0.0);
            centralities.add(l);
        }

        for (int n = 0; n < graph.vertecies; n++ ) {
            List<Double> workingC = new ArrayList<>();
            for (int i = 0; i < graph.vertecies; i++ ) {
                workingC.add(0.0);
            }
            for (int i = layers.size()-1; i > 1; i--) {
                Set<Integer> thisLayer = layers.get(i)[n];
                Set<Integer> nextLayer = layers.get(i-1)[n];
                if (thisLayer == null) continue;
                for (Integer v : thisLayer) {
                    Set<Integer> intersection = new HashSet<>(thisLayer);
                    intersection.removeAll(nextLayer);
                    for (int u : intersection) {
                        double val = (workingC.get(v) + 1)/ intersection.size();
                        centralities.get(u).set(0, centralities.get(u).get(0) + val);
                        workingC.set(u, workingC.get(u) + val);
                    }
                }
            }

        }

        return centralities;
    }


//    @Override
    public List<List<Double>> constructBitSet() {
        List<BitSet[]> layers;
        if (k == 0) {
            layers = neighbourhoodLayers(graph);
        } else {
            layers = neighbourhoodLayers(graph, k);
        }

        List<List<Double>> centralities = new ArrayList<>();
        for (int i = 0; i < graph.vertecies; i++ ) {
            List<Double> l = new ArrayList<>(1);
            l.add(0.0);
            centralities.add(l);
        }

        List<Double> workingC = new ArrayList<>();
        for (int n = 0; n < graph.vertecies; n++ ) {
            for (int i = 0; i < graph.vertecies; i++ ) {
                workingC.add(0.0);
            }
            for (int i = layers.size()-1; i > 1; i--) {
                if (layers.get(i)[n] == null) continue;
                BitSet thisLayer = layers.get(i)[n];
                BitSet nextLayer = layers.get(i-1)[n];

                for (int v = 0; v < thisLayer.size(); v++) {
                    if (!thisLayer.get(v)) continue;
                    BitSet intersection = (BitSet) thisLayer.clone();
                    intersection.and(nextLayer);
                    double inverseCardinality = 1.0 / intersection.cardinality();

                    for (int u = 0; u < intersection.length(); u++) {
                        if (!intersection.get(u)) continue;
                        double val = (workingC.get(v) + 1) * inverseCardinality;
                        centralities.get(u).set(0, centralities.get(u).get(0) + val);
                        workingC.set(u, workingC.get(u) + val);
                    }
                }
            }
        }

        return centralities;
    }
}
