import java.util.Arrays;

public class Brute {
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
        Point arr[] = new Point[N];
        
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            Point p = new Point(x, y);
            arr[i] = p;
            p.draw();
        }
        
        collinear(arr, 4, 0, 1, 2, 3);

        // display to screen all at once
        StdDraw.show(0);
    }
    
    private static void collinear(Point arr[], int p, int i1, int i2, int i3, int i4){
        if(i4 == arr.length || i3 == i4 || i2 == i3 || i1 == i2) return;
        
        //StdOut.println((i1+1)+" "+(i2+1)+" "+(i3+1)+" "+(i4+1));
        Point p1 = arr[i1];
        Point p2 = arr[i2];
        Point p3 = arr[i3];
        Point p4 = arr[i4];
        
        
        double slope2 = p1.slopeTo(p2);
        double slope3 = p1.slopeTo(p3);
        if(slope2 == slope3){
            if(slope2 == p1.slopeTo(p4)) {
                Point sub[] =  new Point[] {p1, p2,p3,p4};
                Arrays.sort(sub);
                sub[0].drawTo(sub[3]);
                StdOut.println(sub[0]+" -> "+sub[1]+" -> "+sub[2]+" -> "+sub[3]);
            }
        }
        
        collinear(arr, 1, i1, i2, i3, i4 + 1);
        if(p>1) collinear(arr, 2, i1, i2, i3 + 1, i4 + 1);
        if(p>2) collinear(arr, 3, i1, i2 + 1, i3 + 1, i4 + 1);
        if(p>3) collinear(arr, 4, i1 + 1, i2 + 1, i3 + 1, i4 + 1);
    }
}






