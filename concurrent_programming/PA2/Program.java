import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Program {

	private static final Integer NO_OF_PHILOSOPHERS = 5;
	private static final Integer NO_OF_TIMES_EAT = 5;
	private static final Long DELAY = 1000L;
	
	private final Lock lock = new ReentrantLock();
	
	// condition(wait queues) for each chopstick number
	private List<Condition> chopsticks;
	// availaible[n]==1 implies (n+1)th chopstick is free
	private int[] available;
	
	// each philosopher functions are run in a separate thread
	private class Philosopher extends Thread {
		private Program program;
		private int counter = NO_OF_TIMES_EAT;
		
		public Philosopher(Program p) {this.program = p;}
		
		public void run(){
			while(counter!=0){
				program.eat(this);	//eat philosopher eat!!
				Program.randomDelay();  // think philosopher think!!				
				counter--;
			}
		}
	}
	
	//initialize variables
	public Program() {
		chopsticks = new ArrayList<Condition>();
		available = new int[NO_OF_PHILOSOPHERS];
		for(int i=1; i<=NO_OF_PHILOSOPHERS; i++){
			chopsticks.add(lock.newCondition());
			available[i-1] = 1; // all chopsticks are available at the start
		}
	}
	
	// a random delay is used to mimic real world behavior
	public static void randomDelay(){
		try {
			Thread.sleep((long) (Math.random()*DELAY));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}		
	}
	
	
	public void eat(Philosopher philosopher) {
		// get philosopher number
		int number = Integer.parseInt(philosopher.getName().split(" ")[1]);
		
		/* Even numbered philosopher will pick left chopstick first
		 * Odd numbered philosopher will pick right chopstick first
		 * This is a simple measure for deadlock avoidance.
		 */		
		if(number%2==0) {
			// pick left chop stick
			getChopStick(number % NO_OF_PHILOSOPHERS + 1);
			Program.randomDelay();
			System.out.println(philosopher.getName()+" picks up left chopstick");
			
			//pick right chop stick
			getChopStick(number);
			Program.randomDelay();
			System.out.println(philosopher.getName()+" picks up right chopstick.");
		} else {
			//pick right chop stick
			getChopStick(number);
			Program.randomDelay();
			System.out.println(philosopher.getName()+" picks up right chopstick.");
			
			// pick left chop stick
			getChopStick(number % NO_OF_PHILOSOPHERS + 1);
			Program.randomDelay();
			System.out.println(philosopher.getName()+" picks up left chopstick");
		}
		
		System.out.println(philosopher.getName()+" eats.");	
		Program.randomDelay();
	
		// release right chop stick
		releaseChopStick(number);
		Program.randomDelay();
		System.out.println(philosopher.getName()+" puts down right chopstick.");
		
		// release left chop stick
		releaseChopStick(number % NO_OF_PHILOSOPHERS + 1);
		Program.randomDelay();
		System.out.println(philosopher.getName()+" puts down left chopstick.");
	}
	
	
	private void releaseChopStick(int number) {
		lock.lock();		
	
		available[number-1] = 1;
		chopsticks.get(number-1).signal();			// signal availability to other waiting philosophers
		
		lock.unlock();	    // release lock
	}


	private void getChopStick(int number) {
		lock.lock();
		try {
			while(available[number-1]==0)	// wait if chopstick is being used
				chopsticks.get(number-1).await();	// enter into wait state
				
			available[number-1] = 0;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();		// release lock
		}
	}


	public static void main(String args[]) {
		Program program = new Program();
		
		System.out.println("Dinner is starting!");
		
		// create philosophers as separate threads
		ExecutorService executor = 	Executors.newFixedThreadPool(NO_OF_PHILOSOPHERS);
		for(int i = 1; i<=NO_OF_PHILOSOPHERS; i++){
			Philosopher p = program.new Philosopher(program);
			p.setName("Philosopher "+i);
			executor.execute(p);			
		}
		
		// wait until all philosophers have eaten a designated number of times
		executor.shutdown();
		while(!executor.isTerminated()){}
		
		System.out.println("Dinner is over!");
		
	}
	
	
}