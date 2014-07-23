/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    public static void main(String args[]) {
        
        // Parse the input
        final Scanner sc = new Scanner(System.in);
        final String src = sc.nextLine();
        final String trg = sc.nextLine();
        if (src.equals(trg)) { // Trivial case
            System.out.println(src);
            System.exit(0);
        }
        final List<String> states = new ArrayList<>();
        states.add(src);
        states.add(trg);
        while (sc.hasNextLine()) {
            final String state = sc.nextLine();
            if (!state.isEmpty()) states.add(state);
        }
        
        // Calculate the possible mutations from each state
        final Map<String, List<String>> mutations = new HashMap<>();
        for (String s1 : states) {
            final List<String> l = new ArrayList<>();
            for (String s2 : states) if (hammingDistance(s1, s2) == 1) l.add(s2);
            mutations.put(s1, l);
        }
        
        // Breadth First Search from the source state to the target state
        final Queue<List<String>> q = new LinkedList<>();
        q.add(Collections.singletonList(src));
        while (!q.isEmpty()) {
            final List<String> path = q.poll();
            for (String mutation : mutations.get(path.get(path.size()-1))) {
                if (mutation.equals(trg)) { // We've reached the end!
                    for (String state : path) System.out.print(state + "->");
                    System.out.println(trg);
                    System.exit(0);
                } else if (!path.contains(mutation)) { // Just to avoid cycles
                    final List<String> mutationPath = new ArrayList(path);
                    mutationPath.add(mutation);
                    q.add(mutationPath);
                }
            }
        }
        
        System.out.println("The shifting is not possible!!!"); // This shouldn't happen according to the wording...
    }
    
    
    private static int hammingDistance(String s1, String s2) {
        int distance = 0;
        final int len = Math.max(s1.length(), s2.length());
        for (int i = 0; i < len; i++) if (s1.charAt(i) != s2.charAt(i)) distance++;
        return distance;
    }
    
}