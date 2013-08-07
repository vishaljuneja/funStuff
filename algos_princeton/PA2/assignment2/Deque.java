import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    
    private int N;     // no of items in queue
    private Node begin;
    private Node end;
    
    private class Node {
        public Node(Item item){
            this.item = item;
        }
        private Item item;
        private Node next;
        private Node previous;
    }
    
    public Deque() {                    // construct an empty deque
        begin = null;
        end = null;
        N = 0;
    }
    
    public boolean isEmpty(){           // is the deque empty?
        if(N==0) return true;
        return false;
    }
    
    // return the number of items on the deque
    public int size(){
        return N;
    }
    
    // insert the item at the front
    public void addFirst(Item item){
        if(item == null) throw new NullPointerException();
        Node node = new Node(item);
        node.previous = null;
        if(isEmpty()){
            node.next = null;
            begin = node;
            end = node;
            N++;
            return;
        }
        node.next = begin;
        begin.previous = node;
        begin = node;
        N++;
    }
    
    // insert the item at the end
    public void addLast(Item item){
        if(item == null) throw new NullPointerException();
        Node node = new Node(item);
        node.next = null;
        if(isEmpty()){
            node.previous = null;
            end = node;
            begin = node;
            N++;
            return;
        }
        node.previous = end;
        end.next = node;
        end = node;
        N++;
    }
    
    // delete and return the item at the front
    public Item removeFirst(){
        if(isEmpty()) throw new 
            NoSuchElementException("Can not remove from empty queue");
        Item item = begin.item;
        if(N==1){            
            begin = end = null;
            N--;
            return item;
        }
        begin = begin.next;
        begin.previous = null;
        N--;
        return item;
    }
    
    // delete and return the item at the end
    public Item removeLast(){
        if(isEmpty()) throw new
            NoSuchElementException("Can not remove from empty queue");
        Item item= end.item;
        if(N==1) {
            begin = end = null;
            N--;
            return item;
        }
        end = end.previous;
        end.next = null;
        N--;
        return item;
    }
    
    // return an iterator over items in order from front to end
    public Iterator<Item> iterator(){
        return new ListIterator();
    }
    
    private class ListIterator implements Iterator<Item> {
        private Node current = begin;
        
        public boolean hasNext()  { return current != null;                 }
        public void remove()      { throw new UnsupportedOperationException(); }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }
    
    
    public static void main(String[] args){
        Deque<Integer> dq = new Deque<Integer>();
        //dq.addFirst(5);
        //StdOut.println(dq.removeLast());
        //dq.addLast(1);
        //StdOut.println(dq.removeFirst());
        //StdOut.println(dq.size());
        for(int i = 1; i<=5; i++){
            dq.addLast(i);
            //StdOut.println(dq.size());
            dq.addFirst(i);
            //StdOut.println(dq.removeFirst());
            //StdOut.println(dq.size());
        }
        //StdOut.println(dq.size());
        
        Iterator<Integer> it = dq.iterator();
        while(it.hasNext()){
            StdOut.println(it.next());
        }
        it.next();
    }
}