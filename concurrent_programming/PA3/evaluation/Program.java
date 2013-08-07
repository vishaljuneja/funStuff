

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class that starts a server that echoes back the information sent by the clients
 * It uses a reactor to register and dispatch handlers,
 * and a wrapper facade that hides the details of the connection method
 *
 */
public class Program{

	/**
	 * @param args
	 * args[0] the number of the port to be listened
	 */
	public static void main(String[] args) {
		int port = 0;
		try{
			port = Integer.parseInt(args[0]);
		}catch(Exception e){
			System.err.println("Proper syntax is 'java Program [port_number]'");
			return;
		}

		try{
			// The wrapper facade to manage Socket channels
			ServiceFacade serviceFacade = SocketChannelServiceFacade.getInstance(port);
			// the reactor engine
			ReactorInner reactor = new ReactorInner(serviceFacade);
			// The acceptor that will accept a connection though the given wrapper facade		
			Acceptor acceptor = new ServiceAcceptor(serviceFacade);
			// An event handler that echoes back the messages received
			EventHandler eventHandler = new EchoServerHandler();
			//registering the acceptor in the reactor
			reactor.registerAcceptor(acceptor);
			// registering the Echo Server handler
			reactor.registerEventHandler(EventType.READ_MASK, eventHandler);
			//starting the reactor
			reactor.start();
			System.out.println("listening on port " + port);
			reactor.join();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("DONE");
	}


}

/**
 * Class that uses the reactor pattern to register and dispact handlers.
 * It uses a wrapper facade that hides the details of the connection  
 * It extend the Thread class to run in a separate thread 
 *
 */
class ReactorInner extends Thread{
	private final ServiceFacade serviceFacade;

	public ReactorInner(ServiceFacade serviceFacade){
		this.serviceFacade = serviceFacade;
	}

	public void registerAcceptor(Acceptor acceptor){
		serviceFacade.registerAcceptor(acceptor);
	}

	public void registerEventHandler(EventType eventType, EventHandler eventHandler){
		serviceFacade.registerEventHandler(eventType, eventHandler);
	}
	
	/**
	 * Method listen for events and dispatch the proper handler for it
	 */
	public void run(){
		System.out.println("ReactorInner started");
		ServiceHandler serviceHandler = null;
		while (true) {
			List<ListenEvent> events = serviceFacade.listen();
			for(ListenEvent event : events){
				switch(event.getEventType()){
				case ACCEPT_MASK:
					Acceptor acceptor = (Acceptor) event.getHandler();
					serviceHandler = acceptor.accept();
					break;
				case READ_MASK:
				case WRITE_MASK:
					if(serviceHandler != null){
						EventHandler eventHandler = (EventHandler) event.getHandler();
						eventHandler.handle(serviceHandler);
					}
					break;
				}
			}
		}
	}
}


/**
 * The event types that can be supported by this program
 */
enum EventType{
	ACCEPT_MASK,
	READ_MASK,
	WRITE_MASK;
}


/**
 * A container to associate an event type and a handler object
 *
 */
class ListenEvent{
	private final Object handler;
	private final EventType eventType;
	public ListenEvent(Object handler, EventType eventType) {
		this.handler = handler;
		this.eventType = eventType;
	}
	public Object getHandler() {
		return handler;
	}
	public EventType getEventType() {
		return eventType;
	}

}

/*
 * 
 * INTERFACES
 * 
 * 
 */

/**
 * The Facade interface with methods to handle generic remote connections technologies using event handlers.
 * @author anselmo
 *
 */
interface ServiceFacade{

	/**
	 * It registers an acceptor for a remote connection
	 * @param acceptor
	 */
	public void registerAcceptor(Acceptor acceptor);

	/**
	 * It register an event Handler and the event type that should trigger it
	 * @param eventType
	 * @param eventHandler
	 */
	public void registerEventHandler(EventType eventType, EventHandler eventHandler);

	/**
	 * Accepts a connection
	 * @return
	 */
	public ServiceHandler accept();

