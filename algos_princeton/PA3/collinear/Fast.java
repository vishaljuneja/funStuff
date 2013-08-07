import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class Fast {
    public static void main(String[] args){
        
        // rescale coordinates and turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);

        // read in the input
        String filename = args[0];
        In in = new In(filename);
        int N = in.readInt();
        
        if(N<4) return;      
        Node arr[] = new Node[N];
        
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            //Point p = new Point(x, y);
            Node n = new Node(x,y);
            arr[i] = n;
            n.draw();
        }
        
        collinear(arr);

        // display to screen all at once
        StdDraw.show(0);
    }
    
    private static class Node extends Point {
        public Node(int x, int y){
            super(x,y);
            slopes = new ArrayList<Double>();
        }
        
        public ArrayList<Double> slopes;
        public double slope;
    }
    
    private static void collinear(Node[] arr){
        Arrays.sort(arr);
        
        int index = 0;
        for(Node n : arr){
            // keep only sub array discarding previously considered data
            Node sub[] = Arrays.copyOfRange(arr, index, arr.length);
            Arrays.sort(sub, n.SLOPE_ORDER);
             
            //remove previously occured slopes(to avoid redundancy)
            Collections.sort(n.slopes);
            int i = 0;
            for(double s : n.slopes){
                double slope = n.slopeTo(sub[i]);
                while( slope < s){
                    i++;
                    if(i >= sub.length) 
                        break;
                    else 
                        slope = n.slopeTo(sub[i]);
                }
                if(i >= sub.length) break;
                
                while(slope == s){
                    sub[i] = null;
                    i++;
                    if(i >= sub.length) 
                        break;
                    else 
                        slope = n.slopeTo(sub[i]);
                }
                if(i >= sub.length) break;
            }
            
            //find lines
            int count = 0;
            int index1 = 0;
            for(int j=0; j<sub.length; j++){
                if(sub[j] == null){
                     if(count>=3){
                         plot(sub, index1, j-1, n);
                     }
                     index1 = j;
                     count = 1;
                } else {
                    if(sub[index1] == null){
                        index1 = j;
                        count = 1;
                        continue;
                    }
                    double slope = n.slopeTo(sub[j]);
                    if(slope == n.slopeTo(sub[index1])) {
                        count++;
                    } else {
                        if(count>=3){
                            plot(sub, index1, j-1, n);
                        }
                        index1 = j;
                        count = 1;
                    }
                }
            }
            
           if(count>=3)  plot(sub, index1, sub.length-1, n);
           index++;
        }    
    }
    
    private static void plot(Node[] arr, int i1, int i2, Node n){
        int length = i2 - i1 + 2;
        
        Node sub[] = new Node[length];
        sub[0] = n;
        for(int i = i1, j = 1; i<=i2; i++, j++){
            sub[j] = arr[i];
        }
        Arrays.sort(sub);
        sub[0].drawTo(sub[sub.length-1]);
        
        StringBuilder sb = new StringBuilder();
        sb.append(n+" -> ");
        for(int i = i1; i<i2; i++){
            sb.append(arr[i]+" -> ");
            arr[i].slopes.add(n.slopeTo(arr[i]));
        }
        arr[i2].slopes.add(n.slopeTo(arr[i2]));
        sb.append(arr[i2]);
        StdOut.println(sb);
    }
}