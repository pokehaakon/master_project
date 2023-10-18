package Tools;

import Graph.Graph;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;

import static java.lang.System.exit;

public class KNeighbourhoodConstructor implements CentralityConstructor<List<Integer>> {
    private final Graph graph;
    private final int k;

    private HashSet<Integer>[] set1 = null, set2 = null;

    public KNeighbourhoodConstructor(Graph graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    @Override
    public List<List<Integer>> construct() {
        return hashSetConstructor();
    }


    public List<List<Integer>> hashSetConstructor() {
        int size = graph.vertecies;
        int k = this.k;

        List<Set<Integer>> prevLayer = new ArrayList<>();
        List<Set<Integer>> nextLayer = new ArrayList<>();
        Set<Integer> workingSet;

        for (int i = 0; i < size; i++) {
            Set<Integer> m = new HashSet<>();
            m.add(i);
            prevLayer.add(m);
        }
        for (int i = 0; i < k; i++) {
            nextLayer = prevLayer.stream().map(e -> (Set<Integer>) new HashSet<>(e)).toList();
            for (int n = 0; n < size; n++) {
                workingSet = nextLayer.get(n);
                for (int nei : graph.getNeighbours(n)) {
                    workingSet.addAll(prevLayer.get(nei));
                }
            }
        }

        return nextLayer.stream().map(e -> (List<Integer>) new ArrayList<>(e)).toList();
    }

    public List<List<Integer>> hashSetConstructor2(Set<Integer> ignore) {
        int size = graph.vertecies;
        int k = this.k + 1;

        List<Set<Integer>> prevLayer = new ArrayList<>();
        List<Set<Integer>> nextLayer = new ArrayList<>();
        Set<Integer> workingSet;

        for (int i = 0; i < size; i++) {
            if (ignore.contains(i)) {
                prevLayer.add(null);
                continue;
            }

            Set<Integer> m = new HashSet<>();
            m.add(i);
            prevLayer.add(m);
        }

        for (int i = 1; i < k; i++) {
            nextLayer = prevLayer.stream().map(e -> e != null ? (Set<Integer>) new HashSet<>(e) : null).toList();
            for (int n = 0; n < size; n++) {
                if (ignore.contains(n)) continue;
                workingSet = nextLayer.get(n);
                for (int nei : graph.getNeighbours(n)) {
                    if (ignore.contains(nei)) continue;
                    workingSet.addAll(prevLayer.get(nei));
                }
            }
        }

        return nextLayer.stream().map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
    }

    public BitSet[] bitSetConstructor(BitSet ignore) {
        int size = graph.vertecies;
        int[] nhs = graph.getAllNeighbours();
        int k = this.k + 1;
        BitSet[] prevLayer = new BitSet[size];
        BitSet[] nextLayer = new BitSet[size];
        BitSet[] temp;
        BitSet workingSet;

        for (int i = ignore.nextClearBit(0); i < size && i >= 0; i = ignore.nextClearBit(i+1)) {
            BitSet m = new BitSet(size);
            m.set(i);
            nextLayer[i] = m;
            prevLayer[i] = (BitSet) m.clone();
        }

        //Calculation of the nh
        for (int i = 1; i < k; i++) {
            temp = prevLayer;
            prevLayer = nextLayer;
            nextLayer = temp;

            for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                workingSet = nextLayer[n];
                for (int nei = graph.getStart(n); nei < graph.getEnd(n); nei++) {
                    if (ignore.get(nhs[nei])) continue;
                    workingSet.or(prevLayer[nhs[nei]]);
                }
            }
        }
        return nextLayer;
        //return Arrays.asList(nextLayer).stream().map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
    }

