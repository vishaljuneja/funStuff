/*************************************************************************
 *  Compilation:  javac PuzzleChecker.java
 *  Execution:    java PuzzleChecker filename1.txt filename2.txt ...
 *  Dependencies: Board.java Solver.java In.java
 *
 *  This program creates an initial board from each filename specified
 *  on the command line and finds the minimum number of moves to
 *  reach the goal state.
 *
 *  % java PuzzleChecker puzzle*.txt
 *  puzzle00.txt: 0
 *  puzzle01.txt: 1
 *  puzzle02.txt: 2
 *  puzzle03.txt: 3
 *  puzzle04.txt: 4
 *  puzzle05.txt: 5
 *  puzzle06.txt: 6
 *  ...
 *  puzzle3x3-impossible: -1
 *  ...
 *  puzzle42.txt: 42
 *  puzzle43.txt: 43
 *  puzzle44.txt: 44
 *  puzzle45.txt: 45
 *
 *************************************************************************/

public class PuzzleChecker {

    public static void main(String[] args) {

        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int N = in.readInt();
            int[][] tiles = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);
            Board initial2 = new Board(tiles);
            StdOut.println(initial.toString());
            StdOut.println("equality testing " + initial.equals(initial2));
            StdOut.println(initial.isGoal());
            
            Board twin = initial.twin();
            StdOut.println("twin "+twin);
            StdOut.println(initial.hamming()+" "+initial.manhattan()
                               +" "+twin.hamming()+" "+twin.manhattan());
            
            StdOut.println("equality testing " + initial.equals(twin));
            
            twin = twin.twin();
            StdOut.println(twin);
            StdOut.println(initial.hamming()+" "+initial.manhattan()
                               +" "+twin.hamming()+" "+twin.manhattan());
            
           StdOut.println("printing initials neighbors " + initial);
            
           for(Board b : initial.neighbors()){
                StdOut.println(b);
                StdOut.println("manhattan and hamming "+b.manhattan()+" "+b.hamming());
            }
           
           StdOut.println("equality testing " + initial.equals(twin));
            //Solver solver = new Solver(initial);
            //System.out.println(filename + ": " + solver.moves());
        }
    }
}