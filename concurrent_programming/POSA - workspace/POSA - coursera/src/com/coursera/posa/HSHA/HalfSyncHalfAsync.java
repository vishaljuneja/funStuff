package com.coursera.posa.HSHA;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Stack;


/**
 * Credits!! (Classes with their roles):
 * 
 * Reactor -> Dispatches client requests to various handlers using nio select API
 * 
 * HalfSyncPool -> maintains a pool of worker threads which execute tasks once they are added to the task queue by a handler
 * 
 * Handler -> Abstract class with abstract handle() method
 * Acceptor -> A concrete handler which accepts a new connection
 * Reader -> Another concrete handler which reads chunks of data from sockets when they are ready
 * 
 * Task -> an interface with execute() method; to define tasks executed by worker threads. 
 * EchoBackTask -> a concrete task which echoes back each client message
 * 
 * Note: java is in itself a Wrapper for sockets and hides their inner complexities
 */


/**
 * example usage:  java HalfSyncHalfAsync <HalfSyncPool_size> <port_num>
 * java HalfSyncHalfAsync 3 5810
 */

public class HalfSyncHalfAsync {

	public static void main(String args[]){
		if(args.length == 0 || args.length == 1) {
			System.out.println("Incorrect usage: number of worker threads and port number not specified");
			System.exit(0);
		}
		Integer pool = Integer.parseInt(args[0]);
		Integer port = Integer.parseInt(args[1]);
		
		Reactor reactor = new HalfSyncHalfAsync().new Reactor(port, pool);
		reactor.start();
		try {
			reactor.join();
		} catch (InterruptedException e) {
			System.out.println("Reactor interrupted ... " + e);
		}
	}

	
	/**
	 * Dispatches client requests to various handlers using nio select API
	 */
	public class Reactor extends Thread {
		
		private int _port;
		private Selector _selector;
		protected boolean _shouldRun = true;
		private HalfSyncPool _pool;
		
		public Reactor(int port, int size) {
			System.out.println("Initiating reactor port: "+port+" _pool_size:"+size);
			this._port = port;
			this._pool = new HalfSyncPool(size);
		}
		
