package Tools;

import Simulation.Report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Reporter {
    String filepath;
    String prefix;
    public Reporter(String filepath) {
        this.filepath = filepath;
        this.prefix = "";
    }

    public Reporter(String filepath, String prefix) {
        this.filepath = filepath;
        this.prefix = prefix;
    }

    public void report(Report rep, String name) {
        try {
            FileWriter writer = new FileWriter(prefix + filepath, true);
            //BufferedWriter bwriter = new BufferedWriter(writer);
            writer.append(name).append("\n").append(rep.toCSV()).append("\n;\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
