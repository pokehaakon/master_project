package Simulation;

import java.util.ArrayList;
import java.util.List;

public class Report {
    public final List<Integer> newCases;
    public final List<Integer> cumulative;
    public final List<Integer> current;
    public final List<Integer> realCumulative;

    public Report() {
        cumulative = new ArrayList<>();
        newCases = new ArrayList<>();
        current = new ArrayList<>();
        realCumulative = new ArrayList<>();
    }

    public String toCSV() {
        return "cumulative" + "\t" + String.join("\t", cumulative.stream().map(Object::toString).toList()) + "\n" +
                "new" + "\t" + String.join("\t", newCases.stream().map(Object::toString).toList()) + "\n" +
                "current" + "\t" + String.join("\t", current.stream().map(Object::toString).toList()) + "\n" +
                "realCumulative" + "\t" + String.join("\t", realCumulative.stream().map(Object::toString).toList());
    }
}
