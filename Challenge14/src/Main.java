/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.util.*;

public class Main {
    
    private static final int[] wagonDestinations = new int[8];
    private static final int[] wagonValues = new int[8];
    private static final double[][] train1Distances = new double[8][8];
    private static final double[][] train2Distances = new double[8][8];
    
    public static void main(String args[]) {
        final Scanner sc = new Scanner(System.in);
        final int n = sc.nextInt(); sc.nextLine();
        for (int it = 0; it < n; it++) {
            // Parse input
            String line[] = sc.nextLine().split(",");
            final int s = Integer.parseInt(line[0]);
            final int r = Integer.parseInt(line[1]);
            final int f = Integer.parseInt(line[2]);
            final int[] coordX = new int[8];
            final int[] coordY = new int[8];
            final Map<String, Integer> nameToIndex = new TreeMap<>();
            int nextIndex = 0;
            for (int i = 0; i < s; i++) {
                line = sc.nextLine().split(" ");
                Integer station = nameToIndex.get(line[0]);
                if (station == null) {
                    nameToIndex.put(line[0], nextIndex);
                    station = nextIndex;
                    nextIndex++;
                }
                final String[] coordinates = line[1].split(",");
                final int x = Integer.parseInt(coordinates[0]);
                final int y = Integer.parseInt(coordinates[1]);
                Integer wagonDestination = nameToIndex.get(line[2]);
                if (wagonDestination == null) {
                    nameToIndex.put(line[2], nextIndex);
                    wagonDestination = nextIndex;
                    nextIndex++;
                }
                int wagonValue = Integer.parseInt(line[3]);
                coordX[station] = x;
                coordY[station] = y;
                wagonDestinations[station] = wagonDestination;
                wagonValues[station] = wagonValue;
            }
            int train1Position = 0, train2Position = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    train1Distances[i][j] = Double.MAX_VALUE;
                    train2Distances[i][j] = Double.MAX_VALUE;
                }
            }
            if (r >= 1) {
                line = sc.nextLine().split(" ");
                train1Position = nameToIndex.get(line[0]);
                for (int i = 1; i < line.length; i++) {
                    final String[] connection = line[i].split("-");
                    final int s1 = nameToIndex.get(connection[0]);
                    final int s2 = nameToIndex.get(connection[1]);
                    train1Distances[s1][s2] = train1Distances[s2][s1] = Math.sqrt((coordX[s1]-coordX[s2])*(coordX[s1]-coordX[s2])+(coordY[s1]-coordY[s2])*(coordY[s1]-coordY[s2]));
                }
            }
            if (r >= 2) {
                line = sc.nextLine().split(" ");
                train2Position = nameToIndex.get(line[0]);
                for (int i = 1; i < line.length; i++) {
                    final String[] connection = line[i].split("-");
                    final int s1 = nameToIndex.get(connection[0]);
                    final int s2 = nameToIndex.get(connection[1]);
                    train2Distances[s1][s2] = train2Distances[s2][s1] = Math.sqrt((coordX[s1]-coordX[s2])*(coordX[s1]-coordX[s2])+(coordY[s1]-coordY[s2])*(coordY[s1]-coordY[s2]));
                }
            }
            final FuelState initialState = new FuelState(new State(new int[]{0,1,2,3,4,5,6,7}, train1Position, train2Position), f, f);
            
