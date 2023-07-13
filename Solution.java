import java.util.*;

public class Solution {
    private static final int OBSTACLE = -1;
    private static final int POWER_UP = 7;
    private static final int NEGATIVE = -3;

    private final int[][] maze;
    private final int[][] scores;
    private final int[][] distances;
    private final int size;

    public Solution(int[][] maze) {
        this.maze = maze;
        this.size = maze.length;
        this.scores = new int[size][size];
        this.distances = new int[size][size];
    }

    public void UCS_Sol() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        boolean[][] visited = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }

        distances[0][0] = 0;
        scores[0][0] = maze[0][0];
        priorityQueue.add(new Node(0, 0, 0));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            int x = currentNode.x();
            int y = currentNode.y();

            if (x == size - 1 && y == size - 1) {
                break; // Reached the goal node
            }

            visited[x][y] = true;

            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (isValid(newX, newY) && !visited[newX][newY] && maze[newX][newY] != OBSTACLE) {
                    int newDistance = distances[x][y] + 1;
                    int newScore = scores[x][y] + maze[newX][newY];

                    if (newDistance < distances[newX][newY] || newScore > scores[newX][newY]) {
                        distances[newX][newY] = newDistance;
                        scores[newX][newY] = newScore;
                        priorityQueue.add(new Node(newX, newY, newDistance));
                    }
                }
            }
        }
    }

    public void AStar_Sol() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        boolean[][] visited = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }

        distances[0][0] = 0;
        scores[0][0] = maze[0][0];
        priorityQueue.add(new Node(0, 0, 0));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            int x = currentNode.x();
            int y = currentNode.y();

            if (x == size - 1 && y == size - 1) {
                break; // Reached the goal node
            }

            visited[x][y] = true;

            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (isValid(newX, newY) && !visited[newX][newY] && maze[newX][newY] != OBSTACLE) {
                    int newDistance = distances[x][y] + 1;
                    int newScore = scores[x][y] + maze[newX][newY];
                    int heuristic = calculateHeuristic(newX, newY);

                    if (newDistance < distances[newX][newY] || newScore > scores[newX][newY]) {
                        distances[newX][newY] = newDistance;
                        scores[newX][newY] = newScore;
                        priorityQueue.add(new Node(newX, newY, newDistance + heuristic));
                    }
                }
            }
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private int calculateHeuristic(int x, int y) {
        return Math.abs(size - 1 - x) + Math.abs(size - 1 - y);
    }

    public int getSteps() {
        return distances[size - 1][size - 1];
    }

    public int getScore() {
        return scores[size - 1][size - 1];
    }


    public void printFinalPath() {
        int x = size - 1;
        int y = size - 1;
        System.out.println("Final Path:");
        System.out.println("(" + x + ", " + y + ")");

        while (x != 0 || y != 0) {
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (isValid(newX, newY) && distances[newX][newY] == distances[x][y] - 1) {
                    x = newX;
                    y = newY;
                    System.out.println("(" + x + ", " + y + ")");
                    break;
                }
            }
        }
    }

    public record Node(int x, int y, int cost) implements Comparable<Node> {

        @Override
            public int compareTo(@org.jetbrains.annotations.NotNull Node other) {
                return Integer.compare(cost, other.cost());
            }
        }

    public static void main(String[] args) {
        int size = generateSize();
        int[][] maze = generateMaze(size);

        maze[0][0] = 0; // Start cell
        maze[size - 1][size - 1] = 0; // Goal cell

        Solution ucsSolver = new Solution(maze);
        ucsSolver.UCS_Sol();
        System.out.println("UCS:");
        System.out.println("Total Steps: " + ucsSolver.getSteps());
        System.out.println("Final Score: " + ucsSolver.getScore());
        ucsSolver.printMaze();

        System.out.println();

        Solution aStarSolver = new Solution(maze);
        aStarSolver.AStar_Sol();
        System.out.println("A*:");
        System.out.println("Total Steps: " + aStarSolver.getSteps());
        System.out.println("Final Score: " + aStarSolver.getScore());
        aStarSolver.printMaze();

    }

    private static int generateSize() {
        Random random = new Random();
        return random.nextInt(10 - 5 + 1) + 5;
    }

    private static int[][] generateMaze(int size) {
        int[][] maze = new int[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cellType = random.nextInt(4);
                if (cellType == 0) {
                    maze[i][j] = OBSTACLE;
                } else if (cellType == 1) {
                    maze[i][j] = POWER_UP;
                } else if (cellType == 2) {
                    maze[i][j] = NEGATIVE;
                }
            }
        }

        return maze;
    }

    public void printMaze() {
        System.out.println("Maze:");
        System.out.println();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (maze[i][j] == OBSTACLE) {
                    System.out.print(" X ");
                } else if (maze[i][j] == POWER_UP) {
                    System.out.print(" + ");
                } else if (maze[i][j] == NEGATIVE) {
                    System.out.print(" - ");
                } else if (i == 0 && j == 0) {
                    System.out.print(" S ");
                } else if (i == size - 1 && j == size - 1) {
                    System.out.print(" G ");
                } else {
                    System.out.print("   ");
                }

            }

            System.out.println();
        }
    }
    }
