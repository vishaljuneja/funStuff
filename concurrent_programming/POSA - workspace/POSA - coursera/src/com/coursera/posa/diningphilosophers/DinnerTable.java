package com.coursera.posa.diningphilosophers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DinnerTable {

	private static final Integer NO_OF_PHILOSOPHERS = 5;
	private static final Integer NO_OF_TIMES_EAT = 5;
	
	private final Lock lock = new ReentrantLock();
	
	// condition(wait queues) for each chopstick number
	private List<Condition> chopsticks;
	// availaible[n]==1 implies (n+1)th chopstick is free
	private int[] available;
	
	// each philosopher functions are run in a separate thread
	private class Philosopher extends Thread {
		private DinnerTable table;
		private int counter = NO_OF_TIMES_EAT;
		
		public Philosopher(DinnerTable t) {this.table = t;}
		
		public void run(){
			while(counter!=0){
				table.eat(this);	//eat philosopher eat!!
				DinnerTable.randomDelay();  // think philosopher think!!				
				counter--;
			}
		}
	}
	
	//initialize parameters
	public DinnerTable() {
		chopsticks = new ArrayList<Condition>();
		available = new int[NO_OF_PHILOSOPHERS];
		for(int i=1; i<=NO_OF_PHILOSOPHERS; i++){
			chopsticks.add(lock.newCondition());
			available[i-1] = 1; // all chopsticks are available
		}
	}
	
	// a random delay of 0-1 second is introduced to mimic real world behavior
	public static void randomDelay(){
		try {
			Thread.sleep((long) (Math.random()*1000L));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}		
	}
	
	
	public void eat(Philosopher philosopher) {
		// get philosopher number
		int number = Integer.parseInt(philosopher.getName().split(" ")[1]);
		
		/* Even number philosopher will pick left chopstick first and then right
		 * Odd number philosopher will pick right chopstick first
		 * This is a simple measure for deadlock avoidance.
		 */		
		if(number%2==0) {
			// pick left chop stick
			getChopStick(number % NO_OF_PHILOSOPHERS + 1);
			DinnerTable.randomDelay();
			System.out.println(philosopher.getName()+" picks up left chopstick");
			
			//pick right chop stick
			getChopStick(number);
			DinnerTable.randomDelay();
			System.out.println(philosopher.getName()+" picks up right chopstick.");
		} else {
			//pick right chop stick
			getChopStick(number);
			DinnerTable.randomDelay();
			System.out.println(philosopher.getName()+" picks up right chopstick.");
			
			// pick left chop stick
			getChopStick(number % NO_OF_PHILOSOPHERS + 1);
			DinnerTable.randomDelay();
			System.out.println(philosopher.getName()+" picks up left chopstick");
		}
		
		System.out.println(philosopher.getName()+" eats.");	
		DinnerTable.randomDelay();
	
		// release right chop stick
		releaseChopStick(number);
		DinnerTable.randomDelay();
		System.out.println(philosopher.getName()+" puts down right chopstick.");
		
		// release left chop stick
		releaseChopStick(number % NO_OF_PHILOSOPHERS + 1);
		DinnerTable.randomDelay();
		System.out.println(philosopher.getName()+" puts down left chopstick.");
	}
	
	
	private void releaseChopStick(int number) {
		lock.lock();		
	
		available[number-1] = 1;
		chopsticks.get(number-1).signal();			// signal availability to other waiting philosophers
		
		lock.unlock();	
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
			lock.unlock();	
		}
	}


	public static void main(String args[]) {
		DinnerTable table = new DinnerTable();
		
		System.out.println("Dinner is starting!");
		
		// create philosophers as separate threads
		ExecutorService executor = 	Executors.newFixedThreadPool(NO_OF_PHILOSOPHERS);
		for(int i = 1; i<=NO_OF_PHILOSOPHERS; i++){
			Philosopher p = table.new Philosopher(table);
			p.setName("Philosopher "+i);
			executor.execute(p);			
		}
		
		// wait until all philosophers have eaten a designated number of times
		executor.shutdown();
		while(!executor.isTerminated()){}
		
		System.out.println("Dinner is over!");
		
	}
	
	
}