    public List<List<Integer>> hashSetConstructor(Set<Integer> ignore) {
        int size = graph.vertecies;
        int[] nhs = graph.getAllNeighbours();
        int k = this.k + 1;
        HashSet<Integer>[] prevLayer;
        HashSet<Integer>[] nextLayer;
        HashSet<Integer>[] temp;
        Set<Integer> workingSet;
        //Setup of the sets
        if (this.set1 == null || this.set2 == null) {
            prevLayer = new HashSet[size];
            nextLayer = new HashSet[size];
            set1 = prevLayer;
            set2 = nextLayer;

            for (int i = 0; i < size; i++) {
                if (ignore.contains(i)) {
                    continue;
                }

                HashSet<Integer> m = new HashSet<>();
                m.add(i);
                prevLayer[i] = m;
                nextLayer[i] = new HashSet<>();
            }
        } else {
            prevLayer = set1;
            nextLayer = set2;
            for (int i = 0; i < size; i++) {
                if (ignore.contains(i)) {
                    continue;
                }
                prevLayer[i].clear();
                prevLayer[i].add(i);
                nextLayer[i].clear();
            }
        }


        //Calculation of the nh
        for (int i = 1; i < k; i++) {
            temp = prevLayer;
            prevLayer = nextLayer;
            nextLayer = temp;

            for (int n = 0; n < size; n++) {
                if (ignore.contains(n)) continue;
                workingSet = nextLayer[n];
                for (int nei = graph.getStart(n); nei < graph.getEnd(n); nei++) {
                    if (ignore.contains(nhs[nei])) continue;
                    workingSet.addAll(prevLayer[nhs[nei]]);
                }
            }
        }

        return Arrays.stream(nextLayer).map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
    }

    private List<List<Integer>> BFSConstructor() {
        List<List<Integer>> nhs = new ArrayList<>(graph.vertecies);
        boolean[] marked = new boolean[graph.vertecies];
        int[] dist = new int[graph.vertecies];
        Queue<Integer> q = new ArrayDeque<>(graph.vertecies);
        int[] neighbours = graph.getAllNeighbours();

        for (int i = 0; i < graph.vertecies; i++) {
            for (int j = 0; j < graph.vertecies; j++) {
                marked[j] = false;
                dist[i] = 1000_000;
            }
            marked[i] = true;
            dist[i] = 0;
            List<Integer> nh = new ArrayList<>();
            q.clear();
            q.add(i);
            while (!q.isEmpty()) {
                int n = q.poll();
                for (int j = graph.getStart(n); j < graph.getEnd(n); j++) {
                    int neig = neighbours[j];
                    if (!marked[neig]) {
                        if (dist[n] + 1 != k) {q.add(neig);}
                        marked[neig] = true;
                        dist[neig] = dist[n] + 1;
                        nh.add(neig);
                    }
                }
            }
            nhs.add(nh);
        }

        return nhs;
    }

    public IntVector[] vectorConstructor(BitSet ignore) {
        final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

        int size = graph.vertecies;

        int[] nhs = graph.getAllNeighbours();
        IntVector[] prevLayer = new IntVector[size];
        IntVector[] nextLayer = new IntVector[size];
        IntVector[] temp;

        IntVector workingSet;

        for (int i = ignore.nextClearBit(0); i < size; i = ignore.nextClearBit(i+1)) {
            int[] m = new int[(int)Math.max(Math.ceil(size/32.0), 8)];
            m[i/32] |= 1 << i%32;
            nextLayer[i] = IntVector.fromArray(SPECIES, m, 0);
            prevLayer[i] = IntVector.fromArray(SPECIES, m, 0);
        }

        //Calculation of the nh
        for (int i = 0; i < k; i++) {
            temp = prevLayer;
            prevLayer = nextLayer;
            nextLayer = temp;


            for (int n = ignore.nextClearBit(0); n < size; n = ignore.nextClearBit(n+1)) {
                workingSet = nextLayer[n];
                for (int nei = graph.getStart(n); nei < graph.getEnd(n); nei++) {
                    if (ignore.get(nhs[nei])) continue;
                    workingSet = workingSet.or(prevLayer[nhs[nei]]);
                }
                nextLayer[n] = workingSet;
            }
        }
        return nextLayer;
    }














}
