import java.util.ArrayList;

public class KdTree {
    
    private int count;
    private Node root;
    
    // usefull in recursive calls for nearest
    private static Double dMin = Double.MAX_VALUE;
    private static Point2D result; 
    
    
    private static class Node {
        private final Point2D point;
        private Node left;
        private Node right;
        private final RectHV rect;
                
        public Node(Point2D p){
            // make defensive copy
            this.point = new Point2D(p.x(), p.y());
            left = null;
            right = null;
            this.rect = new RectHV(0,0,1,1);
        }
        
        public Node(Point2D p, Node parent, boolean level){
            // make defensive copy
            this.point = new Point2D(p.x(), p.y());
            left = null;
            right = null;
            int comp = parent.compare(p, level);
            
            if(level){ // even level
                if(comp>=0) {
                    this.rect = new RectHV(parent.rect.xmin(),
                                           parent.rect.ymin(),
                                           parent.point.x(),
                                           parent.rect.ymax());
                    parent.left = this;
                }
                else {
                    this.rect = new RectHV(parent.point.x(),
                                           parent.rect.ymin(),
                                           parent.rect.xmax(),
                                           parent.rect.ymax());
                    parent.right = this;
                }
            } else {  // odd level
                if(comp>=0) {
                    this.rect = new RectHV(parent.rect.xmin(),
                                           parent.rect.ymin(),
                                           parent.rect.xmax(),
                                           parent.point.y());
                    parent.left = this;
                }
                else {
                    this.rect = new RectHV(parent.rect.xmin(),
                                           parent.point.y(),
                                           parent.rect.xmax(),
                                           parent.rect.ymax());
                    parent.right = this;
                }
            }
            //StdOut.println("level:"+level+" comp: "+ comp+" "+" Inserted:" + toString(this));
        }
        
        public String toString(Node n){
            StringBuilder sb = new StringBuilder();
            sb.append("Point: "+n.point+ " ");
            sb.append("Enclosing Rectangle: "+n.rect);
            return sb.toString();
        }
        
        public int compare(Point2D p, boolean l){
            if(l){
                if(this.point.x() > p.x()) return 1;
                else if(this.point.x() < p.x()) return -1;
                else return 0;
            } else {
                if(this.point.y() > p.y()) return 1;
                else if(this.point.y() < p.y()) return -1;
                else return 0; 
            }
        }
    }
    
    // construct an empty set of points
    public KdTree(){
        this.count = 0;
    }
    
    // is the set empty?
    public boolean isEmpty(){
        return count == 0;
    }
    
    // number of points in the set
    public int size(){
        return count;
    }
    
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p){
        if(p==null) throw new NullPointerException();
        if(isEmpty()){
            root = new Node(p);
            count++;
            return;
        }
        
        Node parent = null;
        Node temp = root;
        boolean level = true;  //even level => true
        
        while(temp!=null){
            if(p.equals(temp.point)) return;
            parent = temp;
            int comp = temp.compare(p, level);
            if(comp<0) temp = temp.right;
            else temp = temp.left;
            level = !level;
        }
        new Node(p, parent, !level);
        count++;
    }
    
    // does the set contain the point p?
    public boolean contains(Point2D p){
        if(p==null) throw new NullPointerException();
        if(isEmpty()) return false;
        
        Node temp = root;
        boolean level = true;  //even level => true
        
        while(temp!=null){
            if(p.equals(temp.point)) return true;
            int comp = temp.compare(p, level);
            if(comp<0) temp = temp.right;
            else temp = temp.left;
            level = !level;
        }
        return false;        
    }
    
    // draw all of the points to standard draw
    public void draw(){
        draw(root, true);
    }
    
    private void draw(Node n, boolean level){
        if(n == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        StdDraw.point(n.point.x(), n.point.y());
         
        StdDraw.setPenRadius();
        if(level){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.point.x(), n.rect.ymin(), 
                         n.point.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.point.y(),
                         n.rect.xmax(), n.point.y());
            
        }
        draw(n.left, !level);
        draw(n.right, !level);
    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect){
        if(rect==null) throw new NullPointerException();
        
        ArrayList<Point2D> result = new ArrayList<Point2D>();
        if(isEmpty()) return result;
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
        while(!queue.isEmpty()){
            Node n = queue.dequeue();
            if(rect.contains(n.point)) result.add(n.point);
            if(n.left!=null)
               if(rect.intersects(n.left.rect)) queue.enqueue(n.left);
            if(n.right!=null)
               if(rect.intersects(n.right.rect)) queue.enqueue(n.right);
        }        
        return result;
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p){
        if(p == null) throw 
            new NullPointerException();
        if(isEmpty()) return null;
         
        // reset dMin
        /*
        dMin = Double.MAX_VALUE;
        this.result = null;
        nearest(root, p);
        return this.result;
        */
        return breadth(p);
    }   
    
    private void nearest(Node n, Point2D p){
        if(n==null || dMin==0) return;
        
        double rectD = n.rect.distanceSquaredTo(p);
        if(rectD>dMin) return;
        
        double dist = p.distanceSquaredTo(n.point);
        if(dist<dMin) {
           dMin = dist;
           this.result = n.point;
           if(dMin==0) return;
        }
        
        double leftD = Double.MAX_VALUE;
        double rightD = Double.MAX_VALUE;
        
        if(n.left!=null) leftD = n.left.rect.distanceSquaredTo(p);
        if(n.right!=null) rightD = n.right.rect.distanceSquaredTo(p);
        
        if(leftD<=rightD){
            nearest(n.left, p);
            nearest(n.right, p);
        } else {
            nearest(n.right, p);
            nearest(n.left, p);
        }
        
    }
        
    
    private Point2D breadth(Point2D p){
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
        Point2D result = null;
        double dMin = Double.MAX_VALUE;
        while(!queue.isEmpty()){
            Node n = queue.dequeue();
            
            double rectD = n.rect.distanceSquaredTo(p);
            if(rectD>dMin) continue;
            
            double temp = p.distanceSquaredTo(n.point);
            if(temp<dMin) {
                dMin = temp;
                result = n.point;
                if(dMin==0) break;
            }
            
            if(n.left == null && n.right == null) continue;
            
            double leftD = Double.MAX_VALUE;
            double rightD = Double.MAX_VALUE;
            
            if(n.left == null) {
                rightD = n.right.rect.distanceSquaredTo(p);
                if(rightD<dMin){
                    queue.enqueue(n.right);
                }
                continue;
            }
            
            if(n.right == null){
                leftD = n.left.rect.distanceSquaredTo(p);
                if(leftD<dMin){
                    queue.enqueue(n.left);
                }
                continue;
            }

            leftD = n.left.rect.distanceSquaredTo(p);
            rightD = n.right.rect.distanceSquaredTo(p);            
            if(leftD<rightD){
                queue.enqueue(n.left);
                if(rightD<dMin){
                    queue.enqueue(n.right);
                }
            } else {
                queue.enqueue(n.right);
                if(leftD<dMin){
                    queue.enqueue(n.left);
                }
            }            
        }
        return result;
    }
    
}