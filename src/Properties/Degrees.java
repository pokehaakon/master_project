package Properties;

import Graph.Graph;

public class Degrees implements GraphProperty {

    @Override
    public String getName() {
        return "degrees";
    }

    @Override
    public String property(Graph g) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < g.vertecies; i++) {
            s.append(g.getEnd(i) - g.getStart(i));
            if (i < g.vertecies - 1) {
                s.append(",");
            }
        }
        return s.toString();
    }
}
