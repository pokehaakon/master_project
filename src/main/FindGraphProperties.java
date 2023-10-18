package main;

import AlgorithmTesting.Printer;
import Graph.Graph;
import Properties.Degrees;
import Properties.Diameter;
import Properties.Eccentricities;
import Properties.GraphProperty;

import java.util.ArrayList;
import java.util.List;

import static Tools.Tools.readGraph;
import static main.Timings.graphinfo;

public class FindGraphProperties {
    public static void main(String[] args) {
        List<String> rawGraphNames = new ArrayList<>();
        List<String> graphNames = new ArrayList<>();
        Printer p = new Printer("properties2.txt");

        int[] sizes = new int[]{2000, 4000, 6000, 8000, 10000};
        int[] denss = new int[]{10, 20, 30};
        int[] diams = new int[]{32, 128};

        for (int size : sizes) {
            for (int dens : denss) {
                for (int i = 0; i<3; i++) {
                    for (int diam : diams) {
                        if (size == 2000 && dens == 30 && diam == 128) continue;
                        rawGraphNames.add("GEO_" + size + "_" + dens + "_" + diam + "_" + i);
                    }
                    rawGraphNames.add("SWG_" + size + "_" + dens + "_0-0505_" + i);
                    rawGraphNames.add("RND_" + size + "_" + dens + "_" + i);
                }
            }
        }


        for (String rgn : rawGraphNames) {
            if (rgn.endsWith("_")) {
                for (int i = 0; i < 5; i++) {
                    graphNames.add(rgn+i);
                }
            } else {
                graphNames.add(rgn);
            }
        }

        GraphProperty[] gProperties = new GraphProperty[] {
                new Diameter(),
                new Degrees(),
                new Eccentricities()
        };

        findGraphProperties(graphNames, gProperties, p);
    }

    public static void findGraphProperties(List<String> graphnames, GraphProperty[] properties, Printer p) {
        for (String gName : graphnames) {
            Graph g = readGraph(gName);
            System.out.println(gName);
            for (GraphProperty prop : properties) {
                String s = "property:" + prop.getName() + ";"
                         + graphinfo(g, gName)
                         + "value:" + prop.property(g);
                        ;
                p.print(s);
            }
        }
    }
}
