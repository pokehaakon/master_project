package AlgorithmTesting;

import java.io.FileWriter;
import java.io.IOException;

public class Printer {
    String filepath;
    public Printer(String filepath) {
        this.filepath = filepath;
    }

    public void print(String str) {
        try {
            FileWriter writer = new FileWriter(filepath, true);
            //BufferedWriter bwriter = new BufferedWriter(writer);
            writer.append(str).append("\n");
            //writer.append(name).append("\n").append(rep.toCSV()).append("\n;\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
