package main;

import Graph.Graph;
import Tools.Combination;
import Tools.Locks;
import Tools.Reporter;
import Tools.SharedData;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static Tools.Tools.*;
import static java.lang.System.exit;

public class MainClass {
    public static final String GRAPHSPATH = "C:\\Users\\Haakon\\Desktop\\dirdesk\\Graphs\\graphs\\";
    public static final int MAXPOOL = 10;

    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        int[] sizes = new int[]{10_000};
        //int[] densities = new int[]{4,6,8,10,12};
        //int[] densities = new int[]{11,13,14};

        //int[] densities = new int[]{4,6,8,10,11,12,13,14};
        //int[] densities = new int[]{4, 14};
        int[] densities = new int[] {14};


        int[] diams = new int[] {32, 128};
        //for (int i = 0; i < 5; i++) {
        for (int i = 0; i < 1; i++) {
            for (int size : sizes) {
                for (int dens : densities) {
                    for (int diam : diams) {
                        //files.add("GEO_" + size + "_" + dens + "_" + diam + "_" + i);
                    }
                    files.add("SWG_" + size + "_" + dens + "_0-0505_" + i);
                    //files.add("RND_" + size + "_" + dens + "_" + i);
                }
            }
        }

        /*
        MISSING GEO GRAPHS
        1000 12 128
        1000 13 128
        1000 14 128

         */

        List<String> remmove = new ArrayList<>();
        for (String file : files) {
            if (!hasFile(file)) {
                System.out.println("removing " + file);
                remmove.add(file);
            }
        }
        files.removeAll(remmove);

        //files.clear();
        //files.add("SWG_15000_2_0-0505_*");
        /*
        Reruns:
        SWG 15000 2
         */

        //System.out.println(files.size());
        //files = files.stream().skip(100).toList();
        //files = files.stream().skip(141).toList();

        long time1 = System.nanoTime();
        //stopper thread

        System.out.println("Total files: " + files.size());
        System.out.println("First file: " + files.get(0));
//        StopperThread stopperThread = new StopperThread();
//        stopperThread.start();

//        System.out.println(System.currentTimeMillis());
//        System.out.println((1_652_684_300 + (10 + 24*8 )*60*60) * 1000L);
//
//        System.exit(1);

        long stopAt = (1_665_565_733 + (7 + 24 * 33 )*60*60) * 1000L ; //23:09
        System.out.println("Epoch: " + stopAt);
        System.out.println("Runs stops in: " + formatTime((stopAt - System.currentTimeMillis()) * 1_000_000));

        //exit(1);


        int counter = -1;
        for (String infile : files) {
            //log current file
            counter++;
            System.out.println("Starting " + infile + "\t" + counter);
            if (System.currentTimeMillis() >= stopAt) {
                System.out.println("Stopped because of time(" + System.currentTimeMillis() + ")at: " + counter + ", filepattern: " + infile);
                break;
            } else {
                System.out.println("Stops in: " + formatTime((stopAt - System.currentTimeMillis()) * 1_000_000));
            }

            main_call(
                    infile,
                    1,
                    new Reporter( getGoodFiledef(infile) + ".mrep", "example_"),
                    300,
                    1,
                    new String[]{
                            "NoPlan",
//
//                            "RemoveEdgesPlan",

//                            "RandomPops",

//                            "1-Neighbourhood",
//                            "2-Neighbourhood",
//                            "3-Neighbourhood",
//                            "4-Neighbourhood",

//                            "1-Ranked-Neighbourhood",
//                            "2-Ranked-Neighbourhood",
//                            "3-Ranked-Neighbourhood",
//                            "4-Ranked-Neighbourhood",
//
//                            "1-Ranked-Neighbourhood-neg-vac",
//                            "2-Ranked-Neighbourhood-neg-vac",
//                            "3-Ranked-Neighbourhood-neg-vac",
//                            "4-Ranked-Neighbourhood-neg-vac",
////
//                              "1-Ranked-Neighbourhood-neg-imm",
//                              "2-Ranked-Neighbourhood-neg-imm",
//                              "3-Ranked-Neighbourhood-neg-imm",
//                              "4-Ranked-Neighbourhood-neg-imm",

//                            //"1-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"2-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"3-Ranked-Neighbourhood-neg-and-ign-vac",
//                            //"4-Ranked-Neighbourhood-neg-and-ign-vac",

//                              "1-Ranked-Neighbourhood-neg-and-ign-imm",
//                              "2-Ranked-Neighbourhood-neg-and-ign-imm",
//                              "3-Ranked-Neighbourhood-neg-and-ign-imm",
//                              "4-Ranked-Neighbourhood-neg-and-ign-imm",
//
//                              "closeness",
//                            "1-closeness",
//                            "2-closeness",
//                            "3-closeness",
//                            "4-closeness",

//                              "betweenness",
//                            "1-betweenness",
//                            "2-betweenness",
//                            "3-betweenness",
//                            "4-betweenness",
                    },
                    //vaccination fraction
                    //new Float[]{0.0025f, 0.00375f, 0.005f, 0.00625f, 0.0075f, 0.00875f, 0.01f}, //based on norwegian covid-19 vaccination rates & standard
                    //new Float[]{0.001f, 0.0015f, 0.002f},
                    new Float[]{0.1f},

                    //time between cycles
                    new Integer[]{1},

                    //infection time
                    new Integer[]{4},
                    //new Integer[]{8}, //standard

                    //infection chance
                    //new Float[]{0.0125f, 0.0175f}
                    new Float[]{0.015f} //standard
                    //new Float[]{0.005f, 0.01f, 0.01f}
            );
        }

        long totalTime = System.nanoTime() - time1;
        System.out.println(formatTime(totalTime));


    }

    public static void main_call(
            String filepattern,
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
        System.out.println(filepattern);
//        System.out.println(graphFiles);


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

        //System.out.println("Starting " + total + " jobs");
        Locks locks = new Locks(new ReentrantLock(), new ReentrantLock(), new ReentrantLock(), new ReentrantLock());

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
            threads[i].start();
        }
        for (int i = 0; i < MAXPOOL; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getGoodFiledef(String s) {
        int underCounter = 0;
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '_') {
                underCounter++;
            }
            if (underCounter == 3) {
                index = i;
                break;
            }
        }
        return s.substring(0, index);
    }

    public static List<String> filesFromPattern(String filepattern) {
        int lastUnder = 0;
        int length = filepattern.length();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < filepattern.length(); i++)
            if (filepattern.charAt(i) == '_') lastUnder = i;

        String beforeLastUnder = filepattern.substring(0, lastUnder+1);

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

    public static boolean hasFile(String filename) {
        File f = new File(GRAPHSPATH + filename);
        return f.exists();
    }
}
