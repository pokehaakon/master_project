package main;

import Graph.Graph;
import Plans.Plan;
import Plans.PlanGenerator;
import Plans.PlanGeneratorBit;
import Simulation.Report;
import Simulation.Simulation;
import Simulation.BitSetSimulation;
import Tools.Combination;
import Tools.Locks;
import Tools.SharedData;
import Tools.ThreadReturn;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static Tools.Tools.formatTime;

public class ThreadTask extends Thread {
    private final Iterator<Combination> combinations;
    private SharedData sharedData;
    private final Locks locks;

    private long totalTime = 0;
    private long id;

    public ThreadTask(Iterator<Combination> combinations, SharedData sharedData, Locks locks) {
        super();
        this.combinations = combinations;
        this.sharedData = sharedData;
        this.locks = locks;
    }

    @Override
    public void run() {
        id = Thread.currentThread().getId();
        Combination task;
        ThreadReturn values;
        List<Graph> copyedGraphs = new ArrayList<>();
        for (Graph g : sharedData.graphs()) {
            copyedGraphs.add(g.copy());
        }
        sharedData = sharedData.replaceGraphs(copyedGraphs);

        while (true) {
            locks.taskLock().lock();
            try {
                if (!combinations.hasNext()) {
                    break;
                }
                task = combinations.next();
            } finally {
                locks.taskLock().unlock();
            }

            values = threadJob(task);

            locks.writeLock().lock();
            try {
                sharedData.reporter().report(values.report(), values.name());
            } finally {
                locks.writeLock().unlock();
            }
        }
        //System.out.println("TotalTime " + formatTime(totalTime));
    }


    private ThreadReturn bitSetSimulation(Combination combination) {
        Graph graph = sharedData.graphs().get(combination.graphIndex());
        BitSetSimulation sim = new BitSetSimulation(graph, combination.infectionTime(), combination.infectionChance());
        Random random = new Random();

        Set<Integer> choices = new HashSet<>();
        while (choices.size() != combination.numberAtInitialization()) {
            choices.add(random.nextInt(graph.vertecies));
        }
        //sim.initialize(x -> random.nextDouble() < (double) combination.numberAtInitialization() / (double) graph.vertecies);
        //sim.initialize(choices::contains);
        //sim.initialize((x) -> x == 0);
        int toInfect = (int)(
                combination.subSimNum()/(float)(sharedData.numberSimsEach()-1) * (graph.vertecies - 1)
        );
        sim.initialize((x) -> x == toInfect);


        Plan plan = PlanGeneratorBit.generate(combination.planName(), sim, sharedData.centralities(), sharedData.simpleCentralities(), locks.centralityLock());
        int planN = Math.round(combination.numberToVaccinateEachStep()
                * combination.timeBetweenEachCycle()
                * graph.vertecies);


        Report rep = plan.runSim(
                combination.totalSimTime(),
                planN,
                combination.timeBetweenEachCycle()
        );


        if (sim.getTimePassed() < combination.totalSimTime()) {
            throw new RuntimeException("simulation did not run!");
        }


        String graphFileName = sharedData.filepattern().substring(0, sharedData.lastUnder());
        String newFile = "report_" + combination.totalSimTime() + "_" + combination.numberAtInitialization() + "_"
                + combination.planName() + "_" + planN + "_" + combination.timeBetweenEachCycle()
                + "_False_" + combination.infectionTime() + "_" + combination.infectionChance()
                + "---" + graphFileName + "_" + combination.graphIndex() + "_" + combination.subSimNum() + ".rep"
                ;

        String name = combination.totalSimTime() + " " + combination.numberAtInitialization() + " "
                + combination.planName() + " " + planN + " " + combination.timeBetweenEachCycle() + " "
                + "False" + " " + combination.infectionTime() + " " + combination.infectionChance() + " "
                + graphFileName + "_" + combination.graphIndex()
                ;
        if (sharedData.total() < 100) {
            System.out.println(combination.totalSimNumber() + "/" + sharedData.total());
        } else if (combination.totalSimNumber() % (sharedData.total() / 100) == 0) {
            System.out.println(combination.totalSimNumber() + "/" + sharedData.total());
        }
        return new ThreadReturn(rep, newFile, name);
    }

