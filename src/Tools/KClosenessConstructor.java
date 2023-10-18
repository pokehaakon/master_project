package Tools;

import Graph.Graph;
import Simulation.Simulation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static Tools.Tools.neighbourhoodLayers;

public class KClosenessConstructor implements CentralityConstructor<Double> {
    Graph graph;
    int k;

    public KClosenessConstructor(Graph graph, int k) {
        this.k = k;
        this.graph = graph;
    }

    @Override
    public List<Double> construct() {

        List<BitSet[]> layers;
        if (k == 0) {
            layers = neighbourhoodLayers(graph);
        } else {
            layers = neighbourhoodLayers(graph, k);
        }
        List<Double> centralities = new ArrayList<>(graph.vertecies);
        for (int i = 0; i < graph.vertecies; i++) {
            centralities.add(0.0);
        }
        for (int layerIndex = 1; layerIndex < layers.size(); layerIndex++) {
            for (int n = 0; n < layers.get(layerIndex).length; n++) {
                if (layers.get(layerIndex)[n] == null) continue;
                centralities.set(n, centralities.get(n) + layers.get(layerIndex)[n].cardinality()/(double)layerIndex);
            }
        }
        return centralities;
    }
}
