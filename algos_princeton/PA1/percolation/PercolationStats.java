public class PercolationStats {
    
    private final Integer numSimulations;
    
    private double mean;
    private double stddev;
    private double confidenceLo;
    private double confidenceHi;
    
    // perform T independent computational experiments on an N-by-N grid
    public PercolationStats(int N, int T){
        if(N<=0 || T<=0) 
           throw new IllegalArgumentException("Illegal constructor arguments");
        
        this.numSimulations = T;
        double thresholds[] = new double[T];
        
        for(int i=0; i<T; i++){
            double opencount = 0;
            Percolation p = new Percolation(N);
            while(!p.percolates()){
                int r = StdRandom.uniform(N)+1;
                int c = StdRandom.uniform(N)+1;
                if(!p.isOpen(r,c)){
                    p.open(r,c);
                    opencount = opencount + 1;
                }
            }
            thresholds[i] = opencount/(N*N);
        }
        
        // calculate mean
        double sum = 0;
        for(int i=0; i<numSimulations; i++){
            sum = sum + thresholds[i];
        }
        mean = sum/numSimulations;
        
        // calculate stddev, confidenceLo, confidenceHi
        if(numSimulations == 1){
            stddev = Double.NaN;
            confidenceLo = Double.NaN;
            confidenceHi = Double.NaN;
        } else {
            sum = 0;
            for(int i=0; i<numSimulations; i++){
                sum = sum + (thresholds[i]-mean)*(thresholds[i]-mean);
            }
            stddev = Math.sqrt(sum/(numSimulations-1));
            confidenceLo = mean - 1.96*stddev/Math.sqrt(numSimulations);
            confidenceHi = mean + 1.96*stddev/Math.sqrt(numSimulations);
        }
    }
   
    // sample mean of percolation threshold
    public double mean(){
        return mean;
    }
    
    // sample standard deviation of percolation threshold
    public double stddev(){
        return stddev;
    }
    
    // returns lower bound of the 95% confidence interval
    public double confidenceLo(){
        return confidenceLo;
    }
    
    // returns upper bound of the 95% confidence interval  
    public double confidenceHi(){
        return confidenceHi;
    }
    
   // test client, described below
    public static void main(String[] args){
       PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]),
                                 Integer.parseInt(args[1]));
       StdOut.println("mean                    = "+ps.mean());
       StdOut.println("stddev                  = "+ps.stddev());
       StdOut.println("95% confidence interval = "+ps.confidenceLo()+", "+
                      ps.confidenceHi());
    }
}