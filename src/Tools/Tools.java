package Tools;

import Graph.Edge;
import Graph.Graph;
import Graph.Property;
import jdk.incubator.vector.IntVector;
import main.MainClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

public abstract class Tools {
    public static <T> List<T> getFirstN(java.util.List<T> list, int n) {
        return list.stream().limit(n).toList();
    }

    public static int arrayCompare(int[] leftInts, int[] rightInts) {
        for (int i = 0; i < Math.min(leftInts.length, rightInts.length); i++) {
            if (leftInts[i] == rightInts[i]) {
                continue;
            }
            return Integer.compare(leftInts[i], rightInts[i]);
        }
        return 0;

    }

    public static int nextSetBit(int i, IntVector vec) {
        while (i >= 0) {
            if (vec.lane(i/32) == 0) {
                i -= 1 + i % 32;
                continue;
            }
            if ((vec.lane(i/32) & (1 << i % 32)) > 0) {
                return i;
            }
            i -= 1;
        }
        return -1;
    }

    public static int cardinality(IntVector vec) {
        int sum = 0;
        for (int i = 0; i < vec.length(); i++) {
            sum += Integer.bitCount(vec.lane(i));
        }
        return sum;
    }
    public static Graph readGraph(String filename) {
        try {
            List<Edge> edges = new ArrayList<>();
            File myObj = new File(MainClass.GRAPHSPATH + filename);
            Scanner myReader = new Scanner(myObj);
            String firstline = myReader.nextLine().strip();
            String[] line;
            double alpha = 0;
            if (firstline.charAt(0) == '!') {
                alpha = Double.parseDouble(firstline.split(":")[1].split(";")[0]);
                line = myReader.nextLine().strip().split(" ");
            } else {
                line = firstline.split(" ");
            }
            //line = myReader.nextLine().strip().split(" ");
            int n = Integer.parseInt(line[0]);
            int m = Integer.parseInt(line[1]);
            int v,u;
            while (myReader.hasNextLine()) {
                line = myReader.nextLine().strip().split(" ");
                v = Integer.parseInt(line[0]);
                u = Integer.parseInt(line[1]);
                edges.add(new Edge(v,u));
            }
            myReader.close();
            return new Graph(n, m, edges, alpha);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static int numberOfCombinations (
            List<Integer> graphIndexes,
            List<Integer> subSims,
            List<Integer> numbersAtInitialization,
            List<String> planNames,
            List<Integer> totalSimTimes,
            List<Float> numbersToVaccinateEachStep,
            List<Integer> timesBetweenEachCycle,
            List<Integer> infectionTime,
            List<Float> infectionChance
    ) {
        final int[] maxIndexes = new int[]{
                graphIndexes.size(),
                subSims.size(),
                numbersAtInitialization.size(),
                planNames.size(),
                totalSimTimes.size(),
                numbersToVaccinateEachStep.size(),
                timesBetweenEachCycle.size(),
                infectionTime.size(),
                infectionChance.size(),
        };
        for (int i = 0; i < maxIndexes.length; i++) {
            if (maxIndexes[i] == 0) {
                System.out.println(i + " ");
            }
        }
        return Arrays.stream(maxIndexes).reduce((a, b) -> a * b).getAsInt();
    }

    public static Iterator<Combination> allCombinations(
            List<Integer> graphIndexes,
            List<Integer> subSims,
            List<Integer> numbersAtInitialization,
            List<String> planNames,
            List<Integer> totalSimTimes,
            List<Float> numbersToVaccinateEachStep,
            List<Integer> timesBetweenEachCycle,
            List<Integer> infectionTime,
            List<Float> infectionChance
    ) {
        return new Iterator<>() {
            final int[] maxIndexes = new int[]{
                    graphIndexes.size(),
                    subSims.size(),
                    numbersAtInitialization.size(),
                    planNames.size(),
                    totalSimTimes.size(),
                    numbersToVaccinateEachStep.size(),
                    timesBetweenEachCycle.size(),
                    infectionTime.size(),
                    infectionChance.size(),
            };
            final int[] indexes = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
            int index = 0;
            final int max = Arrays.stream(maxIndexes).reduce((a, b) -> a * b).getAsInt();

            @Override
            public boolean hasNext() {
                return index < max;
            }

            @Override
            public Combination next() {
                Combination c = new Combination(
                        graphIndexes.get(indexes[0]++),
                        subSims.get(indexes[1]),
                        numbersAtInitialization.get(indexes[2]),
                        planNames.get(indexes[3]),
                        totalSimTimes.get(indexes[4]),
                        numbersToVaccinateEachStep.get(indexes[5]),
                        timesBetweenEachCycle.get(indexes[6]),
                        infectionTime.get(indexes[7]),
                        infectionChance.get(indexes[8]),
                        index++
                );
                for (int i = 0; i < indexes.length; i++) {
                    if (indexes[i] == maxIndexes[i]) {
                        indexes[i] = 0;
                        if (i == 8) {
                            continue;
                        }
                        indexes[i + 1]++;
                    }
                }
                return c;
            }
        };
    }

    public static List<Integer> listRange(int stop) {
        return listRange(0, stop);
    }

    public static List<Integer> listRange(int start, int stop) {
        return IntStream.range(start, stop).boxed().toList();
    }

    public static String formatTime(long timeInNanoSec) {
        double totalTimeInUnit = timeInNanoSec/1_000_000_000.0;
        String unit = "s";
        if (totalTimeInUnit > 60) {
            totalTimeInUnit = totalTimeInUnit / 60;
            unit = "m";
        }
        if (totalTimeInUnit > 60) {
            totalTimeInUnit = totalTimeInUnit / 60;
            unit = "h";
        }
        return  totalTimeInUnit + unit;
    }
    public static List<BitSet[]> neighbourhoodLayers(Graph g) {
        return neighbourhoodLayers(g, g.vertecies);
    }

    public static List<BitSet[]> neighbourhoodLayers(Graph g, int k) {
        int size = g.vertecies;
        List<BitSet[]> layers = new ArrayList<>();

        BitSet[] ilayer = new BitSet[size];
        layers.add(ilayer);
        BitSet[] cumulative = new BitSet[size];
        for (int n = 0; n < size; n++) {
            ilayer[n] = new BitSet();
            ilayer[n].set(n);

            cumulative[n] = new BitSet(size);
            cumulative[n].set(n);
        }

        HashSet<Integer> notDone = new HashSet<>(size);
        for(int i = 0; i < size; i++) {
            notDone.add(i);
        }

        int layerCounter = 0;
        int[] allNeighbours = g.getAllNeighbours();
        List<Integer> doneWith = new ArrayList<>();

        while (layerCounter <= k+1 && !notDone.isEmpty()) {
            layerCounter++;
            if (layerCounter > g.vertecies-1) {
                throw new RuntimeException("k is larger than graph.vertecies");
            }
            layers.add(new BitSet[size]);
            for (int n : notDone) {
                layers.get(layerCounter)[n] = new BitSet(size);
            }
            //for (int n : (Set<Integer>) notDone.clone()) {
            for (int n : notDone) {
                BitSet layer = layers.get(layerCounter)[n];
                for (int neighbourindex = g.getStart(n); neighbourindex < g.getEnd(n); neighbourindex++) {
                //for (int nei : g.getNeighbours(n)) {
                    int nei = allNeighbours[neighbourindex];
                    if (layers.get(layerCounter-1)[nei] == null) continue;
                    layer.or(layers.get(layerCounter-1)[nei]);
                }
                layer.andNot(cumulative[n]);
                cumulative[n].or(layer);
                if (layer.cardinality() == 0) {
                    //notDone.remove(n);
                    doneWith.add(n);
                    layers.get(layerCounter)[n] = null;
                }
            }
            for (int rem : doneWith) {
                notDone.remove(rem);
            }
            doneWith.clear();

        }
        //System.out.println("Layers: " + layers.size());
        return layers;
    }

    public static Iterable<Integer> bitSetIterable(BitSet set) {
        //To iterate over the true bits in a BitSet, use the following loop:
//        for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i+1)) {
//            // operate on index i here
//            if (i == Integer.MAX_VALUE) {
//                break; // or (i+1) would overflow
//            }
//        }

        return () -> new Iterator<>() {
            int i = set.nextSetBit(0);

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public Integer next() {
                int temp = i;
                i = set.nextSetBit(i+1);
                return temp;
            }
        };
    }

    public static boolean getVectorValue(IntVector vec, int index) {
        return 0 < (vec.lane(index/32) & 1 << index%32);
    }

}





























