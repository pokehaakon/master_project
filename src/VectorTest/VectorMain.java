package VectorTest;


import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorSpecies;

public class VectorMain {
    public static void main(String[] args) {

        Timer timer = new Timer();

        int n = 1_000;
        int[] a = new int[n];
        a[0] = 1;

        IntVector vec_a = IntVector.fromArray(IntVector.SPECIES_256, a, 0);
        System.out.println(vec_a.lane(0));

        int[] b = vec_a.toArray();

        System.out.println(b[0]);


    }
}
