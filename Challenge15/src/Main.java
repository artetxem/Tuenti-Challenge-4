/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    private static final byte WHITE = 0, BLACK = 1, EMPTY = 2;
    
    public static void main(String args[]) {
        final Scanner sc = new Scanner(System.in);
        final int n = sc.nextInt(); sc.nextLine();
        for (int it = 0; it < n; it++) {
            final String[] aux = sc.nextLine().split(" ");
            final byte turn = aux[0].equalsIgnoreCase("White") ? WHITE : BLACK;
            final int moves = Integer.parseInt(aux[2]);
            final byte[][] board = new byte[8][8];
            for (int i = 0; i < 8; i++) {
                final String line = sc.nextLine();
                for (int j = 0; j < 8; j++) {
                    board[i][j] = line.charAt(j) == 'O' ? WHITE : line.charAt(j) == 'X' ? BLACK : EMPTY;
                }
            }
            final State initialState = new State(board, turn);
            
            boolean found = false;
            for (int i = 0; i < 8 && !found; i++) {
                for (int j = 0; j < 8 && !found; j++) {
                    if (solve(initialState, moves, i, j)) {
                        found = true;
                        System.out.println((char)('a'+j) + "" + (1+i));
                    }
                }
            }
            if (!found) System.out.println("Impossible");
        }
    }
    
    
    private static boolean solve(State state, int moves, int x, int y) { // x == -1 for null move
        final State nextState = (x == -1) ? new State(state.board, state.turn == WHITE ? BLACK : WHITE) : state.playDisc(x, y);
        if (nextState == null) {
            return false;
        } else if (moves == 1) {
            return  (x == 0 || x == 7) && (y == 0 || y == 7);
        } else {
            for (State neighbor : nextState.neighbors()) {
                boolean found = false;
                boolean played = false;
                for (int i = 0; i < 8 && !found; i++) {
                    for (int j = 0; j < 8 && !found; j++) {
                        if (!played && neighbor.canPlayDisc(i, j)) played = true;
                        found = solve(neighbor, moves-1, i, j);
                    }
                }
                if (!played) found = solve(neighbor, moves-1, -1, -1);
                if (!found) return false;
            }
            return true;
        }
    }
    
    
    private static class State {
        
        public final byte[][] board = new byte[8][8];
        public final byte turn;
        private List<State> neighbors;
        
        public State(byte[][] board, byte turn) {
            for (int i = 0; i < 8; i++) System.arraycopy(board[i], 0, this.board[i], 0, 8);
            this.turn = turn;
        }
        
        public Iterable<State> neighbors() {
            return new Iterable<State>() {
                @Override public Iterator<State> iterator() {
                    if (neighbors == null) initNeighbors();
                    return neighbors.iterator();
                }
            };
        }
        
        private void initNeighbors() {
            neighbors = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    final State neighbor = playDisc(i, j);
                    if (neighbor != null) neighbors.add(neighbor);
                }
            }
            if (neighbors.isEmpty()) neighbors.add(new State(board, turn == WHITE ? BLACK : WHITE));
        }
        
        public State playDisc(int x, int y) {
            if (board[x][y] != EMPTY) return null;
            final State nextState = new State(board, turn == WHITE ? BLACK : WHITE);
            nextState.board[x][y] = turn;
            boolean flipped = false;
            for (int dirX = -1; dirX <= 1; dirX++) {
                for (int dirY = -1; dirY <= 1; dirY++) {
                    if (dirX != 0 || dirY != 0) {
                        for (int offset = 1; ; offset++) {
                            final int i = x + dirX*offset;
                            final int j = y + dirY*offset;
                            if (i < 0 || i >= 8 || j < 0 || j >= 8 || board[i][j] == EMPTY) {
                                break;
                            } else if (board[i][j] == turn) {
                                for (int o = 1; o < offset; o++) {
                                    nextState.board[x+dirX*o][y+dirY*o] = turn;
                                    flipped = true;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            return flipped ? nextState : null;
        }
        
        public boolean canPlayDisc(int x, int y) {
            return playDisc(x, y) != null;
        }
        
    }
    
}
