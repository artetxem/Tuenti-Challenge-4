/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    private static final int WIDTH = 8, HEIGHT = 8;
    
    
    public static void main(String args[]) {
        
        // Parse the initial generation
        final Scanner sc = new Scanner(System.in);
        boolean[][] initialGeneration = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            final String line = sc.nextLine();
            for (int j = 0; j < WIDTH; j++) {
                initialGeneration[i][j] = line.charAt(j) == 'X';
            }
        }
        
        // Simulate evolution until we find a loop
        final List<boolean[][]> generations = new ArrayList<>();
        generations.add(initialGeneration);
        int loopLength;
        while ((loopLength = loopLength(generations)) == -1) {
            generations.add(nextGeneration(generations.get(generations.size()-1)));
        }
        System.out.println((generations.size()-loopLength-1) + " " + loopLength);
    }
    
    
    private static boolean[][] nextGeneration(boolean[][] currentGeneration) {
        final boolean[][] nextGeneration = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                final int neighbors = countNeighbors(currentGeneration, i, j);
                nextGeneration[i][j] = (currentGeneration[i][j] && (neighbors == 2 || neighbors == 3)) ||
                        (!currentGeneration[i][j] && neighbors == 3);
            }
        }
        return nextGeneration;
    }
    
    
    private static int countNeighbors(boolean[][] generation, int i, int j) {
        int neighbors = 0;
        if (i > 0 && j > 0 && generation[i-1][j-1]) neighbors++;
        if (i > 0 && generation[i-1][j]) neighbors++;
        if (i > 0 && j < WIDTH-1 && generation[i-1][j+1]) neighbors++;
        if (j > 0 && generation[i][j-1]) neighbors++;
        if (j < WIDTH-1 && generation[i][j+1]) neighbors++;
        if (i < HEIGHT-1 && j > 0 && generation[i+1][j-1]) neighbors++;
        if (i < HEIGHT-1 && generation[i+1][j]) neighbors++;
        if (i < HEIGHT-1 && j < WIDTH-1 && generation[i+1][j+1]) neighbors++;
        return neighbors;
    }
    
    
    /**
     * Returns the length of the loop in the given list, or -1 if the list doesn't loop.
     * 
     * @return the length of the loop in the given list, or -1 if the list doesn't loop.
     */
    private static int loopLength(List<boolean[][]> l) {
        if (l.size() < 2) return -1;
        final boolean[][] lastElement = l.get(l.size()-1);
        for (int i = l.size()-2; i >= 0; i--) {
            if (Arrays.deepEquals(l.get(i), lastElement)) return l.size() - i - 1;
        }
        return -1;
    }
    
}