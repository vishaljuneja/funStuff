import java.util.ArrayList;
import java.util.Iterator;

public class PointSET {
    
    private SET<Point2D> data;
    
    // construct an empty set of points
    public PointSET(){
        this.data = new SET<Point2D>();
    }
    
    // is the set empty?
    public boolean isEmpty(){
        if(data.size() == 0) return true;
        return false;
    }
    
    // number of points in the set
    public int size(){
        return data.size();
    }
    
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p){
        data.add(p);
    }
    
    // does the set contain the point p?
    public boolean contains(Point2D p){
        return data.contains(p);
    }
    
    // draw all of the points to standard draw
    public void draw(){
        Iterator<Point2D> points = data.iterator();
        while(points.hasNext()){
            Point2D p = points.next();
            StdDraw.point(p.x(), p.y());
        }     
    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect){
        ArrayList<Point2D> result = new ArrayList<Point2D>();
        Iterator<Point2D> points = data.iterator();
        while(points.hasNext()){
            Point2D p = points.next();
            if(rect.contains(p)) result.add(p);
        }
        return result;
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p){
        if(data.isEmpty()) return null;        
        double min = Double.MAX_VALUE;
        Point2D result = null;
        Iterator<Point2D> points = data.iterator();
        while(points.hasNext()){
            Point2D point = points.next();
            double temp = point.distanceTo(p);
            if(temp < min){
                min = temp;
                result = point;
            }
        }        
        return result;      
    }
    
}