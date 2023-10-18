package Graph;

import java.util.HashSet;
import java.util.List;

public class RemovableEdgeGraph extends Graph {
    private HashSet<Edge> removedEdges;

    public RemovableEdgeGraph(int vertices, int edges, List<Edge> edgeList) {
        super(vertices, edges, edgeList, 1);
        removedEdges = new HashSet<>();
    }

    public RemovableEdgeGraph(int vertecies, int edges, int[] neighbours, int[] preFixSum) {
        super(vertecies, edges, neighbours, preFixSum, 1);
        removedEdges = new HashSet<>();
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

        return new Graph(vertecies, edges, neighbours, preFixSum, 1);
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

    public boolean removeEdge(int a, int b) {
        if (a==b) return false;
        boolean has = false;
        for (int i = getStart(a); i < getEnd(a); i++) {
            if(neighbours[i] == b) {
                has = true;
                break;
            }
        }
        if (!has) return false;
        Edge e;
        if (a < b) {
            e = new Edge(a, b);
        } else {
            e = new Edge(b, a);
        }
        if (removedEdges.contains(e)) {
            return false;
        } else {
            removedEdges.add(e);
            return true;
        }
    }

    public boolean hasEdge(int a, int b) {
        Edge e;
        if (a < b) {
            e = new Edge(a,b);
        } else {
            e = new Edge(b,a);
        }
        return !removedEdges.contains(e);
    }

    public Graph makeEdgeRemovable() {
        return this;
    }
}
