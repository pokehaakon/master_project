package AlgorithmTesting;

import Graph.Graph;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;
import static Tools.Tools.cardinality;

public class DistanceLayers {
    public static List<IDistanceLayers> getCalculators() {
        List<IDistanceLayers> calculators = new ArrayList<>();
        calculators.add(new DistanceLayers.BFSImplementation());
        calculators.add(new DistanceLayers.HashSetImplementation());
        calculators.add(new DistanceLayers.BitSetImplementation());
        return calculators;
    }
    public static class BFSImplementation implements IDistanceLayers {


        @Override
        public Object compute(Graph G) {
            List<List<Integer>> layerIndices = new ArrayList<>(G.vertecies);
            List<List<Integer>> layers = new ArrayList<>(G.vertecies);

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
                int maxDist = 0;
                q.clear();
                q.add(i);
                while (!q.isEmpty()) {
                    int n = q.poll();
                    for (int j = G.getStart(n); j < G.getEnd(n); j++) {
                        int neig = neighbours[j];
                        //if (!marked[neig]) {
                        if (dist[neig] == 1000_000) {
                            q.add(neig);
                            dist[neig] = dist[n] + 1;
                            if (dist[neig] > maxDist) {
                                maxDist = dist[neig];
                            }
                        }
                    }
                }
                int[] prefixSum = new int[maxDist+1];
                List<Integer> iIndecies = new ArrayList<>(maxDist+2);
                List<Integer> ilayer = new ArrayList<>(G.vertecies);
                for (int n = 0; n < G.vertecies; n++) {
                    prefixSum[dist[n]]++;
                    ilayer.add(null);
                }
                int cum = 0;
                for (int n = 0; n < maxDist+1; n++) {
                    iIndecies.add(cum);
                    prefixSum[n] = cum;
                    cum += prefixSum[n];
                }
                iIndecies.add(cum);

                for (int n = 0; n < G.vertecies; n++) {
                    ilayer.set(prefixSum[dist[n]]++, n);
                }
                layerIndices.add(iIndecies);
                layers.add(ilayer);
            }

            return layers;
        }

