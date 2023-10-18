package main;

import AlgorithmTesting.DistanceLayers;
import AlgorithmTesting.KNeighbourhood;
import AlgorithmTesting.Timer;
import Graph.Graph;
import Graph.Edge;
import Plans.Plan;
import Plans.PlanGenerator;
import Simulation.Simulation;
import Tools.Eccentricity;
import Tools.KNeighbourhoodConstructor;
import Tools.Tools;
import VectorTest.Vec_and;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorSpecies;


import java.util.*;

import static Tools.Tools.*;
import static java.lang.System.exit;

public class TimeTest {
    public static void main(String[] args) {

        System.out.println(System.getProperty("java.version"));
        System.out.println(Runtime.class.getPackage().getImplementationVersion());
        System.out.println(Runtime.version().toString());
        exit(1);

        List<Integer> nodes = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5));
        List<int[]> keys = new ArrayList<>();
        keys.add(new int[] {9,5}); //0
        keys.add(new int[] {5,5}); //1
        keys.add(new int[] {3,2}); //2
        keys.add(new int[] {3,3}); //3
        keys.add(new int[] {6,8}); //4
        keys.add(new int[] {13,3});//5

        //expected order
        //5, 0, 4, 1, 3, 2
        nodes.sort((a, b) -> - Tools.arrayCompare(keys.get(a), (keys.get(b))));
        System.out.println(nodes);

        exit(1);
        Graph g = readGraph("testgraph.txt");
        //DistanceLayers.VectorImplementation vecImp = new DistanceLayers.VectorImplementation();
        KNeighbourhood.VectorImplementation vecImp = new KNeighbourhood.VectorImplementation();


        BitSet ignore = new BitSet(g.vertecies);
        //ignore.set(0, 10, false);
        ignore.set(1, true);
        ignore.set(3, true);
        System.out.println(ignore.cardinality());

        System.out.println("\t" + "09876543210");
        IntVector[] distanceLayers = vecImp.computeIgnore(g, 4, ignore);
        int i = -1;
        for (IntVector nh : distanceLayers) {
            i++;
            if (nh == null) continue;
            System.out.println(i + "\t" + pad(Integer.toBinaryString(nh.lane(0)), 11));
        }
        System.out.println("\t" + "09876543210");
        for (int j = 0; j < 11; j++) {
            System.out.println(j + " " + getVectorValue(distanceLayers[0], j));
        }


    }


    public static String pad(String s, int pad) {
        if (s.length() == pad) {
            return s;
        }
        return "0".repeat(pad-s.length()) + s;
    }
}
