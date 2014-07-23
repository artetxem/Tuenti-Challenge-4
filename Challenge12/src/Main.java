/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    
    public static void main(String args[]) {
        final Scanner sc = new Scanner(System.in);
        final int t = sc.nextInt(); sc.nextLine();
        for (int it = 1; it <= t; it++) {
            final int m = sc.nextInt(), n = sc.nextInt(); sc.nextLine();
            final char[][] map = new char[n][];
            for (int i = 0; i < n; i++) map[i] = sc.nextLine().toCharArray();
            int startI = -1, startJ = -1, endI = -1, endJ = -1;
            final int nodes[][] = new int[m*n*4][2];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (map[i][j] == 'S') {
                        startI = i;
                        startJ = j;
                    }
                    if (map[i][j] == 'X') {
                        endI = i;
                        endJ = j;
                    }
                    final boolean upFree = i > 0 && map[i-1][j] != '#';
                    final boolean downFree = i < n-1 && map[i+1][j] != '#';
                    final boolean leftFree = j > 0 && map[i][j-1] != '#';
                    final boolean rightFree = j < m-1 && map[i][j+1] != '#';
                    nodes[encode(i,j,UP,m,n)][0] = upFree ? encode(i-1,j,UP,m,n) : -1;
                    nodes[encode(i,j,UP,m,n)][1] = rightFree ? encode(i,j+1,RIGHT,m,n) : -1;
                    nodes[encode(i,j,DOWN,m,n)][0] = downFree ? encode(i+1,j,DOWN,m,n) : -1;
                    nodes[encode(i,j,DOWN,m,n)][1] = leftFree ? encode(i,j-1,LEFT,m,n) : -1;
                    nodes[encode(i,j,LEFT,m,n)][0] = leftFree ? encode(i,j-1,LEFT,m,n) : -1;
                    nodes[encode(i,j,LEFT,m,n)][1] = upFree ? encode(i-1,j,UP,m,n) : -1;
                    nodes[encode(i,j,RIGHT,m,n)][0] = rightFree ? encode(i,j+1,RIGHT,m,n) : -1;
                    nodes[encode(i,j,RIGHT,m,n)][1] = downFree ? encode(i+1,j,DOWN,m,n) : -1;
                }
            }
            final List<Integer> distances = new ArrayList<>();
            distances.add(distance(nodes, encode(startI,startJ,UP,m,n), encode(endI,endJ,UP,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,UP,m,n), encode(endI,endJ,DOWN,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,UP,m,n), encode(endI,endJ,LEFT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,UP,m,n), encode(endI,endJ,RIGHT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,DOWN,m,n), encode(endI,endJ,UP,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,DOWN,m,n), encode(endI,endJ,DOWN,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,DOWN,m,n), encode(endI,endJ,LEFT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,DOWN,m,n), encode(endI,endJ,RIGHT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,LEFT,m,n), encode(endI,endJ,UP,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,LEFT,m,n), encode(endI,endJ,DOWN,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,LEFT,m,n), encode(endI,endJ,LEFT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,LEFT,m,n), encode(endI,endJ,RIGHT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,RIGHT,m,n), encode(endI,endJ,UP,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,RIGHT,m,n), encode(endI,endJ,DOWN,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,RIGHT,m,n), encode(endI,endJ,LEFT,m,n)));
            distances.add(distance(nodes, encode(startI,startJ,RIGHT,m,n), encode(endI,endJ,RIGHT,m,n)));
            final int distance = Collections.min(distances);
            if (distance != Integer.MAX_VALUE) {
                System.out.println("Case #" + it + ": " + distance);
            } else {
                System.out.println("Case #" + it + ": ERROR");
            }
        }
    }
    
    private static int distance(int nodes[][], int start, int end) {
        // Breadth First Search to calculate the minimum distance
        final Set<Integer> visited = new HashSet<>();
        final Queue<Integer> distances = new LinkedList<>();
        final Queue<Integer> q = new LinkedList<>();
        visited.add(start);
        distances.add(0);
        q.add(start);
        while (!q.isEmpty()) {
            final int p = q.poll();
            final int distance = distances.poll();
            if (p == end) {
                return distance;
            }
            if (nodes[p][0] != -1 && !visited.contains(nodes[p][0])) {
                visited.add(nodes[p][0]);
                q.add(nodes[p][0]);
                distances.add(distance+1);
            }
            if (nodes[p][1] != -1 && !visited.contains(nodes[p][1])) {
                visited.add(nodes[p][1]);
                q.add(nodes[p][1]);
                distances.add(distance+1);
            }
        }
        return Integer.MAX_VALUE;
    }
    
    private static int encode(int i, int j, int direction, int m, int n) {
        return 4*(i*m+j)+direction;
    }
    
}
