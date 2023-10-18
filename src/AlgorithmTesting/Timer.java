package AlgorithmTesting;

import java.util.ArrayList;
import java.util.List;

public class Timer {
    public Timer() {}

    public String TimeFunction(Runnable f, String name, int runs) {
        List<Long> times = new ArrayList<>();
        for (int r = 0; r < runs; r++) {
            long t1 = System.nanoTime();
            f.run();
            long t2 = System.nanoTime();
            times.add(t2-t1);
            //System.out.println(t2-t1);
        }
        long sum = 0, lmin = times.get(0), lmax = times.get(0);
        for (Long l : times) {
            sum += l;
            if (l < lmin) lmin = l;
            if (l > lmax) lmax = l;
        }
        double s = sum/1000_000.0;
        String min = String.format("%.2f", lmin/1000_000.0);
        String max = String.format("%.2f", lmax/1000_000.0);
        String delta = String.format("%.2f", (lmax-lmin)/1000_000.0);
        String str = String.format(
                "name:" + lengthen(name, 10) + ";\t"
                + "runs : " + times.size() + ";\t"
                + "total : " + s + " ms;\t"
                + "avg : " + String.format("%.2f", s/times.size()) + " ms;\t"
                + "min : " + min + " ms;\t"
                + "max : " + max + " ms;\t"
                + "delta : " + delta + " ms"
        );
        //System.out.println(str);
        return str.replace("\t", "").replace(" ", "").replace("ms", "");
    }

    public String lengthen(String s, int n) {
        if (s.length() >= n) {
            return s;
        }
        return s + new String(new char[n-s.length()]).replace('\0', ' ');
    }
}