		public void run() {
			try {
				System.out.println("Starting reactor ...");
				
				_pool.startWorkers();	//start workers
				_selector = Selector.open();	//open selector
				ServerSocketChannel sschannel = ServerSocketChannel.open();
				sschannel.configureBlocking(false);
				sschannel.socket().bind(new InetSocketAddress(_port));

				register(sschannel, SelectionKey.OP_ACCEPT).attach(new Acceptor(sschannel, this));

				while(_shouldRun) {
					// poll unless interrupted
					_selector.select();
					for(SelectionKey key : _selector.selectedKeys()){
						if(key.isValid() && key.isAcceptable()) {
							((Acceptor)key.attachment()).handle();
						}

						if(key.isValid() && key.isReadable()) {
							((Reader)key.attachment()).handle();
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error running reactor "+e);
				stopReactor();
			}
		}

		public void stopReactor() {
			_shouldRun = false;
			_pool.stop();
		}
		
		public SelectionKey register(SelectableChannel channel, int mask) {
			SelectionKey key = null;
			try {
				key = channel.register(_selector, mask);
			} catch (ClosedChannelException e) {
				System.out.println("Error registering channel ...");
				e.printStackTrace();
			}
			return key;
		}

		public void addTask(Task task) {
			_pool.addTask(task);
		}
		
	}
	
	/**
	 * maintains a pool of worker threads which execute tasks once they are added to the task queue by a handler
	 */	
	private class HalfSyncPool {
		private Worker workers[];
		private int _pool_size;
		private volatile boolean _shouldRun = true;
		private volatile Stack<Task> _tasks = new Stack<Task>();
		
		public HalfSyncPool(int _pool_size) {
			this._pool_size = _pool_size;
			workers = new Worker[_pool_size];
		}
		
		public void addTask(Task task) {
			_tasks.add(task);
		}

		// stops all workers which are executing
		public void stop() {
			this._shouldRun = false;
		}

		private void startWorkers() {
			for(int i = 1; i<=_pool_size; i++) {
				workers[i-1] = new Worker("Worker_thread_"+i);
				workers[i-1].start();
				System.out.println("Worker_thread_"+i+" started ...");
			}
			
		}
		
		/**
		 * Worker thread which continues checking the tasks for any new available unit of work
		 */
		private class Worker extends Thread {
			private final static String monitor = "monitor";
			
			public Worker(String name) {
				super(name);
			}
			
			public void run() {
				while(_shouldRun) {
					Task task = null;
					synchronized(monitor) {	// synchronize access to _tasks queue
						if(!_tasks.isEmpty()) {
							task = _tasks.pop();
						}
					}
					if(task!=null){
						System.out.println(getName() + ": executing request");
						task.execute();
					}
				}
			}
		}
	}

	/**
	 * Handler which accepts new connection once registered channel is ready
	 * handle() operation is invoked from within the reactor
	 */
	private class Acceptor extends Handler {

		private ServerSocketChannel sschannel;
		
		public Acceptor(ServerSocketChannel channel, Reactor reactor) throws IOException {
			super(reactor);
			sschannel = channel;
		}

		public void handle() throws IOException {
			SocketChannel channel = sschannel.accept();
			
			if(channel!=null) {
				System.out.println("Listening to  "+channel.getRemoteAddress());
				channel.configureBlocking(false);
				register(channel, SelectionKey.OP_READ, new Reader(channel, reactor));
			}
		}
		

	}
	
	/**
	 * This handler reads data from the selected channel and creates a task to process that data
	 */	
	private class Reader extends Handler {
		private SocketChannel schannel;
		private Integer BUFFER_SIZE = 256;
		private String _incoming;
		
		// each line of the message will be processed separately
		protected String separator = System.lineSeparator();	
		
		
		public Reader(SocketChannel channel, Reactor reactor) {
			super(reactor);
			this.schannel = channel;
			this._incoming = "";
		}
		
		public void handle() throws IOException {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			try {
				buf.clear();
				_incoming = "";
				int numBytes = schannel.read(buf);
				if(numBytes == -1) schannel.close();
				
				if(numBytes>0) {
					buf.flip();
					_incoming += new String(buf.array(), 0, numBytes);
					
					System.out.println("Message received from "+schannel.getRemoteAddress());
					process();
				}
			} catch (IOException e) {
				schannel.close();
				e.printStackTrace();
			}
		}

		// incoming message is processed line wise
		private void process() {
			String lines[] = _incoming.split(separator);
			for(String line : lines) {
				addTask(new EchoBackTask(line, schannel));
			}
		}	
		
	}
	
	/**
	 * An abstract handler class
	 */
	private abstract class Handler {
		protected Reactor reactor;
		
		protected Handler(Reactor reactor) {
			this.reactor = reactor;
		}
		
		protected void register(SelectableChannel channel, int mask, Handler handler) {
			reactor.register(channel, mask).attach(handler);
		}
		
		protected void addTask(Task task) {
			reactor.addTask(task);
		}
		
		public abstract void handle() throws IOException; 
	}
	
	
	/**
	 * echoes back each client message
	 */
	private class EchoBackTask implements Task {
		private SocketChannel schannel;
		private String message;
		
		public EchoBackTask(String _incoming, SocketChannel schannel) {
			this.message = _incoming;
			this.schannel = schannel;
		}

		public void execute() {
			if(schannel.isConnected())
				try {
					synchronized(schannel) { // synchronize socket writes among several threads
						schannel.write(ByteBuffer.wrap(message.getBytes()));
					}
				} catch (IOException e) {
					System.out.println("Error writing "+message+" to the channel ...");
					System.out.println("Connection closed by client ...");
				}			
		}		
	}
	
	/**
	 * Tasks created by handlers to be processed by worker threads
	 */
	private interface Task {
		public void execute();
	}
	
	
	
}