	/**
	 * Listen for events, it returns the list of events received and the handlers for them
	 * @return
	 */
	public List<ListenEvent> listen();
}


/**
 * Interface for an acceptor that accept a remote connection 
 * and provide a {@link ServiceHandler} implementation to handle it
 */
interface Acceptor{
	public ServiceHandler accept();
}

/**
 * Interface to handle a remote connection
 */
interface ServiceHandler{
	public String getRemoteAddress();

	public int read (ByteBuffer buf) throws IOException;


	public int write(ByteBuffer buf) throws IOException;


	public void close() throws IOException;
}

/**
 * Interface to handle a generic event, provided a {@link ServiceHandler}
 */
interface EventHandler{
	public void handle(ServiceHandler serviceHandler);
}


/*
 * 
 * IMPLEMENTATIONS
 * 
 * 
 */


/**
 * The facade Implementation that uses java.nio.*, in particular Socket channels, to handle remote connections.
 * NOTE: java.nio uses the reactor pattern on his side as well.
 */
class SocketChannelServiceFacade implements ServiceFacade{
	private Selector selector;
	ServerSocketChannel ssChannel;
	Map<Integer,EventHandler> eventHandlers;
	private SocketChannelServiceFacade(){/*..*/}

	static public SocketChannelServiceFacade getInstance(int port){
		SocketChannelServiceFacade serviceFacade = new SocketChannelServiceFacade();
		serviceFacade.init(port);
		return serviceFacade;
	}

