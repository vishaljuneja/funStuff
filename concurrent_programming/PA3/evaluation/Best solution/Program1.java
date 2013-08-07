// +
// Several WrapperFacades are not needed in Java because the abstraction is allready
// supplied in the Java environment. Below these facades are listed together with there java counterpart.
//
// The EventDemultiplexor is shielded by the Java WrapperFacade java.nio.channels.Selector
//
// The connection can be closed by typing in the word quit in de beginning of a new line.
//
// To run the program to the following:
// 1.	Download the file Program1.txt
// 2.	Rename it to Program1.java
// 3.	Compile with javac Program1.java
// 4.	Run with java Program1
// 5.	Enjoy!
// 
// -

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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main class is a placeholder of all POSA classes. All classes are inner
 * classes of Program in order to make the distribution and compilation easier.
 * 
 * @author Rasmus Tibell
 */
public class Program1 {
	private int portNumber = 8888;

	public Program1(int port) {
		this.portNumber = port;
	}

	/**
	 * Start point of the program
	 * 
	 * @param args, first parameter is the port number to listen on.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java Program <port number>");
			System.out.println("Example: java Program 8888");
			System.exit(0);
		}
		(new Program1(Integer.parseInt(args[0]))).doWork();
	}

	/**
	 * This method initiates the Echo Server.
	 */
	private void doWork() {
		System.out.println("Program is starting...");
		InitiationDispatcher initDispatcher = new InitiationDispatcher(portNumber);
		(new EchoAcceptHandler(initDispatcher)).open();
		initDispatcher.handle_events();
	}

	// ---
	// Definitions of inner classes
	// ---

	/**
	 * The EchoResponceHandler is listening on events from the underlying layer.
	 * When an event ocures the handle_event method is called from the super
	 * class. The buffer is read, information is displayd on standard output and
	 * echoed to the client.
	 */
	public class EchoResponceHandler extends ResponceHandler {

		public EchoResponceHandler(InitiationDispatcher id) {
			super(id);
		}

		public void handle_event() {
			String inbuff = null;
			while ((inbuff = readSocket()) != null) {
				System.out.println("Read <" + inbuff.trim() + ">");
				if (inbuff.trim().toLowerCase().equals("quit")) {
					close();
					return;
				}
				writeSocket(inbuff);
			}
		}

		public void open() { super.open(InitiationDispatcher.FLG_READ); }
	}

	/**
	 * EchoAcceptHandler reacts on events from the underlying layer. When an
	 * event comes in it creates a new EchoResponceHandler and initiates it.
	 */
	public class EchoAcceptHandler extends AcceptHandler {
		InitiationDispatcher id = null;

		public EchoAcceptHandler(InitiationDispatcher id) {
			super(id);
			this.id = id;
		}

		public void handle_event() {
			ResponceHandler rh = new EchoResponceHandler(id);
			rh.open();
		}
	}

	/**
	 * The ResponceHandler is a Facade that creates an abstraction that shields
	 * the EchoResponceHandler from the Java implementation details. When
	 * initialized it creates a ByteBuffer to store the data read from the
	 * client. Events are dispatched to the subclas via the handle_event method.
	 * Two helper methods handles the reading and writing to the NIO sockets.
	 * These are readSocket and writeSocket respectivly.
	 */
	public abstract class ResponceHandler extends EventHandler {
		public CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
		public CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

		private SocketChannel sc;
		private ByteBuffer buff;

		public ResponceHandler(InitiationDispatcher id) {
			super(id);
			buff = ByteBuffer.allocateDirect(1024);
		}

