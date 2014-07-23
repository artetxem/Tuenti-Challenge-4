/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    private static final int MAX_N = 1000000000; // 10^9
    private static final int ITERATIONS = 7;
    private static final long M = 3211123;
    
    public static void main(String args[]) { // I know that the code is extremely ugly, but it's late and I need to sleep ;)
        
        // Precalculate all the results using dynamic programming
        final long[] results = new long[(int)MAX_N+1];
        final long[] prevSol = new long[ITERATIONS+1];
        final long[] sol = new long[ITERATIONS+1];
        final long[] prevKonb = new long[ITERATIONS+1];
        final long[] konb = new long[ITERATIONS+1];
        final long[] prevKen = new long[ITERATIONS+1];
        final long[] ken = new long[ITERATIONS+1];
        final long[] prevBat = new long[ITERATIONS+1];
        final long[] bat = new long[ITERATIONS+1];
        for (long n = 0; n <= MAX_N; n++) {
            for (int it = 1; it <= ITERATIONS; it++) {
                if (n == 0) konb[it] = 0;
                else if (it == 1) konb[it] = 1;
                else konb[it] = (prevKonb[it] + prevKonb[it-1]) % M;
                if (n == 0 || n == 1) bat[it] = 0;
                else bat[it] = (prevBat[it] + 2*prevKonb[it]) % M;
                if (n == 0 || n == 1) ken[it] = 0;
                else ken[it] = (prevKen[it] + 2*prevKonb[it-1]*n + prevBat[it-1] - 2*(n-1)*prevKonb[it-1] - prevKonb[it-1]) % M;
                if (n == 0) sol[it] = 0;
                else if (it == 1) sol[it] = (n*n) % M;
                else sol[it] = (prevSol[it] + prevSol[it-1] + ken[it]) % M;
            }
            System.arraycopy(bat, 0, prevBat, 0, bat.length);
            System.arraycopy(konb, 0, prevKonb, 0, konb.length);
            System.arraycopy(ken, 0, prevKen, 0, ken.length);
            System.arraycopy(sol, 0, prevSol, 0, sol.length);
            results[(int)n] = sol[ITERATIONS];
        }
        
        // Simply sum and print the precalculated results for the given input
        long res = 0;
        final Scanner sc = new Scanner(System.in);
        while (sc.hasNextInt()) res = (res + results[sc.nextInt()]) % M;
        System.out.println(res);
    }
    
}
