package VectorTest;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;

public class Vec_and {
    private final IntVector a, b;

    public Vec_and(IntVector a, IntVector b) {
        this.a = a;
        this.b = b;
    }
    public void run() {
        a.and(b);
    }
}
