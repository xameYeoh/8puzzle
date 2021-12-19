import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.Comparator;

/*
    IMPORTANT PERFORMANCE FIXES THROUGHOUT THE DEVELOPMENT:
    1. Changing the data structure of covered nodes from LinkedList to Stack
    2. Changing isGoal() method implementation from comparing
       this board to finished one -> checking this board manually (- time for constructing another board)
    3. Changing contains() method implementation from checking every entry in Stack
       -> checking just previous board on stack (because duplicates can only by obtained through
       neighbour-of-my-neighbour relation)
 */

public class Solver {
    private boolean solvable = true;
    private final Stack<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        Board twin = initial.twin();
        NodeComparator comparator = new NodeComparator();

        Stack<SearchNode> covered = new Stack<>();
        Stack<SearchNode> coveredTwin = new Stack<>();

        MinPQ<SearchNode> pq = new MinPQ<>(comparator);
        MinPQ<SearchNode> pqTwin = new MinPQ<>(comparator);

        SearchNode start = new SearchNode(null, initial, 0);
        SearchNode startTwin = new SearchNode(null, twin, 0);

        pq.insert(start);
        pqTwin.insert(startTwin);

        while (!pq.isEmpty() || !pqTwin.isEmpty()) {
            SearchNode current = pq.delMin();
            SearchNode currentTwin = pqTwin.delMin();

            covered.push(current);
            coveredTwin.push(currentTwin);

            if (current.board.isGoal()) {
                break;
            }

            if (currentTwin.board.isGoal()) {
                solvable = false;
                break;
            }

            for (Board neighbor : current.board.neighbors()) {
                SearchNode node = new SearchNode(current, neighbor, current.moves + 1);
                if (!contains(covered, node))
                    pq.insert(node);
            }

            for (Board neighbor : currentTwin.board.neighbors()) {
                SearchNode node = new SearchNode(currentTwin, neighbor, currentTwin.moves + 1);
                if (!contains(coveredTwin, node))
                    pqTwin.insert(node);
            }
        }

        solution = new Stack<>();

        SearchNode lastStep = covered.peek();
        while (lastStep != null) {
            solution.push(lastStep.board);
            lastStep = lastStep.previous;
        }
    }

    private static boolean contains(Stack<SearchNode> stack, SearchNode searched) {
        if (stack.size() < 2) return false;
        return searched.board.equals(stack.peek().previous.board);
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!solvable) return -1;

        return solution.size() - 1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!solvable) return null;

        return solution;
    }

    private class SearchNode {
        SearchNode previous;
        Board board;
        int moves;
        int priority;

        SearchNode(SearchNode previous, Board board, int moves) {
            this.previous = previous;
            this.board = board;
            this.moves = moves;
            this.priority = this.moves + this.board.manhattan();
        }
    }

    private class NodeComparator implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            return a.priority - b.priority;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        Solver solver = new Solver(
                new Board(
                        new int[][] {
                                { 8, 6, 7 },
                                { 2, 0, 4 },
                                { 3, 5, 1 }
                        }
                )
        );

        System.out.println("==================\n");
        Iterable<Board> solution = solver.solution();
        for (Board board : solution) {
            System.out.println(board.toString());
        }
    }
}