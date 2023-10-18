package VectorTest;

public class Arr_and {
    private final int[] a, b;

    public Arr_and(int[] a, int[] b) {
        this.a = a;
        this.b = b;
    }
    public void run() {
        for (int i = 0; i < a.length; i++) {
            a[i] &= b[i];
        }
    }
}