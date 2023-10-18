package main;


import AlgorithmTesting.*;
import Graph.Graph;
import Simulation.Simulation;
import Tools.Eccentricity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Tools.Tools.readGraph;

public class Timings {
    public static void main(String[] args) {
        Timer timer = new Timer();
        List<String> rawGraphNames = new ArrayList<>();
        List<String> graphNames = new ArrayList<>();
        Printer p = new Printer("timings_java_19_vec.txt");
        int runs = 3;

        int[] sizes = new int[]{2000, 4000, 6000, 8000, 10000};
        int[] denss = new int[]{10,20, 30};
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

        System.out.println(Runtime.getRuntime().maxMemory());
        System.out.println(Long.MAX_VALUE);

        //System.out.println("Starting distance layers");
        //timeDistanceLayers(timer, graphNames, runs, p);

        System.out.println("Starting k-neighbourhoods");
        timeKNeighbourhood(new int[]{2,3,4}, timer, graphNames, runs, p);

        //System.out.println("Starting distance layers");
        //betweeness(timer, graphNames, runs, p);

        //System.out.println("Starting betweeness and closeness");
        //betweenessAndCloseness(timer, graphNames, runs, p);


    }

    public static void timeDistanceLayers(Timer timer, List<String> graphNames, int runs, Printer p) {
        List<IDistanceLayers> calculators = DistanceLayers.getCalculators();
        String str, ostr;
        for (String gName : graphNames) {
            Graph g = readGraph(gName);
            System.out.println(gName);
            str = "alg:DL;" + graphinfo(g, gName);;
            for (IDistanceLayers calc : calculators) {
                if (calc.getName() != "Vec") continue;
                ostr = str + timer.TimeFunction(() -> calc.compute(g), calc.getName(), runs);
                //System.out.println(ostr);
                p.print(ostr);
            }
            //System.out.println("");
        }
    }

    public static void timeKNeighbourhood(int[] ks, Timer timer, List<String> graphNames, int runs, Printer p) {
        List<IKNeighourhood> calculators = KNeighbourhood.getCalculators();
        String str, ostr;
        for (String gName : graphNames) {
            Graph g = readGraph(gName);
            System.out.println( "KNH " + gName);
            for (int k : ks) {
                //System.out.println("k = " + k);
                for (IKNeighourhood calc : calculators) {
                    str = "alg:KNH;k:" + k + ";" + graphinfo(g, gName);
                    ostr = str + timer.TimeFunction(() -> calc.compute(g, k), calc.getName(), runs);
                    //System.out.println(ostr);
                    p.print(ostr);
                }
                //System.out.println("");
            }
        }
    }

    public static void betweeness(Timer timer, List<String> graphNames, int runs, Printer p) {
        List<IBetweeness> calculators = Betweeness.getCalculators();
        String str, ostr;
        for (String gName : graphNames) {
            Graph g = readGraph(gName);
            System.out.println(gName);
            for (IBetweeness calc : calculators) {
                str = "alg:BTW;" + graphinfo(g, gName);
                ostr = str + timer.TimeFunction(() -> calc.compute(g), calc.getName(), runs);
                System.out.println(ostr);
                p.print(ostr);
            }
            System.out.println("");
        }
    }

    public static void betweenessAndCloseness(Timer timer, List<String> graphNames, int runs, Printer p) {
        List<IBetweenessAndCloseness> calculators = BetweenessAndCloseness.getCalculators();
        String str, ostr;
        int i = 0;
        for (String gName : graphNames) {
            Graph g = readGraph(gName);
            System.out.println("BNC " + gName);
            for (IBetweenessAndCloseness calc : calculators) {
                if (!Objects.equals(calc.getName(), "VecB")) continue;
                str = "alg:BNC;" + graphinfo(g, gName);
                ostr = str + timer.TimeFunction(() -> calc.compute(g), calc.getName(), runs);
                System.out.println(ostr);
                p.print(ostr);
                i++;
                //if (i == 8) return;
            }
            //System.out.println("");
        }
    }

    public static String graphinfo(Graph g, String gName) {
        Pattern pat = Pattern.compile("_\\d+_");
        Matcher m = pat.matcher(gName.substring(4, gName.length()));
        if (!m.find()) {
            System.out.println(gName);
        }
        String str = "graphtype:" +  gName.substring(0,3) + ";"
                + "size:" + g.vertecies + ";"
                + "dens:" + m.group(0).replace("_", "") + ";"
                ;

        if (gName.startsWith("GEO")) {
            String diam = gName.split("_")[3];
            str += "diam:" + diam + ";";
        }
        if (g.alpha != 0.0) {
            str += "alpha:" + g.alpha + ";";
        }

        return str;
    }

}