public class Percolation {
    private final Integer size;
    private boolean grid[][];
    
    private final Integer virtualTop;
    private final Integer virtualBottom;
    
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF bwuf;
    
    // create N-by-N grid, with all sites blocked
    public Percolation(int N){
        this.size = N;
        this.grid = new boolean[N][N];
        initializeGrid();
               
        this.virtualTop = N*N;
        this.virtualBottom = N*N + 1;
        uf = new WeightedQuickUnionUF(N*N + 2);
        
        bwuf = new WeightedQuickUnionUF(N*N + 1);
        // connect virtualTop and virtualBottom
        for(int i=1; i<=N; i++){
            uf.union(xyTo1D(1,i), this.virtualTop);
            uf.union(xyTo1D(N,i), this.virtualBottom);
            
            bwuf.union(xyTo1D(1,i), this.virtualTop);
        }
    }
   
   // open site (row i, column j) if it is not already
    public void open(int i, int j){
        validate(i,j);
                
        grid[i-1][j-1] = true;
        // uf operation
        if(isConnectedNeighbour(i,j, "left")) {
            uf.union(xyTo1D(i,j), xyTo1D(i, j-1)); 
            bwuf.union(xyTo1D(i,j), xyTo1D(i, j-1)); 
        }
        if(isConnectedNeighbour(i,j, "right")) {
            uf.union(xyTo1D(i,j), xyTo1D(i, j+1)); 
            bwuf.union(xyTo1D(i,j), xyTo1D(i, j+1));
        }
        if(isConnectedNeighbour(i,j, "top")) {
            uf.union(xyTo1D(i,j), xyTo1D(i-1, j));
            bwuf.union(xyTo1D(i,j), xyTo1D(i-1, j));
        }
        if(isConnectedNeighbour(i,j, "bottom")) { 
            uf.union(xyTo1D(i,j), xyTo1D(i+1, j)); 
            bwuf.union(xyTo1D(i,j), xyTo1D(i+1, j));
        }
    }
    
    // is site (row i, column j) open?
    public boolean isOpen(int i, int j){
        validate(i,j);
        return grid[i-1][j-1];
    }
    
    // is site (row i, column j) full?
    public boolean isFull(int i, int j){
        validate(i,j);
        return (grid[i-1][j-1] == true 
                    && bwuf.connected(xyTo1D(i,j), virtualTop));
    }
    
    // does the system percolate?
    public boolean percolates() {
        if(size == 1 && !isOpen(1,1) ) return false;
        return uf.connected(virtualTop, virtualBottom);
    }
    
    private Boolean isConnectedNeighbour(int i, int j, String str){
        if(str.equals("left")){
            if(j > 1 && grid[i-1][j-2] == true) 
                return true;
            else 
                return false;
        } else if(str.equals("right")){
            if(j < size && grid[i-1][j] == true) 
                return true;
            else 
                return false;
        } else if(str.equals("top")){
            if(i > 1 && grid[i-2][j-1] == true) 
                return true;
            else 
                return false;
        } else if(str.equals("bottom")){
            if(i < size && grid[i][j-1] == true) 
                return true;
            else 
                return false;
        } else {
            return false;
        }
        
    }
    
    private void initializeGrid(){
        for(int i=0; i<size ; i++){
            for(int j=0; j<size ; j++){
                grid[i][j] = false;
            }
        }
    }

    private void validate(int i, int j){
        if (i <= 0 || i > size) 
           throw new IndexOutOfBoundsException("row index i out of bounds");
        if (j <= 0 || j > size) 
           throw new IndexOutOfBoundsException("column index i out of bounds");
    }

    private Integer xyTo1D(int i, int j){
        return (i-1)*size + j - 1;
    }
    
    public static void main(String[] args){
        Percolation p = new Percolation(3);
        //p.open(1,1);
        //StdOut.println(p.isFull(1,1));
        //StdOut.println(p.isOpen(1,1));
        //StdOut.println(p.percolates());
        
        p.open(2,1);
        p.open(3,1);
        p.open(1,3);
        p.open(2,3);
        p.open(3,3);
        StdOut.println(p.bwuf.find(p.xyTo1D(2,1)));
        StdOut.println(p.bwuf.find(p.xyTo1D(2,3)));
        StdOut.println(p.isFull(2,1));
        StdOut.println(p.percolates());
        
    }
}