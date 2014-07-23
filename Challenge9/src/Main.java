/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    // No need to reinvent the wheel: the maximum flow implementation has been taken from http://web.mit.edu/~ecprice/acm/acm08/
    public static void main(String args[]) {
        final MinCostMaxFlow flow = new MinCostMaxFlow();
        final Scanner sc = new Scanner(System.in);
        final int c = sc.nextInt();
        for (int it = 0; it < c; it++) {
            final String city = sc.next();
            final int s = sc.nextInt();
            final int d = sc.nextInt();
            final int i = sc.nextInt();
            final int r = sc.nextInt();
            final int[][] capacity = new int[i+2][i+2];
            final int[][] cost = new int[i+2][i+2];
            for (int k = 0; k < i+2; k++) for (int l = 0; l < i+2; l++) cost[k][l] = 1;
            for (int k = 0; k < r; k++) {
                final String fromName = sc.next();
                final String toName = sc.next();
                final String type = sc.next();
                final int lanes = sc.nextInt();
                final int from = fromName.equals(city) ? 0 : fromName.equals("AwesomeVille") ? 1 : Integer.parseInt(fromName)+2;
                final int to = toName.equals(city) ? 0 : toName.equals("AwesomeVille") ? 1 : Integer.parseInt(toName)+2;
                capacity[from][to] += 200 * lanes * (type.equals("normal") ? s : d);
            }
            System.out.println(city + " " + flow.getMaxFlow(capacity, cost, 0, 1)[0]);
        }
    }
    
}
