package Tools;

import Graph.Graph;

import java.util.*;

public class KShellConstructor implements CentralityConstructor<List<Integer>> {

    private final Graph graph;

    public KShellConstructor(Graph g) {
        graph = g;
    }

    @Override
    public List<List<Integer>> construct() {
        return null;
    }

    public List<Integer> KShells(BitSet ignore) {
        List<Integer> kvalue = new ArrayList<>(graph.vertecies);
        List<Set<Integer>> D = new ArrayList<>(graph.vertecies);
        for (int i = 0; i < graph.vertecies; i++) {
            D.add(null);
        }

        int[] neigbours = graph.getAllNeighbours();
        int[] degree = new int[graph.vertecies];
        for (int v = 0; v < graph.vertecies; v++) {
            if (ignore.get(v)) continue;
            for (int ui = graph.getStart(v); ui < graph.getEnd(v); ui++) {
                if (ignore.get(neigbours[ui])) continue;
                degree[v]++;
            }
        }
        for (int v = 0; v < graph.vertecies; v++) {
            if (ignore.get(v)) continue;
            if (D.get(degree[v]) == null) {
                D.set(degree[v], new HashSet<>());
            }
            D.get(degree[v]).add(v);
        }
        int k = 0;
        Set<Integer> s;

        for (int inc = 0; inc < graph.vertecies; inc++) {
            int i = 0;
            for (Set<Integer> s1 : D) {
                if (!s1.isEmpty()) {
                    break;
                }
                i++;
            }
            s = D.get(i);
            k = Math.max(k, i);

            while (!s.isEmpty()) {
                int v = s.iterator().next();
                s.remove(v);
                kvalue.set(v, k);
                for (int u = graph.getStart(v); u < graph.getEnd(v); u++) {
                    if (ignore.get(u)) continue;
                    D.get(degree[u]).remove(u);
                    degree[u]--;
                    D.get(degree[u]).add(u);
                }
            }
        }

        return kvalue;
    }
}