            // Breadth First Search
            final long time = System.currentTimeMillis();
            int maxScore = initialState.state.score;
            final Map<State, Set<FuelState>> visitedStates = new HashMap<>();
            final Queue<FuelState> q = new LinkedList<>();
            visitedStates.put(initialState.state, new HashSet<>(Collections.singleton(initialState)));
            q.add(initialState);
            while (!q.isEmpty()) {
                final FuelState state = q.poll();
                search: for (FuelState neighbor : state.neighbors()) {
                    if (neighbor.state.score > maxScore) maxScore = neighbor.state.score;
                    Set<FuelState> states = visitedStates.get(neighbor.state);
                    if (states == null) {
                        states = new HashSet<>();
                        visitedStates.put(neighbor.state, states);
                    }
                    for (FuelState fs : states) {
                        if (fs.train1Fuel >= neighbor.train1Fuel && fs.train2Fuel >= neighbor.train2Fuel) {
                            continue search;
                        }
                    }
                    states.add(neighbor);
                    q.add(neighbor);
                }
                // If we aren't done in one hour give up and return the best score so far
                if ((System.currentTimeMillis()-time) > 60*60*1000) break;
            }
            System.out.println(maxScore);
        }
    }
    
    private static class State {
        public final int[] wagonPositions;
        public final int train1Position;
        public final int train2Position;
        public final int score;
        
        public State(int[] wagonPositions, int train1Position, int train2Position) {
            this.wagonPositions = wagonPositions;
            this.train1Position = train1Position;
            this.train2Position = train2Position;
            int score = 0;
            for (int i = 0; i < 8; i++) if (wagonPositions[i] == wagonDestinations[i]) score += wagonValues[i];
            this.score = score;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Arrays.hashCode(this.wagonPositions);
            hash = 41 * hash + this.train1Position;
            hash = 41 * hash + this.train2Position;
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
            final State other = (State) obj;
            if (!Arrays.equals(this.wagonPositions, other.wagonPositions)) {
                return false;
            }
            if (this.train1Position != other.train1Position) {
                return false;
            }
            if (this.train2Position != other.train2Position) {
                return false;
            }
            return true;
        }
        
    }
    
    private static class FuelState {
        public final State state;
        public final double train1Fuel;
        public final double train2Fuel;
        private List<FuelState> neighbors;
        
        public FuelState(State state, double train1Fuel, double train2Fuel) {
            this.state = state;
            this.train1Fuel = train1Fuel;
            this.train2Fuel = train2Fuel;
        }
        
        public Iterable<FuelState> neighbors() {
            return new Iterable<FuelState>() {
                @Override public Iterator<FuelState> iterator() {
                    if (neighbors == null) initNeighbors();
                    return neighbors.iterator();
                }
            };
        }
        
        private void initNeighbors() {
            neighbors = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                if (train1Distances[state.train1Position][i] <= train1Fuel && i != state.train1Position) {
                    for (int j = 0; j < 8; j++) {
                        if (state.wagonPositions[j] == state.train1Position && i != j) {
                            // Train 1 takes wagon j to i
                            final int[] wagonPositions = Arrays.copyOf(state.wagonPositions, 8);
                            wagonPositions[j] = i;
                            neighbors.add(new FuelState(
                                    new State(wagonPositions, i, state.train2Position),
                                    train1Fuel-train1Distances[state.train1Position][i], train2Fuel));
                        }
                    }
                    neighbors.add(new FuelState( // Train 1 goes to i without taking any wagon
                                    new State(state.wagonPositions, i, state.train2Position),
                                    train1Fuel-train1Distances[state.train1Position][i], train2Fuel));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (train2Distances[state.train2Position][i] <= train2Fuel && i != state.train2Position) {
                    for (int j = 0; j < 8; j++) {
                        if (state.wagonPositions[j] == state.train2Position && i != j) {
                            // Train 2 takes wagon j to i
                            final int[] wagonPositions = Arrays.copyOf(state.wagonPositions, 8);
                            wagonPositions[j] = i;
                            neighbors.add(new FuelState(
                                    new State(wagonPositions, state.train1Position, i),
                                    train1Fuel, train2Fuel-train2Distances[state.train2Position][i]));
                        }
                    }
                    neighbors.add(new FuelState( // Train 2 goes to i without taking any wagon
                                    new State(state.wagonPositions, state.train1Position, i),
                                    train1Fuel, train2Fuel-train2Distances[state.train2Position][i]));
                }
            }
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.state);
            hash = 79 * hash + (int) (Double.doubleToLongBits(this.train1Fuel) ^ (Double.doubleToLongBits(this.train1Fuel) >>> 32));
            hash = 79 * hash + (int) (Double.doubleToLongBits(this.train2Fuel) ^ (Double.doubleToLongBits(this.train2Fuel) >>> 32));
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
            final FuelState other = (FuelState) obj;
            if (!Objects.equals(this.state, other.state)) {
                return false;
            }
            if (Double.doubleToLongBits(this.train1Fuel) != Double.doubleToLongBits(other.train1Fuel)) {
                return false;
            }
            if (Double.doubleToLongBits(this.train2Fuel) != Double.doubleToLongBits(other.train2Fuel)) {
                return false;
            }
            return true;
        }
        
    }
    
}
