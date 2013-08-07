package com.coursera.posa.pingpong;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class PingPongMonitor {

	private static final Integer number_of_runs = 40000;
	
	
	
	private AtomicBoolean turn = new AtomicBoolean(false);
	
	private class Ping extends Thread {
		private int runs = number_of_runs;
		private Object monitor;
		private Semaphore mySemaphore;
		
		public Ping(Object monitor, Semaphore semaphore) {
			this.monitor = monitor;
			this.mySemaphore = semaphore;
		}
		
		
		public void run(){
			
			while(runs>0){
				if(turn.get()){
					/// all thread specific work here
					System.out.println("Ping!");
					runs--;
					/////////////////
					
					//turn = false;
					
					synchronized(monitor) {
						try {
							mySemaphore.acquire();
							turn.set(false);
							mySemaphore.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						monitor.notify();							
					}
				} else {
					try {
						synchronized(monitor) {	
							monitor.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}		
	}
		
	private class Pong extends Thread {
		private int runs = number_of_runs;
		private Object monitor;
		private Semaphore mySemaphore;
		
		public Pong(Object monitor, Semaphore semaphore) {
			this.monitor = monitor;
			this.mySemaphore = semaphore;
		}
		
		
		public void run(){
			
			while(runs>0){
				if(!turn.get()){
					/// all thread specific work here
					System.out.println("Pong! "+runs);
					runs--;
					/////////////////
					
					//turn = true;
					
					
					
					synchronized(monitor) {
						try {
							mySemaphore.acquire();
							turn.set(true);
							mySemaphore.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						monitor.notify();						
					}
				} else {
					try {
						synchronized(monitor) {
							monitor.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
		
	public static void main(String args[]){
		PingPongMonitor ppm = new PingPongMonitor();
		Semaphore s = new Semaphore(0);
		Thread ping = ppm.new Ping(ppm, s);
		Thread pong = ppm.new Pong(ppm, s);
		
		System.out.println("Start...");
		
		ping.start();
		pong.start();
		
		try {
			ping.join();
			pong.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done!");
		
	}
}
