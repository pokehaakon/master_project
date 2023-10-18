package Properties;

import Graph.Graph;
import Simulation.IPop;
import Tools.Eccentricity;

import java.util.BitSet;
import java.util.List;

import static Tools.Tools.neighbourhoodLayers;

public class Eccentricities implements GraphProperty {

    @Override
    public String getName() {
        return "ecc";
    }

    @Override
    public String property(Graph g) {
        List<BitSet[]> layers = neighbourhoodLayers(g);
        int[] ecc = new int[g.vertecies];
        for (int l = 0; l < layers.size(); l++) {
            BitSet[] layer = layers.get(l);
            for (int i = 0; i < g.vertecies; i++) {
                if (ecc[i] != 0) continue;
                if (layer[i] == null) ecc[i] = l-1;
            }
        }
        for (int i = 0; i < g.vertecies; i++) {
            if (ecc[i] == 0) ecc[i] = layers.size();
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < g.vertecies; i++) {
            s.append(ecc[i]);
            if (i < g.vertecies - 1) {
                s.append(",");
            }
        }
        return s.toString();
    }
}
