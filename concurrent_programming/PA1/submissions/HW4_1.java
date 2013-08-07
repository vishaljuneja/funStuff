/*
 * https://class.coursera.org/posa-001/human_grading/view/#courses/970268/assessments/14/submissions
 * 
You are to design a simple Java program where you create two threads, Ping and Pong, to alternately display “Ping” and “Pong” respectively on the console.  The program should create output that looks like this:

Ready… Set… Go!

Ping!
Pong!
Ping!
Pong!
Ping!
Pong!
Done!

It is up to you to determine how to ensure that the two threads alternate printing on the console, and how to ensure that the main thread waits until they are finished to print: “Done!”  The order does not matter (it could start with "Ping!" or "Pong!").

 Consider using any of the following concepts discussed in the videos:

·      wait() and notify()

·      Semaphores

·      Mutexes

·      Locks

Please design this program in Java without using extra frameworks or libraries (you may use java.util.concurrent) and contain code in a single file.  Someone should be able to run something like “javac Program.java” and “java Program” and your program should successfully run!

20 possible points

·      5 points – code is well-structured and design makes sense

·      5 points – concurrency controls are used correctly

·      5 points – code demonstrates the appropriate level of readability for others to understand the intent and execution of the program

·      5 points – code compiles and runs smoothly


 */

public class HW4_1 {

	/**
	 * @param args
	 * 
	 * Someone should be able to run something like 
	 * “javac Program.java” and “java Program” 
	 * and your program should successfully run!
	 */
	public static void main(String[] args) 
	{
		// Based on the assignment description, it is OK to hardcode the 
		// number of times Ping and Pong will be echoed to the console.
		int thisManyTimes = 5;

		// An instance of the HW4_1 class will set up the two threads, start them
		// and wait for them to finish.
		new HW4_1(thisManyTimes);

	}

	// Constants for the echoed text.
	private static final String T_PING = "Ping!";
	private static final String T_PONG = "Pong!";
	

	// An object of this class will be used to synchronize the Ping and Pong threads.
	private class Signal
	{
		public String sigValue="";
	}
	private static Signal signal;
	
	// An object of this class will echo either Ping or Pong, but not both,
	// to the console when it is its turn to do so.
	private class Echo implements Runnable
	{
		// Number of times to echo the text.
		private int thisManyTimes=0;
		
		// The text to be echoed.
		private String text;

		public Echo (String s, int times)
		{
			text = s;
			thisManyTimes = times;
		}

		public void run() 
		{
			echo();
		}

		private void toggleSignalValue()
		{
			if (signal.sigValue.equals(T_PING))
				signal.sigValue = T_PONG;
			else
				signal.sigValue = T_PING;
		}

		// This method will try to obtain the rights to echo the assigned text
		// If it can enter the critical section, then it has the right to
		// echo. 
		// We have two threads in this exercise. When one thread enters the critical
		// section, the other thread cannot. As soon as the first thread leaves the
		// critical section, the second thread enters and shut out the first thread
		// from re-entering until it is done. Therefore, alternation is achieved.

		// The shutting out is done by the 
		// "while (!signal.sigValue.equals(text)) signal.wait()" construct.
		// It will compensate for the fact that a thread is so fast that it reenters the
		// critical section again and again even though it is not its turn.

		public void echo() 
		{
			int iterationLeft = thisManyTimes;
			while (iterationLeft > 0)
			{
				synchronized(signal)
				{
					// Make sure the threads take turn.
					while (!signal.sigValue.equals(text))
						try 
						{
							signal.wait();
						} 
						catch (InterruptedException e) 
						{
							System.out.println("The wait is interrupted. " + e.getMessage());
							e.printStackTrace();
						}
					
					// Echo the string.
					System.out.println(text);
					
					iterationLeft--;
					
					// Tell the other thread to take a turn.
					toggleSignalValue();
					signal.notify();		
				}
			}	
		}		
	}


	public HW4_1(int thisManyTimes)
	{

		System.out.println ("Ready...Set...Go!");
		// This is the object used to synchronize the threads.
		signal = new Signal();
		signal.sigValue = T_PING;
		
		Thread pingThread = new Thread (new Echo(T_PING, 10));
		Thread pongThread = new Thread (new Echo(T_PONG, thisManyTimes));

		// Start the threads to do the echoing.
		pingThread.start();
		pongThread.start();

		// Wait for both threads to finish.
		try 
		{
			pingThread.join();
			pongThread.join();
		} 
		catch (InterruptedException e) 
		{
			System.out.println ("The program is interrupted : " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println ("Done!");
	}

}

