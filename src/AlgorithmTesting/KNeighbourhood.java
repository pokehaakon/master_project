package AlgorithmTesting;

import Graph.Graph;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;

public class KNeighbourhood {
    public static List<IKNeighourhood> getCalculators() {
        List<IKNeighourhood> calculators = new ArrayList<>();
        calculators.add(new KNeighbourhood.BFSImplementation());
        calculators.add(new KNeighbourhood.HashSetImplementation());
        calculators.add(new KNeighbourhood.BitSetImplementation());
        calculators.add(new KNeighbourhood.VectorImplementation());
        return calculators;
    }
    public static class BitSetImplementation implements IKNeighourhood{
        public BitSetImplementation() {}

        @Override
        public Object compute(Graph G, int k) {
            int size = G.vertecies;
            int[] nhs = G.getAllNeighbours();
            BitSet[] prevLayer = new BitSet[size];
            BitSet[] nextLayer = new BitSet[size];
            BitSet[] temp;
            BitSet workingSet;

            //for (int i = ignore.nextClearBit(0); i < size && i >= 0; i = ignore.nextClearBit(i+1)) {
            for (int i = 0; i < size; i++) {
                BitSet m = new BitSet(size);
                m.set(i);
                nextLayer[i] = m;
                prevLayer[i] = (BitSet) m.clone();
            }

            //Calculation of the nh
            for (int i = 0; i < k; i++) {
                temp = prevLayer;
                prevLayer = nextLayer;
                nextLayer = temp;

                //for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                for (int n = 0; n < size; n++) {
                    workingSet = nextLayer[n];
                    for (int nei = G.getStart(n); nei < G.getEnd(n); nei++) {
                        //if (ignore.get(nhs[nei])) continue;
                        workingSet.or(prevLayer[nhs[nei]]);
                    }
                }
            }
            return nextLayer;
            //return Arrays.asList(nextLayer).stream().map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
        }

        @Override
        public String getName() {
            return "BSet";
        }
    }

    public static class HashSetImplementation implements IKNeighourhood{
        public HashSetImplementation() {}

        @Override
        public Object compute(Graph G, int k) {
            int size = G.vertecies;
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
                    for (int nei : G.getNeighbours(n)) {
                        workingSet.addAll(prevLayer.get(nei));
                    }
                }
            }

            return nextLayer;
        }

        @Override
        public String getName() {
            return "HSet";
        }
    }

    public static class BFSImplementation implements IKNeighourhood{
        public BFSImplementation() {}

        @Override
        public Object compute(Graph G, int k) {

            List<List<Integer>> nhs = new ArrayList<>(G.vertecies);
            //boolean[] marked = new boolean[G.vertecies];
            int[] dist = new int[G.vertecies];
            Queue<Integer> q = new ArrayDeque<>(G.vertecies);
            int[] neighbours = G.getAllNeighbours();

            for (int i = 0; i < G.vertecies; i++) {
                for (int j = 0; j < G.vertecies; j++) {
                    //marked[j] = false;
                    dist[j] = 1000_000;
                }
                //marked[i] = true;
                dist[i] = 0;
                List<Integer> nh = new ArrayList<>();
                q.clear();
                q.add(i);
                while (!q.isEmpty()) {
                    int n = q.poll();
                    for (int j = G.getStart(n); j < G.getEnd(n); j++) {
                        int neig = neighbours[j];
                        //if (!marked[neig]) {
                        if (dist[neig] == 1000_000) {
                            if (dist[n] + 1 < k) {
                                q.add(neig);
                                dist[neig] = dist[n] + 1;
                            }
                            //marked[neig] = true;
                            nh.add(neig);
                        }
                    }
                }
                nhs.add(nh);
            }

            return nhs;
        }

        @Override
        public String getName() {
            return "BFS";
        }
    }

    public static class VectorImplementation implements IKNeighourhood{
        public VectorImplementation() {}

        @Override
        public IntVector[] compute(Graph G, int k) {
            final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

            int size = G.vertecies;

            int[] nhs = G.getAllNeighbours();
            IntVector[] prevLayer = new IntVector[size];
            IntVector[] nextLayer = new IntVector[size];
            IntVector[] temp;

            IntVector workingSet;

            //for (int i = ignore.nextClearBit(0); i < size && i >= 0; i = ignore.nextClearBit(i+1)) {
            for (int i = 0; i < size; i++) {
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

                //for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                for (int n = 0; n < size; n++) {
                    workingSet = nextLayer[n];
                    for (int nei = G.getStart(n); nei < G.getEnd(n); nei++) {
                        //if (ignore.get(nhs[nei])) continue;
                        workingSet = workingSet.or(prevLayer[nhs[nei]]);
                    }
                    nextLayer[n] = workingSet;
                }
            }
            return nextLayer;
            //return Arrays.asList(nextLayer).stream().map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
        }


        public IntVector[] computeIgnore(Graph G, int k, BitSet ignore) {
            final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

            int size = G.vertecies;

            int[] nhs = G.getAllNeighbours();
            IntVector[] prevLayer = new IntVector[size];
            IntVector[] nextLayer = new IntVector[size];
            IntVector[] temp;

            IntVector workingSet;

            for (int i = ignore.nextClearBit(0); i < size; i = ignore.nextClearBit(i+1)) {
            //for (int i = ignore.nextClearBit(size-1); i >= 0; i = ignore.nextClearBit(i-1)) {
            //for (int i = 0; i < size; i++) {
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

                //for (int n = ignore.nextClearBit(0); n < size && n >= 0; n = ignore.nextClearBit(n+1)) {
                for (int n = ignore.nextClearBit(0); n < size; n = ignore.nextClearBit(n+1)) {
                //for (int n = 0; n < size; n++) {
                    workingSet = nextLayer[n];
                    for (int nei = G.getStart(n); nei < G.getEnd(n); nei++) {
                        if (ignore.get(nhs[nei])) continue;
                        workingSet = workingSet.or(prevLayer[nhs[nei]]);
                    }
                    nextLayer[n] = workingSet;
                }
            }
            return nextLayer;
            //return Arrays.asList(nextLayer).stream().map(e -> e != null ? (List<Integer>) new ArrayList<>(e) : null).toList();
        }

        @Override
        public String getName() {
            return "Vec";
        }
    }

}
