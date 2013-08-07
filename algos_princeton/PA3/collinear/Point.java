/*************************************************************************
 * Name:
 * Email:
 *
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/

import java.util.Comparator;
import java.util.Arrays;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER = new Comparator<Point>() {
        public int compare(Point point1, Point point2){
            double slope1 = slopeTo(point1);
            double slope2 = slopeTo(point2);
            
            if(slope1>slope2) return 1;
            else if(slope1<slope2) return -1;
            else return 0;
        }
    };

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
        double dy = that.y - this.y;
        double dx = that.x - this.x;
        if(dx == 0){
            if(dy == 0) return Double.NEGATIVE_INFINITY;
            else return Double.POSITIVE_INFINITY;
        }
        if(dy == 0) dx = Math.abs(dx);
        return dy/dx;
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    public int compareTo(Point that) {
        int dy = this.y - that.y;
        if(dy>0){
            return 1;
        } else if(dy<0) {
            return -1;
        } else {
            int dx = this.x - that.x;
            if(dx>0) return 1;
            else if(dx<0) return -1;
            else return 0;
        }
    }

    // return string representation of this point
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }
    
    private void show(Point a[]){
        for(int i=0; i<a.length; i++){
            StdOut.println(a[i]);
        }
    }

    // unit test
    public static void main(String[] args) {
        /* YOUR CODE HERE */
        Point p1 = new Point(0,0);
        Point p2 = new Point(2,0);
        Point p3 = new Point(2,2);
        Point p4 = new Point(0,2);
        Point p5 = new Point(1,1);
        
        Point array[] = new Point[] {p1, p2, p3, p4,p5};
        Arrays.sort(array);
        Arrays.sort(array, p1.SLOPE_ORDER);
        p1.show(array);
    }
}