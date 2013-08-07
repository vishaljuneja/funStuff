import java.util.Comparator;

public class Solver {
    
    private Comparator<SearchNode> MANHATTTAN_BASED = new Comparator<SearchNode>(){
        public int compare(SearchNode node1, SearchNode node2){
             int p1 = node1.manhattan() + node1.moves;
             int p2 = node2.manhattan() + node2.moves;
             
             if(p1>p2) return 1;
             else if(p1<p2) return -1;
             else return 0;
        }        
    };
    
    private boolean solvable;
    private int moves;
    private Stack<Board> solution;
    
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial){
        if(initial==null) throw new NullPointerException();
        Board twin = initial.twin();
        
        SearchNode start = new SearchNode(initial, null, 0);
        SearchNode startTwin = new SearchNode(twin, null, 0);
        MinPQ pq = new MinPQ(MANHATTTAN_BASED);
        pq.insert(start);
        pq.insert(startTwin);
        
        // find goal
        SearchNode next = (SearchNode) pq.delMin();
        while(!next.isGoal()){
            for(Board b : next.neighbors()){
                if(!b.equals(next.previous())) {
                    SearchNode node = 
                        new SearchNode(b, next, next.moves + 1);
                    pq.insert(node);                    
                }
            }
            next = (SearchNode) pq.delMin();
        }
        
        // retrace path
        SearchNode path = next;
        solution = new Stack<Board>();
        while(path.previous!=null) {
            solution.push(path.board);
            path = path.previous;
        }
        solution.push(path.board);
        
        // check for solvability
        if(path.board.equals(initial)){
            solvable = true;
            moves = next.moves;
        } else {
            solvable = false;
            moves = -1;
            solution = null;
        }
    }
    
    // is the initial board solvable?
    public boolean isSolvable(){
        return solvable;
    }
    
    // min number of moves to solve initial board; -1 if no solution
    public int moves(){
        return moves;
    }
    
    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution(){
        return solution;
    }
    
    // solve a slider puzzle (given below)
    public static void main(String[] args){
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
    
    
    private class SearchNode {
        public Board board;
        public SearchNode previous;
        public int moves;
        
        public SearchNode(Board board, SearchNode previous, int moves){
            this.board = board;
            this.previous = previous;
            this.moves = moves;
        }
        
        public int manhattan(){
            return board.manhattan();
        }
        
        public boolean isGoal(){
            return board.isGoal();
        }
        
        public Iterable<Board> neighbors(){
            return board.neighbors();
        }
        
        public Board previous(){
            if(previous == null) return null;
            return this.previous.board;
        }
    }
}