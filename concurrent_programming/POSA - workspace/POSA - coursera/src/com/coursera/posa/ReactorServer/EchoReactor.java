package com.coursera.posa.ReactorServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Stack;

public class EchoReactor {
	
	public static void main(String args[]){
		if(args.length == 0) {
			System.out.println("Incorrect usage: port number not specified");
			System.exit(0);
		}
		Integer pool = Integer.parseInt(args[0]);
		Integer port = Integer.parseInt(args[1]);
		
		//instantiate facade to awaken server
		new EchoReactor().new ServerFacade(pool, port);
	}

	
	/** 
	 * A facade which hides/abstracts the internal functioning of the Reactor
	 */
	public class ServerFacade {
		private Reactor reactor;
		public ServerFacade(Integer pool, Integer port) {
			reactor = new Reactor(port, pool);
			reactor.start();
		}

	}	
	
	/**
	 * This is main "Reactor - a Synchronous event demultiplexer"
	 * Selects events (like read() or accept()) from a selector and calls appropriate handlers
	 * The handlers create tasks after handling appropriate socket operations,
	 * the tasks are then run by worker threads
	 */
	private class Reactor extends Thread {
		
		private int _pool_size;
		private int _port;
		private Worker workers[];
		private Selector _selector;
		protected volatile boolean _shouldRun = true;
		private volatile Stack<Task> _tasks = new Stack<Task>();
		
		public Reactor(int port, int size) {
			this._port = port;
			this._pool_size = size;
			workers = new Worker[this._pool_size];
		}
		
		public void run() {
			try {
				startWorkers();
				_selector = Selector.open();
				ServerSocketChannel sschannel = ServerSocketChannel.open();
				sschannel.configureBlocking(false);
				sschannel.socket().bind(new InetSocketAddress( _port));

				sschannel.register(_selector, SelectionKey.OP_ACCEPT, new Acceptor(sschannel, _selector, _tasks));

				while(_shouldRun) {
					_selector.select();
					for(SelectionKey s : _selector.selectedKeys()){
						if(s.isValid() && s.isAcceptable()) {
							((Acceptor)s.attachment()).accept();
						}

						if(s.isValid() && s.isReadable()) {
							((Reader)s.attachment()).read();
						}
					}
				}
			} catch (Exception e) {
				stopReactor();
			}
		}

		public void stopReactor() {
			_shouldRun = false;
		}

		private void startWorkers() {
			for(int i = 1; i<=_pool_size; i++) {
				workers[i-1] = new Worker("Worker_thread_"+i);
				workers[i-1].start();
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
					try {
						if(task!=null){
							task.execute();
							System.out.println(getName() + ": executed");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Handler which accepts new connection once registered channel is ready
	 * The main operation is then called from the reactor
	 */
	private class Acceptor {

		private ServerSocketChannel sschannel;
		private Selector selector;
		private Stack tasks;
		
		public Acceptor(ServerSocketChannel channel, Selector selector, Stack<Task> tasks) throws IOException {
			sschannel = channel;
			this.selector = selector;
			this.tasks = tasks;
		}

		public void accept() throws IOException {
			SocketChannel channel = sschannel.accept();
			
			if(channel!=null) {
				System.out.println("Listening to  "+channel.getRemoteAddress());
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ, new Reader(channel, tasks));
			}
		}
		

	}
	
	/**
	 * This handler reads data from the selected channel and creates a task to process that data
	 */	
	private class Reader {
		private SocketChannel schannel;
		//private String MESSAGE_END = "\n";
		private Integer BUFFER_SIZE = 256;
		private String _incoming;
		private Stack tasks;
		
		public Reader(SocketChannel channel, Stack tasks) {
			this.schannel = channel;
			this._incoming = "";
			this.tasks = tasks;
		}
		
		public void read() throws IOException {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
			System.out.println("Reading from "+schannel.getRemoteAddress());
			try {
				buf.clear();
				int numBytes = schannel.read(buf);
				if(numBytes == -1) schannel.close();
				
				if(numBytes>0) {
					buf.flip();
					_incoming += new String(buf.array(), 0, numBytes);
				}
			} catch (IOException e) {
				schannel.close();
				e.printStackTrace();
			}
			tasks.add(new EchoBackTask(_incoming, schannel));
		}	
		
	}
	
	private class EchoBackTask implements Task {
		private SocketChannel schannel;
		private String message;
		public EchoBackTask(String _incoming, SocketChannel schannel) {
			this.message = _incoming;
			this.schannel = schannel;
		}

		public void execute() throws IOException {
			if(schannel.isConnected()) 
				schannel.write(ByteBuffer.wrap(message.getBytes()));			
		}		
	}
	
	/**
	 * Tasks created by handlers to be processed by worker threads
	 */
	private interface Task {
		public void execute() throws IOException;
	}
	
}
