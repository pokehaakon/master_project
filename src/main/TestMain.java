package main;

import Graph.Graph;
import Tools.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static Tools.Tools.*;

public class TestMain {
    public static final String GRAPHSPATH = "C:\\Users\\Haakon\\Desktop\\dirdesk\\Graphs\\graphs\\";
    public static final int MAXPOOL = 6;

    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        int[] sizes = new int[]{10_000};
        //int[] densities = new int[]{4,6,8,10,12};
        //int[] densities = new int[]{11,13,14};
        int[] densities = new int[]{13,14};
        int[] diams = new int[] {32, 128};
        for (int i = 0; i < 1; i++) {
            for (int size : sizes) {
                for (int dens : densities) {
                    for (int diam : diams) {
                        files.add("GEO_" + size + "_" + dens + "_" + diam + "_" + i);
                    }
                    files.add("SWG_" + size + "_" + dens + "_0-0505_" + i);
                    files.add("RND_" + size + "_" + dens + "_" + i);
                }
            }
        }

        //files = files.stream().skip(55).toList();
        //files = files.stream().limit(1).toList();

        //files = files.stream().skip(14).limit(1).toList();

        System.out.println(files);
        long time1 = System.nanoTime();
        //stopper thread

        System.out.println("Total files: " + files.size());

        StopperThread stopperThread = new StopperThread();
        stopperThread.start();

        for (String infile : files) {
            //log current file

            main_call(
                    infile,
                    10,
                    new TestReporter(),
                    300,
                    1,
                    new String[]{
//                            "NoPlan",
//
//                            "RemoveEdgesPlan",

                            "RandomPops",

                            "1-Neighbourhood",
//                          "2-Neighbourhood",
//                          "3-Neighbourhood",
                            "4-Neighbourhood",

                            "1-Ranked-Neighbourhood",
 //                           "2-Ranked-Neighbourhood",
 //                           "3-Ranked-Neighbourhood",
                            "4-Ranked-Neighbourhood",

                            "1-Ranked-Neighbourhood-neg-vac",
 //                           "2-Ranked-Neighbourhood-neg-vac",
 //                           "3-Ranked-Neighbourhood-neg-vac",
                            "4-Ranked-Neighbourhood-neg-vac",
//
                            "1-Ranked-Neighbourhood-neg-imm",
 //                           "2-Ranked-Neighbourhood-neg-imm",
 //                           "3-Ranked-Neighbourhood-neg-imm",
                            "4-Ranked-Neighbourhood-neg-imm",

//                            //"1-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"2-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"3-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"4-Ranked-Neighbourhood-neg-and-ign-vac",

                            "1-Ranked-Neighbourhood-neg-and-ign-imm",
  //                          "2-Ranked-Neighbourhood-neg-and-ign-imm",
  //                          "3-Ranked-Neighbourhood-neg-and-ign-imm",
                            "4-Ranked-Neighbourhood-neg-and-ign-imm",
//
                            "closeness",
//                            "1-closeness",
//                            "2-closeness",
//                            "3-closeness",
//                            "4-closeness",

                            "betweenness",
//                            "1-betweenness",
//                            "2-betweenness",
//                            "3-betweenness",
//                            "4-betweenness",
                    },
                    //new Float[]{0.001f, 0.00125f, 0.0015f, 0.00175f, 0.002f}, // add two (0.00225, 0.0025)
                    new Float[]{0.00875f, 0.01f}, //based on norwegian covid-19 vaccination rates
//                    new Float[]{0.001f, 0.0015f, 0.002f},
                    new Integer[]{1}, //time between each cycle
                    //new Integer[]{4, 8, 12},
                    new Integer[]{8}, //infection time
                    //new Float[]{0.005f, 0.0075f, 0.01f, 0.0125f, 0.015f}
                    new Float[]{0.015f} //infection chance
//                    new Float[]{0.005f, 0.01f, 0.01f}
            );
        }

