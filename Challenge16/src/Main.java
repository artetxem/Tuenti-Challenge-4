/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.util.*;

public class Main {
    
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("USAGE: java -jar Challenge16.jar points");
            System.exit(-1);
        }
        try {
            // Parse input
            final String input[] = new Scanner(System.in).nextLine().split(",");
            final int n = Integer.parseInt(input[0]), len = Integer.parseInt(input[1]);
            final Scanner sc = new Scanner(new File(args[0]));
            final Point[] points = new Point[len];
            for (int i = 1; i < n; i++) sc.nextLine();
            for (int i = 0; i < len; i++) points[i] = new Point(sc.nextLong(), sc.nextLong(), sc.nextLong());
            
            // Sort the points in the X axis
            Arrays.sort(points, new Comparator<Point>(){
                @Override public int compare(Point o1, Point o2) {
                    return Long.compare(o1.x, o2.x);
                }
            });
            
            long result = 0;
            for (int i = 0; i < len; i++) {
                // Analyze all the points that are up to 1000 units to the right from the current one
                // (the maximum radius is 500, so no more intersection could be found by moving further away)
                for (int j = i+1; j < len && points[j].x-points[i].x <= 1000; j++) {
                    if (points[i].intersects(points[j])) result++;
                }
            }
            System.out.println(result);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + args[0]);
            System.exit(-1);
        }
    }
    
    private static class Point {
        public final long x, y, r;
        public Point(long x, long y, long r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
        public boolean intersects(Point p) {
            return (this.x-p.x)*(this.x-p.x) + (this.y-p.y)*(this.y-p.y) < (this.r+p.r)*(this.r+p.r);
        }
    }
    
}
