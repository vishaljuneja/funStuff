import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
   
    private static final int initialCapacity = 8;
    private Item array[];
    private int lastIndex;
    private int count;
    private ArrayList<Integer> emptySites;
    
    // construct an empty randomized queue
    public RandomizedQueue(){
        this.array = (Item[]) new Object[initialCapacity];
        count = 0;
        lastIndex = 0;
        emptySites = new ArrayList<Integer>();
    }
    
    // is the queue empty?
    public boolean isEmpty(){
        return count == 0;
    }
    
    // return the number of items on the queue
    public int size(){
        return count;
    }
    
    // add the item
    public void enqueue(Item item){
        if(item == null) throw new NullPointerException();        
        
        if(emptySites.size()!=0){ 
            array[emptySites.get(0)] = item;
            emptySites.remove(0);
            count++;
            return;
        }
        
        if(count == array.length) resize();
        
        array[lastIndex] = item;
        lastIndex++;
        count++;
    }
    
    // delete and return a random item
    public Item dequeue(){
        if(isEmpty()) throw new 
            java.util.NoSuchElementException("Can not remove from empty queue");
        
        if(count < array.length/2 && array.length>8) compact();
        
        int index = StdRandom.uniform(lastIndex);
        while(array[index] == null){
            index = StdRandom.uniform(lastIndex);
        }
        
        emptySites.add(0,index);
        count--;
        Item result = array[index];
        array[index] = null;
        return result;
    }
      
   // return (but do not delete) a random item              
    public Item sample(){
        if(isEmpty()) throw new 
            java.util.NoSuchElementException("Can not remove from empty queue");
        
        if(count < array.length/2 && array.length>8) compact();
        
        int index = StdRandom.uniform(lastIndex);
        while(array[index] == null){
            index = StdRandom.uniform(lastIndex);
        }
        
        return array[index];
    }
    
    private void resize(){
        Item bigger[] = (Item[]) new Object[array.length*2];
        for(int i=0; i<array.length; i++){
           bigger[i] = array[i];
        }
        array = bigger;
    }
    
    private void compact(){
        Item smaller[] = (Item[]) new Object[array.length/2];
        int index = 0;
        for(int i=0; i<array.length; i++){
            if(array[i]!=null){ 
                smaller[index] = array[i];
                index++;
            }
        }
        array = smaller;
        lastIndex = count;
        emptySites = new ArrayList();
        //StdOut.println("after compaction");
        //showArray();
    }
    
   // return an independent iterator over items in random order    
    public Iterator<Item> iterator(){
        return new ListIterator();
    }
    
    private class ListIterator implements Iterator<Item> {
        private Item arrayCopy[];
        private int current;
        
        public boolean hasNext()  { return current != arrayCopy.length;   }
        public void remove()      
        { throw new UnsupportedOperationException(); }
        
        
        public ListIterator(){
            int index = 0;
            arrayCopy = (Item[]) new Object[count];
            for(int i=0; i<lastIndex; i++){
                if(array[i]!=null){
                    arrayCopy[index] = array[i];
                    index++;
                }
            }
            assert index==count;
            StdRandom.shuffle(arrayCopy);
            current = 0;
        }
        
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = arrayCopy[current];
            current = current + 1; 
            return item;
        }
    }
    
    private void showArray(){
       StringBuilder sb = new StringBuilder();
       for(int i=0; i<array.length; i++){
           sb.append(array[i]+" ");
       }
       StdOut.println(sb);
    }
    
    public static void main(String args[]){
        //RandomizedQueue rq = new RandomizedQueue();
    }
}