	public void init(int port){
		// Create the selector and bind the server socket to it
		try{
			ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(false);
			ssChannel.socket().bind(new InetSocketAddress(port));
			selector = Selector.open();
		}catch(Exception e){

		}
		eventHandlers = new HashMap<Integer,EventHandler>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerAcceptor(Acceptor acceptor){
		
		try{
			ssChannel.register(selector, SelectionKey.OP_ACCEPT, acceptor);
		}catch(Exception e){

		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerEventHandler(EventType eventType, EventHandler eventHandler){
		int selectionKey = 0;
		switch(eventType){
		case READ_MASK:
			selectionKey = SelectionKey.OP_READ;
			break;
		case WRITE_MASK:
			selectionKey = SelectionKey.OP_WRITE;
			break;
		default:
			throw new IllegalArgumentException("EventType " + eventType + " not supported");
		}

		eventHandlers.put(selectionKey, eventHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	public SocketServerServiceHandler accept(){
		SocketServerServiceHandler serviceHandler = null;
		try{
			SocketChannel socketChannel = ssChannel.accept();

			if (socketChannel != null) {
				SocketAddress address = socketChannel.socket().getRemoteSocketAddress();
				System.out.println("Accepting connection from " + address);
				socketChannel.configureBlocking(false);

				for(Map.Entry<Integer,EventHandler> entry  : eventHandlers.entrySet()){
					socketChannel.register(selector, entry.getKey(), entry.getValue());
				}

			}

			serviceHandler = new SocketServerServiceHandler(socketChannel);
		}catch(Exception e){

		}
		return serviceHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ListenEvent> listen(){
		List<ListenEvent> events = new ArrayList<ListenEvent>();

		try{
			// Wait for an event
			selector.select();

			// Get list of selection keys with pending events
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			events = new ArrayList<ListenEvent>();
			// Process each key
			while (it.hasNext()) {

				// Get the selection key
				SelectionKey selKey = it.next();

				// Remove it from the list to indicate that it is being processed
				it.remove();

				// Check if it's a connection request
				if (selKey.isValid() && selKey.isAcceptable()) {
					System.out.println("acceptable event found ");
					events.add(new ListenEvent(selKey.attachment(), EventType.ACCEPT_MASK));
				}
				// Check if a message has been sent
				if (selKey.isValid() && selKey.isReadable()) {
					System.out.println(" Readable event found ");
					events.add(new ListenEvent(selKey.attachment(), EventType.READ_MASK));
				}
				// Check if a message has been written (???)
				if (selKey.isValid() && selKey.isWritable()) {
					System.out.println("writable event found ");
					events.add(new ListenEvent(selKey.attachment(), EventType.WRITE_MASK));
				}
			}
		}catch(Exception e){

		}
		return events;
	}
}



/**
 * Implementation of Acceptor that uses the ServiceWrapper to accept a connection
 * It is probably not a neat design, but I wanted to keep all the java.nio references well  isolated in the wrapper
 *
 */
class ServiceAcceptor implements Acceptor{

	private final ServiceFacade serviceFacade;

	public ServiceAcceptor(ServiceFacade serviceFacade){
		
		this.serviceFacade = serviceFacade;
	}

	public ServiceHandler accept(){

		return serviceFacade.accept();
	}

}

/**
 * Implementation of ServiceHandle that uses java.nio socketChannel to handle a remote connection
 *
 */
class SocketServerServiceHandler implements ServiceHandler{
	private SocketChannel socketChannel;

	SocketServerServiceHandler(SocketChannel socketChannel){
		this.socketChannel = socketChannel;

	}

	public String getRemoteAddress(){
		return socketChannel.socket().getRemoteSocketAddress().toString();
	}

	public int read(ByteBuffer buf) throws IOException{
		return socketChannel.read(buf);
	}


	public int write(ByteBuffer buf) throws IOException{
		return socketChannel.write(buf);
	}


	public void close() throws IOException{
		if(socketChannel.isOpen()){
			socketChannel.close();
		}
		System.out.println("Connection closed ");
	}
}

/**
 * implementation of {@link EventHandler} that echoes back messages received from the remote connection
 *
 */
class EchoServerHandler  implements EventHandler{


	public static final int BUFFER_SIZE = 256;
	public static final String LINE_ENDERS = "\n|\r|\0";
	
	/**
	 * Read a message and echoes back any single line contained
	 */
	public void handle(ServiceHandler serviceHandler){

		String message = readMessage(serviceHandler);
		System.out.println("Message " + message + " received");

		String[] lines = message.split(LINE_ENDERS);
		for(String line : lines){
			writeMessage(serviceHandler, line);
			System.out.println("line " + line + " echoed back");
		}
		try{
		serviceHandler.close();
		}catch(IOException ioEx){
			
		}
	}

	/**
	 * Uses the serviceHandler to read a message from a remote connection
	 * @param serviceHandler
	 * @return
	 */
	private String readMessage(ServiceHandler serviceHandler){
		String incomingData = "";
		String address = serviceHandler.getRemoteAddress();
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		// Read the entire content of the socket
		while (true) {
			try{
				buf.clear();
				int numBytesRead = serviceHandler.read(buf);
				// Closed channel
				if (numBytesRead == -1) {
					// No more bytes can be read from the channel
					System.out.println("client on " + address + " has disconnected");
					serviceHandler.close();
					break;
				}
				// Read the buffer
				if (numBytesRead > 0) {
					//read the data
					buf.flip();
					String str = new String(buf.array(), 0, numBytesRead);
					incomingData = incomingData + str;
				}
				//end of message
				if (numBytesRead < BUFFER_SIZE) {
					break;
				}
			}catch(IOException ioEx){

			}

		}
		return incomingData;
	}

	/**
	 * 
	 * Uses the serviceHandler to write a message to a remote connection
	 * @param serviceHandler
	 * @param message
	 */
	private void writeMessage(ServiceHandler serviceHandler, String message){
		String incomingData = "";
		String address = serviceHandler.getRemoteAddress();		
		ByteBuffer buf = ByteBuffer.wrap(message.getBytes());
		while (true) {
					
			try{
				int numBytesRead = serviceHandler.write(buf);
				// Closed channel
				if (numBytesRead == -1) {
					// No more bytes can be read from the channel
					System.out.println("client on " + address + " has disconnected");
					serviceHandler.close();
					break;
				}
				// Read the buffer
				if (numBytesRead == 0) {
					break;
				}

			}catch(IOException ioEx){

			}

		}
	}

}
