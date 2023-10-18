package Tools;

import Graph.Graph;

import java.util.List;
import java.util.Map;

public record SharedData(List<Graph> graphs, int total, String filepattern, int lastUnder, Map<String, List<List<Integer>>> centralities, Map<String, List<Double>> simpleCentralities,
                         Map<String, Long> timings, Reporter reporter, int numberSimsEach) {
    public SharedData replaceGraphs(List<Graph> graphs) {
        return new SharedData(graphs, total, filepattern, lastUnder, centralities, simpleCentralities, timings, reporter, numberSimsEach);
    }
}
