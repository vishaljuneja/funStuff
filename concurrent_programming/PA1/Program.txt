import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Program {
	
	// No of times to print either Ping or Pong
	private static final Integer number_of_runs = 3;
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
		
		Program pp = new Program();
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
