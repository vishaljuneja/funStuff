public class PingPong {

	//Number of times the printToConsole Method has been called
	private static int printTimes = 0;

	public static void main(String[] args) {
		//Init threads for runnable private inner classes
		Thread a = new Thread(new Ping());
		Thread b = new Thread(new Pong());
		a.start();
		b.start();
		try {
			//Join the first thread so the main thread does not terminate the two runnables
			//The runnables are agnostic to print counts. So A will not terminate 
			//Until the main thread calls the exit method. So deciding which thread to join
			//is arbitrary...So let's just pick the first
			a.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	//Method each thread will call to print it's statement
	public static synchronized void printToConsole(String myPrint) {
		//If we are above 5 prints i.e six. We need to exit. 
		//This is specifically an if -> else, so that any thread can terminate the program
		if(printTimes > 5){
			System.out.println("Done!");
			//ZERO for non-abnormal shutdown
			Runtime.getRuntime().exit(0);
		} else {
			System.out.println(myPrint);
			//Increment prints.
			printTimes++;
			
		}
	}

	//Ping runnable to be used in Thread constructor
	private static class Ping implements Runnable {
		private String myPrint = "Ping";
		//Will be set to true during thread.start()
		private boolean isRunning;
		
		@Override
		public void run() {
			isRunning = true;
			//Loop until we have printed our maximum times
			//as decided by the main loop's printToConsole method.
			while(isRunning) {
				//Only print if printTimes is odd: Modulo Operator
				if(printTimes%2!=0) {
					printToConsole(myPrint);
				} else {
					try {
						//Let's sleep so the other thread can do it's work
						//and Increment the counter
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}	
	
	//Pong runnable to be used in Thread constructor
	private static class Pong implements Runnable {
		private String myPrint = "Pong";
		//Will be set to true during thread.start()
		private boolean isRunning;
		
		@Override
		public void run() {
			isRunning = true;
			//Loop until we have printed our maximum times
			//as decided by the main loop's printToConsole method.
			while(isRunning) {
				//Only print if printTimes is even: Modulo Operator
				if(printTimes%2!=1) {
					printToConsole(myPrint);
				} else {
					try {
						//Let's sleep so the other thread can do it's work
						//and Increment the counter
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	}
	

}