        long totalTime = System.nanoTime() - time1;
        System.out.println(formatTime(totalTime));
    }

    public static void main_call(String filepattern,
                                 int numberOfSimsEach,
                                 Reporter rep,
                                 int simulationTime,
                                 int simulationInit,
                                 String[] planName,
                                 Float[] numberVaccEachCycle,
                                 Integer[] timeBetweenEachCycle,
                                 Integer[] infectionTime,
                                 Float[] infectionChance
    ) {

        List<String> graphFiles = filesFromPattern(filepattern);
        int lastUnder = 0;
        //List<String> files = new ArrayList<>();
        for (int i = 0; i < filepattern.length(); i++)
            if (filepattern.charAt(i) == '_') lastUnder = i;
        List<Graph> graphs = new ArrayList<>();
        for (String filename : graphFiles) {
            graphs.add(readGraph(filename));
        }

        Iterator<Combination> combinations = allCombinations(
                listRange(graphs.size()),
                listRange(numberOfSimsEach),
                List.of(simulationInit),
                Arrays.asList(planName),
                List.of(simulationTime),
                Arrays.asList(numberVaccEachCycle),
                Arrays.asList(timeBetweenEachCycle),
                Arrays.asList(infectionTime),
                Arrays.asList(infectionChance)
        );

        int total = numberOfCombinations(
                listRange(graphs.size()),
                listRange(numberOfSimsEach),
                List.of(simulationInit),
                Arrays.asList(planName),
                List.of(simulationTime),
                Arrays.asList(numberVaccEachCycle),
                Arrays.asList(timeBetweenEachCycle),
                Arrays.asList(infectionTime),
                Arrays.asList(infectionChance)
        );

        System.out.println("Starting " + total + " jobs");
        Locks locks = new Locks(new ReentrantLock(true), new ReentrantLock(true), new ReentrantLock(true), new ReentrantLock(true));

        Map<String, List<List<Integer>>> centralities = new HashMap<>();
        Map<String, List<Double>> simpleCentralities = new HashMap<>();
        Map<String, Long> timings = new HashMap<>();

        SharedData sharedData = new SharedData(graphs, total, filepattern, lastUnder, centralities, simpleCentralities, timings, rep, numberOfSimsEach);



        //System.out.println(Runtime.getRuntime().availableProcessors());
        //System.exit(0);

        Thread[] threads = new Thread[MAXPOOL];
        for (int i = 0; i < MAXPOOL; i++) {
            threads[i] = new ThreadTask(combinations, sharedData, locks);
            //threads[i].setDaemon(true);
            //threads[i].setPriority(Thread.MAX_PRIORITY);
            //System.out.println("Starting thread " + i);
            threads[i].start();
        }
        for (int i = 0; i < MAXPOOL; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //Display timings
        displayTimings(timings, planName);

    }


    public static void displayTimings(Map<String, Long> timings, String[] keys) {
        System.out.println("Timings:");
        for (String key : keys) {
            System.out.println(key + " -> " + formatTime(timings.get(key)));
        }
    }

    public static List<String> filesFromPattern(String filepattern) {
        int lastUnder = 0;
        int length = filepattern.length();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < filepattern.length(); i++)
            if (filepattern.charAt(i) == '_') lastUnder = i;

        String beforeLastUnder = filepattern.substring(0,lastUnder+1);

        if (filepattern.charAt(length - 1) == ']') {
            throw new RuntimeException("[] not supported!");
//            int start = lastUnder + 1;
//            endings = filepattern.
        } else if (filepattern.charAt(length - 1) == '*') {
            File f = new File(GRAPHSPATH);
            List<String> filenames = Arrays.asList(Objects.requireNonNull(f.list()));
            files = filenames.stream().filter(s -> s.startsWith(beforeLastUnder)).toList();
            System.out.println(beforeLastUnder);
        } else {
            files.add(filepattern);
        }
        return files;
    }


}