        @Override
        public String getName() {
            return "BFS";
        }
    }

    public static class HashSetImplementation implements IDistanceLayers {

        @Override
        public Object compute(Graph G) {
            int size = G.vertecies;
            List<HashSet<Integer>[]> layers = new ArrayList<>();

            HashSet<Integer>[] ilayer = new HashSet[size];
            layers.add(ilayer);
            HashSet<Integer>[] cumulative = new HashSet[size];
            for (int n = 0; n < size; n++) {
                ilayer[n] = new HashSet<>();
                ilayer[n].add(n);

                cumulative[n] = new HashSet<>(size);
                cumulative[n].add(n);
            }

            HashSet<Integer> notDone = new HashSet<>(size);
            for(int i = 0; i < size; i++) {
                notDone.add(i);
            }

            int layerCounter = 0;
            while (!notDone.isEmpty()) {
                layerCounter++;
                if (layerCounter > G.vertecies-1) {
                    throw new RuntimeException("k is larger than graph.vertecies");
                }
                layers.add(new HashSet[size]);
                for (int n : notDone) {
                    layers.get(layerCounter)[n] = new HashSet<>(size);
                }
                for (int n : (Set<Integer>) notDone.clone()) {
                    HashSet<Integer> layer = layers.get(layerCounter)[n];
                    for (int nei : G.getNeighbours(n)) {
                        if (layers.get(layerCounter-1)[nei] == null) continue;
                        layer.addAll(layers.get(layerCounter-1)[nei]);
                    }

                    layer.removeAll(cumulative[n]);
                    cumulative[n].addAll(layer);
                    if (layer.size() == 0) {
                        notDone.remove(n);
                        layers.get(layerCounter)[n] = null;
                    }
                }

            }
            return layers;
        }

        @Override
        public String getName() {
            return "HSet";
        }
    }

    public static class BitSetImplementation implements IDistanceLayers {

        @Override
        public Object compute(Graph G) {
            int size = G.vertecies;
            List<BitSet[]> layers = new ArrayList<>();

            BitSet[] ilayer = new BitSet[size];
            layers.add(ilayer);
            BitSet[] cumulative = new BitSet[size];
            for (int n = 0; n < size; n++) {
                ilayer[n] = new BitSet(size);
                ilayer[n].set(n);

                cumulative[n] = new BitSet(size);
                cumulative[n].set(n);
            }

            HashSet<Integer> notDone = new HashSet<>(size);
            for(int i = 0; i < size; i++) {
                notDone.add(i);
            }

            int layerCounter = 0;
            while (!notDone.isEmpty()) {
                layerCounter++;
                if (layerCounter > G.vertecies-1) {
                    throw new RuntimeException("k is larger than graph.vertecies");
                }
                layers.add(new BitSet[size]);
                for (int n : notDone) {
                    layers.get(layerCounter)[n] = new BitSet(size);
                }
                for (int n : (Set<Integer>) notDone.clone()) {
                    BitSet layer = layers.get(layerCounter)[n];
                    for (int nei : G.getNeighbours(n)) {
                        if (layers.get(layerCounter-1)[nei] == null) continue;
                        layer.or(layers.get(layerCounter-1)[nei]);
                    }
                    layer.andNot(cumulative[n]);
                    cumulative[n].or(layer);
                    if (layer.cardinality() == 0) {
                        notDone.remove(n);
                        layers.get(layerCounter)[n] = null;
                    }
                }

            }
            //System.out.println("Layers: " + layers.size());
            return layers;
        }

        @Override
        public String getName() {
            return "BSet";
        }
    }

    public static class VectorImplementation implements IDistanceLayers {
        @Override
        public List<IntVector[]> compute(Graph G) {
            int size = G.vertecies;
            final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
            List<IntVector[]> layers = new ArrayList<>();
            //List<BitSet[]> layers = new ArrayList<>();

            IntVector[] ilayer = new IntVector[size];
            layers.add(ilayer);
            IntVector[] cumulative = new IntVector[size];
            for (int n = 0; n < size; n++) {
                int[] m = new int[Math.max((int)Math.ceil(size/32.0), 32)];
                m[n/32] |= 1 << n%32;
                ilayer[n] = IntVector.fromArray(SPECIES, m, 0);
                cumulative[n] = IntVector.fromArray(SPECIES, m, 0);
            }

            HashSet<Integer> notDone = new HashSet<>(size);
            for(int i = 0; i < size; i++) {
                notDone.add(i);
            }

            int layerCounter = 0;
            int[] empty = new int[Math.max((int)Math.ceil(size/32.0), 32)];
            while (!notDone.isEmpty()) {
                layerCounter++;
                if (layerCounter > G.vertecies-1) {
                    throw new RuntimeException("k is larger than graph.vertecies");
                }
                layers.add(new IntVector[size]);
                for (int n : notDone) {
                    layers.get(layerCounter)[n] = IntVector.fromArray(SPECIES, empty, 0);
                    //layers.get(layerCounter)[n] = new BitSet(size);
                }
                for (int n : (Set<Integer>) notDone.clone()) {
                    IntVector layer = layers.get(layerCounter)[n];
                    for (int nei : G.getNeighbours(n)) {
                        if (layers.get(layerCounter-1)[nei] == null) continue;
                        layer.or(layers.get(layerCounter-1)[nei]);
                    }
                    cumulative[n].not();
                    layer.and(cumulative[n]);
                    cumulative[n].not();
                    cumulative[n].or(layer);
                    if (cardinality(layer) == 0) {
                        notDone.remove(n);
                        layers.get(layerCounter)[n] = null;
                    }
                }

            }
            //System.out.println("Layers: " + layers.size());
            return layers;
        }

        @Override
        public String getName() {
            return "Vec";
        }
    }
}
