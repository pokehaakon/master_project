package Graph;

import java.util.List;

public class Graph {
    public final int vertecies;
    public final int edges;
    protected final int[] neighbours;
    protected final int[] preFixSum;
    public final double alpha;

    protected Graph(int vertecies, int edges, int[] neighbours, int[] preFixSum, double alpha) {
        this.vertecies = vertecies;
        this.edges = edges;
        this.neighbours = neighbours;
        this.preFixSum = preFixSum;
        this.alpha = alpha;
    }

    public Graph(int vertices, int edges, List<Edge> edgeList, double alpha) {
        this.vertecies = vertices;
        this.edges = edges;
        this.alpha = alpha;
        preFixSum = new int[vertices+1];
        neighbours = new int[edges*2];
        for (Edge e : edgeList) {
            preFixSum[e.v()]++;
            preFixSum[e.u()]++;
        }
        int sum = 0;
        int temp = 0;
        for (int i = 0; i < vertices+1; i++) {
            temp = preFixSum[i];
            preFixSum[i] = sum;
            sum += temp;
        }
        int[] tempArr = new int[vertices+1];
        System.arraycopy(preFixSum, 0 , tempArr, 0, vertices+1);

        for (Edge e : edgeList) {
            neighbours[tempArr[e.v()]++] = e.u();
            neighbours[tempArr[e.u()]++] = e.v();
        }

/*
        neighbours = new List[vertices];
        for (int n = 0; n < vertices; n++) {
            neighbours[n] = new ArrayList<>();
        }
        for (Edge e : edgeList) {
            neighbours[e.v()].add(e.u());
            neighbours[e.u()].add(e.v());
        }

 */
    }

    public int[] getNeighbours(int v) {
        int[] ret = new int[preFixSum[v+1] - preFixSum[v]];
        System.arraycopy(neighbours, preFixSum[v], ret, 0,preFixSum[v+1] - preFixSum[v]);
        return ret;
    }

    public Graph copy() {
        int[] neighbours = new int[this.neighbours.length];
        int[] preFixSum = new int[this.preFixSum.length];
        System.arraycopy(this.neighbours, 0, neighbours, 0, neighbours.length);
        System.arraycopy(this.preFixSum, 0, preFixSum, 0, preFixSum.length);

        return new Graph(vertecies, edges, neighbours, preFixSum, alpha);
    }

    public int getStart(int v) {
        return preFixSum[v];
    }

    public int getEnd(int v) {
        return preFixSum[v+1];
    }

    public int[] getAllNeighbours() {
        return neighbours;
    }

    public boolean hasEdge(int a, int b) {
        return true;
    }

    public boolean removeEdge(int a, int b) {
        return false;
    }

    public Graph makeEdgeRemovable() {
        int[] neighbours = new int[this.neighbours.length];
        int[] preFixSum = new int[this.preFixSum.length];
        System.arraycopy(this.neighbours, 0, neighbours, 0, neighbours.length);
        System.arraycopy(this.preFixSum, 0, preFixSum, 0, preFixSum.length);

        return new RemovableEdgeGraph(vertecies, edges, neighbours, preFixSum);
    }
}
