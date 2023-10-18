package Properties;

import AlgorithmTesting.DistanceLayers;
import Graph.Graph;

import java.util.BitSet;
import java.util.List;

import static Tools.Tools.neighbourhoodLayers;

public class Diameter implements GraphProperty {
    @Override
    public String getName() {
        return "DIAMETER";
    }

    @Override
    public String property(Graph g) {
        return "" + neighbourhoodLayers(g).size();
    }
}
