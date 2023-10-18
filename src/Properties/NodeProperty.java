package Properties;

import Graph.Graph;

public interface NodeProperty {
    String getName();

    double[] property(Graph g);
}
