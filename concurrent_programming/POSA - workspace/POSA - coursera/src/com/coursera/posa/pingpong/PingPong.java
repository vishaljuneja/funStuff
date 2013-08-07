package com.coursera.posa.pingpong;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * peer 3 → Code is well-structured and design makes sense: 
 * The exceptions InterruptedException | BrokenBarrierException e do stop the execution of the whole 
 * thread which is bad Concurrency controls are used correctly: 
 * Your program uses way to much resources (busy-waiting). 
 * You should not do ping.isAlive() but ping.join() because then you are not wasting resources 
 * with the empty {} in the wile loop Also the if(flag.intValue() == 1) in the while loop wastes resources 
 * because both threads loops are running all the time only doing sometimes some work -> if you use 
 * any of the proposed mechanisms your threads are not burning CPU resources while waiting 
 * The cyclicbarrier is useless here. 
 * The code does also work without it and its purpose is to wait for example until other threads finished 
 * their calculations (wait for dependencies!). 
 * Code demonstrates the appropriate level of readability for others to understand the intent and 
 * execution of the program: should have some comments as your use of concurrency mechanisms is quite 
 * strange
 */


public class PingPong {
	
	// No of times to print either Ping or Pong
	private static final Integer number_of_runs = 300000;
	// CyclicBarrier used to start execution of two thread simultaneously
	private final CyclicBarrier gate = new CyclicBarrier(3);
	
	// flag = 0 => thread Ping can print
	// flag = 1 => thread Pong can print
	private AtomicInteger flag = new AtomicInteger(0);
	
	public class Ping implements Runnable {
		
		public int printed = 0;
		
		@Override
		public void run() {
			try {
				gate.await();
				while(printed < number_of_runs){
					if(flag.intValue() == 0){	// check if Ping can print
						System.out.println("Ping!");
						printed++;
						flag.getAndIncrement(); // signal Pong
					}
				}
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public class Pong implements Runnable {

		public int printed = 0;
		
		@Override
		public void run() {
			try {
				gate.await();
				while(printed < number_of_runs){
					if(flag.intValue() == 1) { // check if Pong can print
						System.out.println("Pong!");
						printed++;
						flag.getAndDecrement();  // signal Ping
					}
				}
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String args[]){
		
		PingPong pp = new PingPong();
		Thread ping = new Thread(pp.new Ping());
		Thread pong = new Thread(pp.new Pong());
		
		ping.start();
		pong.start();
		
		try {
			pp.gate.await(); // now start execution of threads
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		// wait until both threads finish execution
		while(ping.isAlive() || pong.isAlive()){}
		System.out.println("Done!");
	}
	
}
