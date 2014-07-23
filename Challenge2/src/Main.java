/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    
    
    public static void main(String args[]) {
        
        final Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            
            final String line = sc.nextLine(); if (line.isEmpty()) continue;
            final char[] code = (line+line).toCharArray(); // Duplicate the line to start from the '#'
            
            // Calculate the bounds
            int i = -1, dir = RIGHT, x = 0, y = 0, minX = 0, maxX = 0, minY = 0, maxY = 0;
            while (code[++i] != '#') {} // Skip up to the '#'
            while (code[++i] != '#') {
                if (dir == RIGHT) x++;
                else if (dir == LEFT) x--;
                else if (dir == UP) y--;
                else if (dir == DOWN) y++;
                switch (code[i]) {
                    case '/':
                        if (dir == RIGHT) dir = UP;
                        else if (dir == LEFT) dir = DOWN;
                        else if (dir == UP) dir = RIGHT;
                        else if (dir == DOWN) dir = LEFT;
                        break;
                    case '\\':
                        if (dir == RIGHT) dir = DOWN;
                        else if (dir == LEFT) dir = UP;
                        else if (dir == UP) dir = LEFT;
                        else if (dir == DOWN) dir = RIGHT;
                        break;
                }
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            
            // Build the track
            final char[][] track = new char[maxY-minY+1][maxX-minX+1];
            for (int k = 0; k < maxY-minY+1; k++) for (int l = 0; l < maxX-minX+1; l++) track[k][l] = ' ';
            track[-minY][-minX] = '#';
            i = -1; dir = RIGHT; x = 0; y = 0;
            while (code[++i] != '#') {} // Skip up to the '#'
            while (code[++i] != '#') {
                if (dir == RIGHT) x++;
                else if (dir == LEFT) x--;
                else if (dir == UP) y--;
                else if (dir == DOWN) y++;
                char pixel = ' ';
                switch (code[i]) {
                    case '-':
                        if (dir == RIGHT || dir == LEFT) pixel = '-';
                        else if (dir == UP || dir == DOWN) pixel = '|';
                        break;
                    case '/':
                        pixel = '/';
                        if (dir == RIGHT) dir = UP;
                        else if (dir == LEFT) dir = DOWN;
                        else if (dir == UP) dir = RIGHT;
                        else if (dir == DOWN) dir = LEFT;
                        break;
                    case '\\':
                        pixel = '\\';
                        if (dir == RIGHT) dir = DOWN;
                        else if (dir == LEFT) dir = UP;
                        else if (dir == UP) dir = LEFT;
                        else if (dir == DOWN) dir = RIGHT;
                        break;
                }
                track[y-minY][x-minX] = pixel;
            }
            
            // Print the track
            for (int k = 0; k < maxY-minY+1; k++) System.out.println(new String(track[k]));
            
        }
    }
    
}
