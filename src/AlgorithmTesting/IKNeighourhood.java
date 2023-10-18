package AlgorithmTesting;

import Graph.Graph;

public interface IKNeighourhood {
    Object compute(Graph G, int k);
    String getName();
}

