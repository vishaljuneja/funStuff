import java.util.Arrays;
import java.util.ArrayList;

public class Board {
    private Byte blocks[][];
    
    private Short hamming = null;
    private Short manhattan = null;
    
    private int z_row;
    private int z_col;
    
    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] tiles){
        if(tiles == null) throw new NullPointerException();
        int N = tiles.length;
        this.blocks = new Byte[N][N];
        
        hamming = 0;
        int man = 0;
        int count = 1;
        
        //make deep copies
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                this.blocks[i][j] = (byte) tiles[i][j];
                if(tiles[i][j] == 0){
                    z_row = i;
                    z_col = j;
                } else {
                    if(tiles[i][j] != count){
                        hamming++;
                        man += Math.abs(i - (tiles[i][j]-1)/N);
                        man += Math.abs(j - (tiles[i][j]-1)%N);
                    }
                }
                count++;
            }           
        }
        this.manhattan = (short) man;
    }
    
    private Board(Byte[][] tiles, Short hamming, Short manhattan,
                     int row, int col, int z_row, int z_col){
        
        int N = tiles.length;
        this.z_row = z_row;
        this.z_col = z_col;
        Byte val = tiles[row][col];
        int actual_row = (val-1)/N;
        int actual_col = (val-1)%N;
        // compute new hamming and manhattan
        this.hamming = hamming;
        //this.manhattan = manhattan;
        if(z_row==actual_row && z_col==actual_col){
            this.hamming = (short) (hamming + 1);
            //this.manhattan = (short) (manhattan + 1);
        }
        
        if(row==actual_row && col==actual_col){
            this.hamming = (short) (hamming - 1);
            //this.manhattan = (short) (manhattan - 1);
        }
        
        int temp = manhattan - Math.abs(z_row - actual_row)
                             - Math.abs(z_col - actual_col)
                             + Math.abs(row - actual_row)
                             + Math.abs(col - actual_col);
        
        this.manhattan = (short) temp;
        
        
        this.blocks = new Byte[N][N];
        //make deep copies
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                this.blocks[i][j] = tiles[i][j];
            }           
        }
    }
    
    // board dimension N
    public int dimension(){
        return blocks.length;
    }
    
    // number of blocks out of place
    public int hamming(){
        return hamming;        
    }
    
    // sum of Manhattan distances between blocks and goal
    public int manhattan(){
        return manhattan;
    }
    
    
    // is this board the goal board?
    public boolean isGoal() {
        int h = hamming();
        //int m = manhattan();
        if(h==0){
            //assert h==m;
            return true;
        }
        return false;
    }
    
    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin(){
        int N = blocks.length;
        int newBlocks[][] = new int[N][N];
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                newBlocks[i][j] = blocks[i][j];
            }
        }
        
        int i = StdRandom.uniform(blocks.length);
        int j = StdRandom.uniform(blocks.length);
        
        if(hasRight(i,j)){
            if(blocks[i][j] == 0 || blocks[i][j+1] == 0){
                if(hasTop(i,j)) 
                    i = i-1; 
                else  
                    i = i+1;
            }
            exchange(newBlocks, i, j, i, j+1);
        } else {
            if(blocks[i][j] == 0 || blocks[i][j-1] == 0){
                if(hasTop(i,j)) 
                    i = i-1; 
                else  
                    i = i+1;
            }
            exchange(newBlocks, i, j, i, j-1);
        }
        
        return new Board(newBlocks);
    }
    
    // does this board equal y?
    public boolean equals(Object y){
        if(y==null) return false;
        if(y==this) return true;
        if(y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if(that.blocks.length != this.blocks.length) return false;
        if(this.z_row!=that.z_row || this.z_col!=that.z_col) return false;
        if(!(this.manhattan() == that.manhattan())) return false;
        
        for(int i=0; i<this.blocks.length; i++){
            if(!Arrays.equals(this.blocks[i], that.blocks[i]))
               return false;
        }
        return true;
    }
    
    // all neighboring boards
    public Iterable<Board> neighbors(){
        // find index of 0
        int row = z_row;
        int col = z_col;
        
        ArrayList<Board> result = new ArrayList<Board>();
 
        if(hasRight(row,col)) {
            exchange(blocks, row, col, row, col+1);
            result.add(new Board(blocks, 
                                 this.hamming, 
                                 this.manhattan, 
                                 row, col,
                                 row, col+1));
            exchange(blocks, row, col, row, col+1);
        }
        
        if(hasLeft(row,col)) {
            exchange(blocks, row, col, row, col-1);
            result.add(new Board(blocks, 
                                 this.hamming, 
                                 this.manhattan, 
                                 row, col,
                                 row, col-1));
            exchange(blocks, row, col, row, col-1);
        }
        
        if(hasTop(row,col)) {
            exchange(blocks, row, col, row-1, col);
            result.add(new Board(blocks, 
                                 this.hamming, 
                                 this.manhattan, 
                                 row, col,
                                 row-1, col));
            exchange(blocks, row, col, row-1, col);
        }
        
        if(hasBottom(row,col)) {
            exchange(blocks, row, col, row+1, col);
            result.add(new Board(blocks, 
                                 this.hamming, 
                                 this.manhattan, 
                                 row, col,
                                 row+1, col));
            exchange(blocks, row, col, row+1, col);
        }
        
        return result;        
    }
    
    
    // string representation of the board (in the output format specified below)
    public String toString(){
         if(blocks == null) return "0";
         int N = blocks.length;
         StringBuilder s = new StringBuilder();
         s.append(N + "\n");
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 s.append(String.format("%2d ", blocks[i][j]));
             }
             s.append("\n");
         }
         return s.toString();
    }
    
    private boolean hasRight(int i, int j){
        if(j != (blocks.length - 1)) return true;
        return false;
    }
    
    private boolean hasTop(int i, int j){
        if(i != 0) return true;
        return false;
    }
    
    private boolean hasLeft(int i, int j){
        if(j != 0) return true;
        return false;
    }
    
    private boolean hasBottom(int i, int j){
        if(i != (blocks.length - 1)) return true;
        return false;
    }
    
    //exchange (i1,j1) with (i2,j2)
    private void exchange(Byte[][] arr, int i1, int j1, int i2, int j2){
         byte temp = arr[i1][j1];
         arr[i1][j1] = arr[i2][j2];
         arr[i2][j2] = temp;
    }
    
    //exchange (i1,j1) with (i2,j2)
    private void exchange(int[][] arr, int i1, int j1, int i2, int j2){
         int temp = arr[i1][j1];
         arr[i1][j1] = arr[i2][j2];
         arr[i2][j2] = temp;
    }

    private boolean inBounds(int i){
        if(i<blocks.length && i>=0) return true;
        return false;
    }
}