		public String readSocket() {
			int readSize = 0;
			// String strBuff = null;
			StringBuffer strBuff = new StringBuffer();
			try {
				buff.clear();
				while ((readSize = sc.read(buff)) > 0) {
					buff.flip();
					strBuff.append(decoder.decode(buff.asReadOnlyBuffer()).toString());
					// System.out.println("Read #" + readSize + " from buffer=<"
					// + strBuff.trim() + ">");
					buff.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			if ((readSize > -1) && strBuff.length() > 0) return strBuff.toString();
			else return null;
		}

		public void writeSocket(String msg) {
			try {
				buff = encoder.encode(CharBuffer.wrap(msg));
				while (buff.hasRemaining())
					sc.write(buff);
				buff.clear();
			} catch (CharacterCodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void handle_input(SelectableChannel channel, int type) {
			sc = (SocketChannel) channel;
			handle_event();
		}

		public void close() {
			try {
				sc.close();
			} catch (IOException e) { e.printStackTrace(); }
			super.close();
		}

		public abstract void handle_event();
		public abstract void open();
	}

	/**
	 * Native NIO AcceptHandler. This class handles all the NIO low level stuff. 
	 * It creates a ServerSocketChannel, accepts the connection and registers the 
	 * new peer-peer socket.
	 */
	public abstract class AcceptHandler extends EventHandler {

		public AcceptHandler(InitiationDispatcher id) { super(id); }

		public void handle_input(SelectableChannel channel, int type) {
			// TODO Auto-generated method stub
			try {
				System.out.println("In AcceptHandler method handle_input");
				ServerSocketChannel ssc = (ServerSocketChannel) channel;
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				super.id.registerChannel(sc, SelectionKey.OP_READ);
				handle_event();
			} catch (IOException e) { e.printStackTrace(); }
		}

		public void open() { super.open(InitiationDispatcher.FLG_ACCEPT); }
		public abstract void handle_event();
	}

	/**
	 * The EventHandler is a abstract class handling the registration and removal of
	 * the Handlers.
	 * 
	 */
	public abstract class EventHandler {
		private InitiationDispatcher id = null;

		public EventHandler(InitiationDispatcher id) { this.id = id; }

		public abstract void handle_input(SelectableChannel channel, int type);

		public void open(int mask) {
			System.out.println("Opening EventHandler");
			id.register_handler(this, mask);
		}

		public void close() {
			System.out.println("Closing EventHandler");
			id.remove_handler(this);
		}
	}

	/**
	 * This class is implementing the Reactor pattern together with the base
	 * class EventHandler and its two subclasses ResponceHandler and
	 * AcceptHandler
	 */
	public class InitiationDispatcher {
		public static final int FLG_ACCEPT = 1 << 0;
		public static final int FLG_READ = 1 << 1;
		public static final int FLG_WRITE = 1 << 2;

		private Selector dispSelector = null;
		private ServerSocketChannel srvSockChan = null;
		private ArrayList<Handle> listeners = new ArrayList<Handle>();

		public InitiationDispatcher(int port) {
			try {
				dispSelector = Selector.open();
				srvSockChan = ServerSocketChannel.open();
				srvSockChan.configureBlocking(false);
				srvSockChan.socket().bind(new InetSocketAddress(port));
				srvSockChan.register(dispSelector, SelectionKey.OP_ACCEPT);
			} catch (IOException e) { e.printStackTrace(); }
			System.out.println("Constructing InitiationDispatcher");
		}

		public void registerChannel(SocketChannel chan, int mask) {
			try {
				chan.register(dispSelector, mask);
			} catch (ClosedChannelException e) { e.printStackTrace(); }
		}

		public void handle_events() {
			System.out.println("Starting InitiationDispatcher");
			try {
				while (true) {
					int acceptsInQueue = dispSelector.select();
					Iterator<SelectionKey> iter = dispSelector.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey key = iter.next();
						iter.remove();
						if (key.isValid() && key.isAcceptable())
							for (Handle hand : (ArrayList<Handle>) listeners.clone()) {
								if ((hand.getMask() & FLG_ACCEPT) != 0)
									hand.getHandler().handle_input(key.channel(), FLG_ACCEPT);
							}
						if (key.isValid() && key.isReadable())
							for (Handle hand : (ArrayList<Handle>) listeners.clone()) {
								if ((hand.getMask() & FLG_READ) != 0)
									hand.getHandler().handle_input(key.channel(), FLG_READ);
							}
					}
				}
			} catch (IOException e) { e.printStackTrace(); }
		}

		public synchronized void register_handler(EventHandler handler, int mask) {
			listeners.add(new Handle(mask, handler));
		}

		public synchronized void remove_handler(EventHandler h) {
			for (Handle hand : (ArrayList<Handle>) listeners.clone()) {
				EventHandler fromList = hand.getHandler();
				if (fromList.hashCode() == h.hashCode()) listeners.remove(hand);
			}
		}

		/*
		 * The class Handle groups the EventHandler together with the action
		 * mask.
		 */
		private class Handle {
			private int mask;
			private EventHandler hndl;

			public Handle(int m, EventHandler h) {
				this.mask = m;
				this.hndl = h;
			}

			public int getMask() { return this.mask; }

			public EventHandler getHandler() { return this.hndl; }
		}
	}
}
