/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.util.*;

public class Main {
    
    private static final int MAX_PHONE_NUMBER = 1000000000;
    
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("USAGE: java -jar Challenge7.jar phone_call.log");
            System.exit(-1);
        }
        try {
            final Scanner in = new Scanner(System.in), log = new Scanner(new File(args[0]));
            final int terroristA = in.nextInt(), terroristB = in.nextInt();
            final int[] partition = new int[MAX_PHONE_NUMBER];
            int index = 0;
            while (log.hasNextInt()) {
                final int p1 = log.nextInt(), p2 = log.nextInt();
                union(partition, tag(partition, p1), tag(partition, p2));
                if (tag(partition, terroristA) == tag(partition, terroristB)) {
                    System.out.println("Connected at " + index);
                    System.exit(0);
                }
                index++;
            }
            System.out.println("Not connected");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + args[0]);
            System.exit(-1);
        }
    }
    
    private static int tag(int[] partition, int elem) {
        int tag = elem;
        while (partition[tag] > 0) tag = partition[tag];
        return tag;
    }
    
    private static void union(int[] partition, int tag1, int tag2) {
        if (tag1 == tag2) {
            // NOP
        } else if (partition[tag1] == partition[tag2]) {
            partition[tag1]--;
            partition[tag2] = tag1;
        } else if (partition[tag1] < partition[tag2]) {
            partition[tag2] = tag1;
        } else {
            partition[tag1] = tag2;
        }
    }
    
}
