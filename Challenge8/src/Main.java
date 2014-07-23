/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    public static void main(String args[]) {
        
        // Breadth First Search to precalculate the days for every possible layout
        final Layout initialLayout = new Layout(new int[]{0,1,2,3,4,5,6,7,8});
        final Map<Layout,Integer> layoutToDays = new HashMap<>();
        final Queue<Layout> q = new LinkedList<>();
        layoutToDays.put(initialLayout, 0);
        q.add(initialLayout);
        while (!q.isEmpty()) {
            final Layout layout = q.poll();
            final int days = layoutToDays.get(layout);
            for (Layout neighbor : layout.neighborhood()) {
                if (!layoutToDays.containsKey(neighbor)) {
                    layoutToDays.put(neighbor, days+1);
                    q.add(neighbor);
                }
            }
        }
        
        // Now parse the input and simply write the precalculated results
        final Scanner sc = new Scanner(System.in);
        final int t = sc.nextInt(); sc.nextLine();
        for (int it = 0; it < t; it++) {
            final String[] l1 = new String[9];
            sc.nextLine();
            String[] line = sc.nextLine().split(",");
            l1[0] = line[0].trim(); l1[1] = line[1].trim(); l1[2] = line[2].trim();
            line = sc.nextLine().split(",");
            l1[3] = line[0].trim(); l1[4] = line[1].trim(); l1[5] = line[2].trim();
            line = sc.nextLine().split(",");
            l1[6] = line[0].trim(); l1[7] = line[1].trim(); l1[8] = line[2].trim();
            final String[] l2 = new String[9];
            sc.nextLine();
            line = sc.nextLine().split(",");
            l2[0] = line[0].trim(); l2[1] = line[1].trim(); l2[2] = line[2].trim();
            line = sc.nextLine().split(",");
            l2[3] = line[0].trim(); l2[4] = line[1].trim(); l2[5] = line[2].trim();
            line = sc.nextLine().split(",");
            l2[6] = line[0].trim(); l2[7] = line[1].trim(); l2[8] = line[2].trim();
            final Integer days = layoutToDays.get(new Layout(l1, l2));
            System.out.println(days == null ? "-1" : days);
        }
        
    }
    
    
    
    private static class Layout {
        
        private final int[] layout;
        
        public Layout(int[] layout) {
            this.layout = Arrays.copyOf(layout, 9);
        }
        
        public Layout(Object[] src, Object[] trg) {
            this.layout = new int[9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (src[i].equals(trg[j])) layout[j] = i;
                }
            }
        }
        
        public Layout swap(int i, int j) {
            final Layout res = new Layout(this.layout);
            res.layout[i] = this.layout[j];
            res.layout[j] = this.layout[i];
            return res;
        }
        
        public Iterable<Layout> neighborhood() {
            final Layout[] neighborhood = {
                swap(0,1), swap(1,2), swap(3,4), swap(4,5), swap(6,7), swap(7,8), // Horizontal swaps
                swap(0,3), swap(3,6), swap(1,4), swap(4,7), swap(2,5), swap(5,8)  // Vertical swaps
            };
            return Arrays.asList(neighborhood);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Arrays.hashCode(this.layout);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Layout other = (Layout) obj;
            if (!Arrays.equals(this.layout, other.layout)) {
                return false;
            }
            return true;
        }
        
    }
    
}
