/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

public class Main {
    
    private static final long B = 10000000; // B = 10^7 since we know that password < 10^14
    
    public static void main(String args[]) throws MalformedURLException, IOException {
        final Scanner sc = new Scanner(new URL("http://54.83.207.90:9083/index.py?" + new Scanner(System.in).nextLine() + ":auskalo").openStream());
        sc.nextLine();
        final BigInteger g = sc.nextBigInteger();
        final BigInteger p = sc.nextBigInteger();
        final BigInteger h = sc.nextBigInteger();
        System.out.println(computeDiscreteLog(g,p,h));
    }
    
    // Based on http://groglogs.blogspot.com.es/2013/12/java-compute-discrete-logarithm.html
    private static long computeDiscreteLog(BigInteger g, BigInteger p, BigInteger h) {
        final Map<BigInteger, Long> m = new HashMap<>();
        for (long i = 0; i < B; i++) m.put(h.multiply(g.modPow(BigInteger.valueOf(i), p).modInverse(p)).mod(p), i);        
        final BigInteger gb = g.modPow(BigInteger.valueOf(B), p);
        for (long i = 0; i < B; i++) {
            final BigInteger n = gb.modPow(BigInteger.valueOf(i), p);
            if (m.containsKey(n)) return i*B + m.get(n);
        }
        return -1; // Fail :(
    }
    
}