    private ThreadReturn normalSimulation(Combination combination) {
        Graph graph = sharedData.graphs().get(combination.graphIndex());
        Simulation sim = new Simulation(graph, combination.infectionTime(), combination.infectionChance());
        Random random = new Random();

        Set<Integer> choices = new HashSet<>();
        while (choices.size() != combination.numberAtInitialization()) {
            choices.add(random.nextInt(graph.vertecies));
        }
        //sim.initialize(x -> random.nextDouble() < (double) combination.numberAtInitialization() / (double) graph.vertecies);
        //sim.initialize(choices::contains);
        //sim.initialize((x) -> x == 0);
        int toInfect = (int)(
                combination.subSimNum()/(float)(sharedData.numberSimsEach()-1) * (graph.vertecies - 1)
        );

        sim.initialize((x) -> x == toInfect);


        Plan plan = PlanGenerator.generate(combination.planName(), sim, sharedData.centralities(), sharedData.simpleCentralities(), locks.centralityLock());

        Report rep = plan.runSim(
                combination.totalSimTime(),
                combination.numberToVaccinateEachStep(),
                combination.timeBetweenEachCycle()
        );


        if (sim.getTimePassed() < combination.totalSimTime()) {
            throw new RuntimeException("Simulation only ran for " + sim.getTimePassed());
        }

        String planN = String.valueOf(combination.numberToVaccinateEachStep());
        if (planN.length() > 5) {
            planN = planN.substring(0, 6);
        }
        String graphFileName = sharedData.filepattern().substring(0, sharedData.lastUnder());
        String newFile = "report_" + combination.totalSimTime() + "_" + combination.numberAtInitialization() + "_"
                + combination.planName() + "_" + planN + "_" + combination.timeBetweenEachCycle()
                + "_False_" + combination.infectionTime() + "_" + combination.infectionChance()
                + "---" + graphFileName + "_" + combination.graphIndex() + "_" + combination.subSimNum() + ".rep"
                ;

        String name = combination.totalSimTime() + " " + combination.numberAtInitialization() + " "
                + combination.planName() + " " + planN + " " + combination.timeBetweenEachCycle() + " "
                + "False" + " " + combination.infectionTime() + " " + combination.infectionChance() + " "
                + graphFileName + "_" + combination.graphIndex()
                ;
        if (sharedData.total() < 100) {
            System.out.println(combination.totalSimNumber() + "/" + sharedData.total() + ", by thread: " + id);
        } else if (combination.totalSimNumber() % (sharedData.total() / 100) == 0) {
            System.out.println(combination.totalSimNumber() + "/" + sharedData.total() + ", by thread: " + id);
        }
        return new ThreadReturn(rep, newFile, name);
    }
    public ThreadReturn threadJob(Combination combination) {
        long t1 = System.nanoTime();
        ThreadReturn ret;
        if (false) {
        //if (isBitSim(combination.planName())) {
            ret = bitSetSimulation(combination);
        } else {
            ret = normalSimulation(combination);
        }

        long totalTime = System.nanoTime() - t1;

        locks.timeLock().lock();
        try {
            if (!sharedData.timings().containsKey(combination.planName())) {
                sharedData.timings().put(combination.planName(), 0L);
            }
            sharedData.timings().put(combination.planName(), sharedData.timings().get(combination.planName()) + totalTime);
        } finally {
            locks.timeLock().unlock();
        }

        return ret;
    }

    private boolean isBitSim(String s) {
        return switch (s) {
            case "1-Ranked-Neighbourhood-neg-and-ign-vac", "2-Ranked-Neighbourhood-neg-and-ign-vac", "3-Ranked-Neighbourhood-neg-and-ign-vac", "4-Ranked-Neighbourhood-neg-and-ign-vac", "1-Ranked-Neighbourhood-neg-and-ign-imm", "2-Ranked-Neighbourhood-neg-and-ign-imm", "3-Ranked-Neighbourhood-neg-and-ign-imm", "4-Ranked-Neighbourhood-neg-and-ign-imm" ->
                    true;
            default -> false;
        };
    }
}
