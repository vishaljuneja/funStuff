import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Echo Server that implements Reactor, Acceptor/Connector and Half-Sync/Half-Async pattern. It uses
 * JAVA NIO functions to select events. For Half Synch part  LinkedBlockingQueue and ThreadPoolExecutor is used.
 * Wrapper Facade pattern are implemented
 * from JAVA NIO classes Tested with Putty in raw socket mode
 */
public class EchoServerHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int port = Integer.parseInt(args[0]);

		EchoReactor reactor = new EchoReactor();

		HalfSyncPool pool = new HalfSyncPool(5);
		pool.createAndRunThreads();

		EchoHandler serviceHandler = new EchoHandler(reactor, pool);
		EchoAcceptor<EchoHandler> acceptor = new EchoAcceptor<EchoHandler>(
				reactor, port, serviceHandler);

		reactor.registerEventHandler(acceptor, acceptor.getEventType());

		reactor.runReactor();
	}

}

/**
 * Half-Sync part of Half-Sync/Half-Async pattern is implemented from this class
 * creates and starts threads which will wait for messages in blocking queue
 */
class HalfSyncPool {
	private LinkedBlockingQueue<ServiceHandlerMessage> queue;
	private ThreadPoolExecutor threadPool = null;
	private int threadPoolSize;
	public boolean running = false;

	HalfSyncPool(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
		queue = new LinkedBlockingQueue<ServiceHandlerMessage>();
		// Keep alive time for waiting threads for jobs(Runnable)
		long keepAliveTime = 100;
		ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
				threadPoolSize);

		threadPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
				keepAliveTime, TimeUnit.SECONDS, workQueue);
	}

	void enqueue(ServiceHandlerMessage svcMessage) {
		System.out.println("Enqueue begin");
		try {
			queue.put(svcMessage);
			System.out.println("Enqueue end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	synchronized ServiceHandlerMessage take() {
		ServiceHandlerMessage svcMessage = null;
		try {
			svcMessage = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Service Thread taken message");

		return svcMessage;
	}

	void createAndRunThreads() {
		running = true;
		for (int i = 0; i < threadPoolSize; i++) {
			threadPool.execute(new ServiceThread());
		}
	}

	private class ServiceThread implements Runnable {

		@Override
		public void run() {
			while (running) {
				ServiceHandlerMessage svcMessage = take();
				if (svcMessage != null) {
					ServiceHandler handler = svcMessage.handlerForMessage;
					String message = svcMessage.message;
					// send message to associated service handler
					handler.svc(message);
				} else {
					System.out.print("Error SVC MESSAGE NULL");
				}
			}

		}

	}

}

/**
 * Class for messages sent to blocking queue
 */
class ServiceHandlerMessage {
	ServiceHandler handlerForMessage;
	String message;

	ServiceHandlerMessage(ServiceHandler handler, String message) {
		handlerForMessage = handler;
		this.message = message;
	}
}

/**
 * Abstract EventHandler in Reactor pattern
 */
abstract class EventHandler {
	Reactor registeredReactor;

	abstract int handleEvent();

	abstract int getEventType();

	abstract SelectableChannel getChannel();

	abstract void closeHandler();
}

/**
 * Service_Handler in Acceptor Connector and Reactor pattern reads or write
 * to/from Socket Normally, business logic must be implemented here Wrapper
 * Facade pattern is implemented partly from this classes and partly from java
 * nio library classes
 */
abstract class ServiceHandler extends EventHandler {
	// SocketChannel implements Wrapper Facade
	SocketChannel socketChannel;

	public void setChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	void closeHandler() {
		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	abstract void svc(String message);
}

/**
 * Concrete Service_Handler in Acceptor Connector and Reactor pattern Normally,
 * business logic must be implemented here Reads line and echos back to client i
 * 
 */
class EchoHandler extends ServiceHandler {
	HalfSyncPool threadPool;

	public EchoHandler(Reactor reactor, HalfSyncPool pool) {
		threadPool = pool;
		registeredReactor = reactor;
	}

	// Reads a chunk and puts in HalfSyncPool with pointer to self
	@Override
	public int handleEvent() {
		ByteBuffer buf = ByteBuffer.allocate(4092);
		buf.clear();
		// read message from socket
		try {
			int read = socketChannel.read(buf);
			if (read == -1) {
				return -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		buf.flip();
		// decode message using utf-8
		Charset charset1 = Charset.forName("UTF-8");
		CharsetDecoder decoder1 = charset1.newDecoder();
		CharBuffer charBuffer = null;
		try {
			charBuffer = decoder1.decode(buf);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		System.out.println("Message from Server: " + charBuffer.toString());

		ServiceHandlerMessage svcMessage = new ServiceHandlerMessage(this,
				charBuffer.toString());
		threadPool.enqueue(svcMessage);

		return 0;
	}

	@Override
	int getEventType() {
		return SelectionKey.OP_READ;
	}

	@Override
	SelectableChannel getChannel() {
		return socketChannel;
	}

	// will be called from a thread in threadpool
	// echoing part is done here
	@Override
	void svc(String message) {
		message = message + "\n";
		System.out.println("svc called in new thread");
		// create encoder
		CharsetEncoder enc = Charset.forName("US-ASCII").newEncoder();

		String threadIDText = "Thread id of serving thread is "
				+ Thread.currentThread().getId() + "\n";

		try {
			socketChannel.write(enc.encode(CharBuffer.wrap(threadIDText)));
			socketChannel.write(enc.encode(CharBuffer.wrap(message)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Acceptor in Acceptor/Connector pattern If a client connects to server It
 * creates and registers a EchoHandler Parameterized with type of
 * service_handler that will be created and registered after accept Wrapper
 * Facade pattern is implemented partly from this classes and partly from java
 * nio library classes
 */
class EchoAcceptor<ConcreteEventHandler extends EchoHandler> extends
		EventHandler {
	// ServerSocketChannel implements Wrapper Facade
	ServerSocketChannel serverChannel;
	ConcreteEventHandler serviceHandler;

	public EchoAcceptor(Reactor reactor, int portNumber,
			ConcreteEventHandler serviceHandler) {
		registeredReactor = reactor;
		this.serviceHandler = serviceHandler;
		ServerSocketChannel server;
		try {
			server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(portNumber));
			server.configureBlocking(false);
			serverChannel = server;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int handleEvent() {
		System.out.println("Acceptor handleEvent");
		SocketChannel channel = null;
		int successfull = -1;
		try {
			channel = serverChannel.accept();
			if (channel != null) {
				successfull = 0;
				channel.configureBlocking(false);
				serviceHandler.setChannel(channel);
				registeredReactor.registerEventHandler(serviceHandler,
						serviceHandler.getEventType());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return successfull;
	}

	@Override
	int getEventType() {
		return SelectionKey.OP_ACCEPT;
	}

	@Override
	SelectableChannel getChannel() {
		return serverChannel;
	}

	@Override
	void closeHandler() {
		if (serverChannel != null) {
			try {
				serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

/**
 * Connector part of Acceptor/Connector pattern Not used in this assigment
 * Implemented only for completion of Acceptor/Connector pattern Wrapper Facade
 * pattern is implemented partly from this classes and partly from java nio
 * library classes
 */
class EchoConnector<ConcreteEventHandler extends EventHandler> extends
		EventHandler {
	// SocketChannel implements Wrapper Facade
	SocketChannel channel;
	ConcreteEventHandler serviceHandler;
	int portNumber;

	EchoConnector(Reactor reactor, int portNumber,
			ConcreteEventHandler serviceHandler) {
		registeredReactor = reactor;
		this.serviceHandler = serviceHandler;
		this.portNumber = portNumber;
		try {
			channel = SocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int handleEvent() {
		int connected = -1;
		try {
			if (channel.connect(new InetSocketAddress(portNumber))) {
				connected = 0;
			} else {
				connected = -1;
			}

			channel.configureBlocking(false);
			registeredReactor.registerEventHandler(serviceHandler,
					serviceHandler.getEventType());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connected;
	}

	@Override
	int getEventType() {
		return SelectionKey.OP_CONNECT;
	}

	@Override
	SelectableChannel getChannel() {
		return channel;
	}

	@Override
	void closeHandler() {
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

interface Reactor {
	public void runReactor();

	public void registerEventHandler(EventHandler eventHandler, int eventType);

}

/**
 * Reactor in Reactor pattern Main event loop started with runReactorMethod It
 * uses JAVA NIO functions to select events Also Half-Async part of
 * Half-Sync/Half-Async pattern
 */
class EchoReactor implements Reactor {
	private Selector selector;
	// Map for demultiplexing events to approciate eventhandlers
	private Map<SelectionKey, EventHandler> registeredHandlers = new HashMap<SelectionKey, EventHandler>();

	public EchoReactor() {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runReactor() {
		try {
			// main reactor loop
			while (true) {
				// wait for incoming events
				selector.select();
				Set<SelectionKey> readyHandles = selector.selectedKeys();

				Iterator<SelectionKey> handleIterator = readyHandles.iterator();

				while (handleIterator.hasNext()) {

					SelectionKey handle = handleIterator.next();

					if (handle.isAcceptable()) {
						// System.out.println("Acceptor Selection");
						EventHandler handler = registeredHandlers.get(handle);

						if (handler.handleEvent() == -1) {
							handler.closeHandler();
						}
						handleIterator.remove();

					} else {
						// System.out.println("Service Handler Selection");
						EventHandler handler = registeredHandlers.get(handle);

						if (handler.handleEvent() == -1) {
							handler.closeHandler();
						}

						handleIterator.remove();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void registerEventHandler(EventHandler eventHandler, int eventType) {
		SelectionKey key = null;
		try {
			key = eventHandler.getChannel().register(selector, eventType);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}

		registeredHandlers.put(key, eventHandler);

	}
}