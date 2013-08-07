
public class DiningPhilosophers {
    
    static final int nTimesToEat = 5;
    static final int nOfChopsticks = 5;
    static final int nOfPhilosopers = 5;
    static final MonitoredTable table = new MonitoredTable(nOfChopsticks);
    
    public static void main(String args[]) throws InterruptedException {
        Philosopher[] phils = new Philosopher[nOfPhilosopers];
        
        System.out.println("Dinner is starting!\n");
        
        // init threads and start them
        for (int i = 0; i < nOfPhilosopers; i++) 
            (phils[i] = new Philosopher(i, table)).start();

        // this will block the execution of the main thread from 
        // proceeding until philosophers threads has completed
        for (Philosopher p : phils) 
            p.join();       
              
        System.out.println("\nDinner is over!");
    }
    
}

class Philosopher extends Thread {
    
    private int pid;
    private MonitoredTable table;
    
    public Philosopher(int x, MonitoredTable table) { 
        this.table = table;
        this.pid = x;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < DiningPhilosophers.nTimesToEat; i++) {            
            // try to pick up both chopsticks and eat
            try {
                
                table.pickSticks(pid);
                System.out.println("Philosopher "+(pid+1)+" eats.");
                
                // then put them down
                table.putDownSticks(pid); 
                
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
}

class MonitoredTable {
    
    private boolean chopsticks[];

    public MonitoredTable(int size) {
        chopsticks = new boolean[size];
        for (int i = 0; i < size; i++) 
            chopsticks[i] = true;
    }

    public synchronized void pickSticks(int pid) throws InterruptedException {
        int index1 = pid;
        int index2;

        if (pid == 0) index2 = chopsticks.length - 1;
        else index2 = pid - 1;

        // think and wait for other philosophers to put down both chopsticks
        while (!chopsticks[index1] || !chopsticks[index2]) 
            wait();

        chopsticks[index1] = false;
        System.out.println("Philosopher "+(pid+1)+" picks up left chopstick.");
        
        chopsticks[index2] = false;
        System.out.println("Philosopher "+(pid+1)+" picks up right chopstick.");
    }

    public synchronized void putDownSticks(int pid) {
        int index1 = pid;
        int index2;

        if (pid == 0) index2 = chopsticks.length - 1;
        else index2 = pid - 1;

        chopsticks[index1] = true;
        System.out.println("Philosopher "+(pid+1)+" puts down left chopstick.");
        
        chopsticks[index2] = true;
        System.out.println("Philosopher "+(pid+1)+" puts down right chopstick.");
        
        // wakes up all philosopher's threads that are waiting for chopsticks
        notifyAll();
    }
    
}
