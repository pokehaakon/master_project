package Tools;

import java.util.BitSet;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.FloatVector;


public class FastBitSet extends BitSet {

    public static void main() {
        VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
        int length = SPECIES.length();
    }

    public FastBitSet () {

    }



}
