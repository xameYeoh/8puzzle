import java.util.LinkedList;
import java.util.List;

public class Board {

    private final int[][] tiles;
    private final int[] zero;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][];
        zero = new int[2];
        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i] = new int[tiles[i].length];
            for (int j = 0; j < tiles[i].length; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (this.tiles[i][j] == 0) {
                    zero[0] = i;
                    zero[1] = j;
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder builder = new StringBuilder(String.valueOf(tiles.length));
        for (int i = 0; i < tiles.length; i++) {
            builder.append("\n ");
            for (int j = 0; j < tiles[i].length; j++) {
                builder.append(String.valueOf(tiles[i][j]));
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        int count = 0;

        for (int i = 0; i < dimension(); i++) {
            int row = i * dimension();
            for (int j = 0; j < dimension(); j++) {
                int element = tiles[i][j];

                if (element == 0)
                    continue;

                int correctPosition = element - 1;
                int actualPosition = row + j;
                if (actualPosition != correctPosition) count++;
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int sum = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                int element = tiles[i][j];

                if (element == 0) {
                    continue;
                }

                int rowNeeded = (element - 1) / dimension();
                int colNeeded = (element - 1) % dimension();
                int xDistance = Math.abs(j - colNeeded);
                int yDistance = Math.abs(i - rowNeeded);

                sum += xDistance + yDistance;
            }
        }
        return sum;
    }

    // // is this board the goal board?
    // public boolean isGoal()
    //
    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null || this.getClass() != y.getClass()) return false;

        Board that = (Board) y;
        if (this.dimension() != that.dimension()) return false;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) return false;
            }
        }
        return true;
    }

    public boolean isGoal() {
        if (tiles[dimension() - 1][dimension() - 1] != 0) return false;

        for (int i = 0; i < dimension() - 1; i++) {
            if (tiles[i][dimension() - 1] != i * dimension() + dimension())
                return false;
        }

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension() - 1; j++) {
                if (tiles[i][j] != i * dimension() + j + 1)
                    return false;
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {

        List<Board> neighbors = new LinkedList<>();

        int top = zero[0] - 1;
        int down = zero[0] + 1;
        int left = zero[1] - 1;
        int right = zero[1] + 1;

        if (top >= 0) {
            neighbors.add(getNeighbor(top, zero[1]));
        }

        if (down < dimension()) {
            neighbors.add(getNeighbor(down, zero[1]));
        }

        if (left >= 0) {
            neighbors.add(getNeighbor(zero[0], left));
        }

        if (right < dimension()) {
            neighbors.add(getNeighbor(zero[0], right));
        }

        return neighbors;
    }

    private Board getNeighbor(int row, int col) {
        int replaced = tiles[row][col];
        tiles[zero[0]][zero[1]] = replaced;
        tiles[row][col] = 0;
        Board neighbor = new Board(tiles);
        tiles[zero[0]][zero[1]] = 0;
        tiles[row][col] = replaced;

        return neighbor;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        for (int i = dimension() - 1; i >= dimension() - 2; i--) {
            for (int j = dimension() - 1; j >= dimension() - 2; j--) {
                int replacedBottom = tiles[i][j];
                if (replacedBottom != 0) {
                    int replacedTop = tiles[i - 1][j];
                    if (replacedTop != 0) {
                        tiles[i][j] = replacedTop;
                        tiles[i - 1][j] = replacedBottom;
                        Board twin = new Board(tiles);
                        tiles[i][j] = replacedBottom;
                        tiles[i - 1][j] = replacedTop;

                        return twin;
                    }
                }
            }
        }
        return null;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        Board board = new Board(
                new int[][] {
                        { 8, 1, 3 },
                        { 4, 0, 2 },
                        { 7, 6, 5 }
                }
        );

        Board copy = new Board(
                new int[][] {
                        { 8, 1, 3 },
                        { 4, 0, 2 },
                        { 7, 6, 5 }
                }
        );

        Board neighbors = new Board(
                new int[][] {
                        { 1, 6, 3 },
                        { 4, 2, 5 },
                        { 7, 8, 0 }
                }
        );

        System.out.println(board.toString());
        System.out.println(board.hamming());
        System.out.println(board.manhattan());
        System.out.println(board.equals(copy));

        Iterable<Board> ns = neighbors.neighbors();
        for (Board variant : ns) {
            System.out.println(variant.toString());
        }

        System.out.println("\n\n===============\n\n");

        Board twin = neighbors.twin();
        System.out.println(twin.toString());
        twin = neighbors.twin();
        System.out.println(twin.toString());
        twin = neighbors.twin();
        System.out.println(twin.toString());
    }
}