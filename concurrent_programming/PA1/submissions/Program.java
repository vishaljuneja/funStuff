/**
 * 
 */

import java.util.concurrent.*;

/**
 * 
 * 
 */
public class Program {
	
	/**
	 * This enumeration is used as a token for synchronisation of the threads.
	 * A printer thread won't print any message if it's not its turn (this is,
	 * the current active token is the one it's associated with)
	 */
	private enum TokenClass {
		PING, PONG
	}
	
	/**
	 * The current turn represented as a token.
	 */
	private TokenClass mCurrentTurn;
	
	/**
	 * A map of tokens and their associated monitor objects. We use monitor 
	 * objects to control the concurrency of the different threads. Only the
	 * active token's thread and monitor object will be active. The rest of the
	 * threads will wait in their respective monitor objects.
	 */
	private ConcurrentMap<TokenClass, Object> mMonitorObjects;

	/**
	 * This private class implements interface Runnable. It contains a string be 
	 * printed, a token item to which it's associated: the class won't write the
	 * message unless the global token (current turn) is active.
	 */
	private class WriterRunnable implements Runnable {

		/**
		 * Message to be printed on the screen (standard output)
		 */
		private String mString = "";
		/**
		 * The token this writer is associated to. The Runnable will only write
		 * to the screen if it is currently its turn.
		 */
		private TokenClass mAssociatedToken;
		/**
		 * Token for the other thread. This is an optimization, and for a 
		 * pool of more than two threads we would have to use loops.
		 */
		private TokenClass mOtherToken;
		/**
		 * The maximum number of times the Runnable will write its string.
		 */
		private int mRepetitions;

		/**
		 * The constructor for this class receives the string to be printed, the
		 * token it's associated with and the number of times to write the 
		 * message.
		 * @param stringToPrint The message to be printed to standard output.
		 * @param token The token this Runnable is associated with for writing.
		 * @param repetitions The number of times to print the message.
		 */
		public WriterRunnable(String stringToPrint, TokenClass token,
				int repetitions) {
			mString = stringToPrint;
			mAssociatedToken = token;
			mRepetitions = repetitions;
		}

		@Override
		public void run() {
			// The following is an optimisation. If we had to notify (wake) more
			// than one thread we would have to loop through all the monitor 
			// objects and call notify() on all of them. In our dual example, we
			// can pre-store the monitor object of the other token and notify()
			// directly
			Object notificationMonitorObject = null;
			for (TokenClass token : TokenClass.values()) {
				// The other monitor object is the one not equal to our 
				// associated token
				if (token != mAssociatedToken) {
					mOtherToken = token;
					notificationMonitorObject = mMonitorObjects.get(token);
					break;
				}
			}
			// Get our monitor object. This will be used to wait() while it's
			// not our turn
			Object waitMonitorObject = mMonitorObjects.get(mAssociatedToken);
			// While we haven't completed all the write operations
			while (mRepetitions > 0) {
				if (mCurrentTurn == mAssociatedToken) {
					// If it's our turn, we print and notify the other threads
					print();
					synchronized (notificationMonitorObject) {
						notificationMonitorObject.notify();
					}
				} else {
					// It's not our turn, just wait on the monitor object
					try {
						synchronized (waitMonitorObject) {
							waitMonitorObject.wait();	
						}
					} catch (InterruptedException e) {
						System.err.println("WriterRunnable was interrupted");
					}
				}
			}
		}

		/**
		 * The print() method is synchronized because only one thread can modify
		 * the current turn. It prints the string to standard output, then 
		 * decrements the repetitions and updates the current token
		 */
		private synchronized void print() {
			System.out.println(mString);
			mRepetitions--;
			mCurrentTurn = mOtherToken;
		}

	}

	/**
	 * The Runnable that will be used to print "Ping!" messages.
	 */
	private WriterRunnable mPingRunnable;
	/**
	 * The Runnable that will be used to print "Pong!" messages.
	 */
	private WriterRunnable mPongRunnable;

	public Program(int repetitions) {
		// Create the WriterRunnable object used for "Ping!" messages. The token
		// for this object will be PING. Pass the repetitions as a parameter too
		mPingRunnable = new WriterRunnable("Ping!", TokenClass.PING,
				repetitions);
		// Create the WriterRunnable object used for "Pong!" messages. The token
		// for this object will be PONG. Pass the repetitions as a parameter too
		mPongRunnable = new WriterRunnable("Pong!", TokenClass.PONG,
				repetitions);
		// Arbitrarily set the first turn to PING.
		mCurrentTurn = TokenClass.PING;
		// Create the map that contains the tokens and the respective Monitor 
		// Objects
		mMonitorObjects = new ConcurrentHashMap<TokenClass, Object>();
		mMonitorObjects.put(TokenClass.PING, new Object());
		mMonitorObjects.put(TokenClass.PONG, new Object());
	}
	
	/**
	 * @return The Runnable used to print Ping messages.
	 */
	public Runnable getPingRunnable() {
		return mPingRunnable;
	}
	
	/**
	 * @return The Runnable used to print Pong messages.
	 */
	public Runnable getPongRunnable() {
		return mPongRunnable;
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		// Initial message
		System.out.println("Ready... Set... Go!");

		// See class PingPongApplication above. This class contains two 
		// different runnable instances based on the same Runnable class. The
		// number passed as a parameter is the number of messages that will be
		// printed by each Runnable.
		Program application = new Program(5);
		// Create one thread with the Ping message runnable
		Thread pingThread = new Thread(application.getPingRunnable());
		// Create the other thread with the Pong message runnable
		Thread pongThread = new Thread(application.getPongRunnable());
		// Start both threads
		pingThread.start();
		pongThread.start();
		try {
			// Wait for both threads to join
			pingThread.join();
			pongThread.join();
		} catch (InterruptedException e) {
			// If we get interrupted while executing, print an error message
			System.err.println("Main application was interrupted");
		}
		// Exit message
		System.out.println("Done!");
	}

}
