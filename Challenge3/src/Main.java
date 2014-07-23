/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    public static void main(String args[]) {
        final Scanner sc = new Scanner(System.in);
        final int n = sc.nextInt();
        for (int it = 0; it < n; it++) {
            final int x = sc.nextInt();
            final int y = sc.nextInt();
            final String s = String.format("%.02f", Math.sqrt(x*x+y*y));
            System.out.println(s.replaceAll("\\.?0+$", ""));
        }
    }
    
}
