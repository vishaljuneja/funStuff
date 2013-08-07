import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Credits!! (Classes with their roles):
 * 
 * ServerFacade -> Abstracts the internal functioning of the main Reactor; makes a clients life easier!
 * Reactor -> main "Reactor/Synchronous event demultiplexer/Dispatcher". Dispatches client requests to various handlers.
 * EventHandler -> General event handler interface
 * EchoEventHandler -> A concrete event handler
 * ConnectionHandler -> A connection acceptor which blocks on a port
 */


/**
 * example usage:
 * java Server 5810
 */
public class Server {

	public static void main(String args[]){
		if(args.length == 0) {
			System.out.println("Incorrect usage: port number not specified");
			System.exit(0);
		}
		Integer port = Integer.parseInt(args[0]);
		
		//instantiate facade to awaken server
		new Server().new ServerFacade(port);
	}

	
	/** 
	 * A facade which hides/abstracts the internal functioning of the Reactor
	 */
	public class ServerFacade {
		
		public ServerFacade(Integer port){
			try {
				new Reactor(port);
			} catch (IOException e) {
				System.out.println("Error connecting to the port number "+port);
				System.out.println("Please try another port...");
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * This is main "Reactor/Synchronous event demultiplexer/Dispatcher"
	 * It maintains list of active server connection and dispatches requests to appropriate service/event handlers
	 */
	private class Reactor {
		private ConnectionHandler acceptor;
		private List<Socket> activeConnections = new ArrayList<Socket>();
		
		private EventHandler handler1;	//usually this will be a list for different event handler services

		public Reactor(Integer port) throws IOException {
			//initialize handlers
			handler1 = new EchoEventHandler(this);
			
			//start accepting client connections
			acceptor = new ConnectionHandler(port, this);
			acceptor.start();
		}
		
		public void add(Socket socket) {
			this.activeConnections.add(socket);
			
			// ask an EventHandler to handle this request
			handler1.handle(socket);
		}

		public void remove(Socket socket) {
			activeConnections.remove(socket);
		}
	}
	
	
	
	/**
	 * Connection acceptor class which runs in a separate thread
	 * Listens on a port and sends socket to a reactor/dispatcher once connection is established
	 */
	private class ConnectionHandler extends Thread {

		private ServerSocket serverSocket;
		private Reactor reactor;
		
		public ConnectionHandler(Integer port, Reactor r) throws IOException {
			serverSocket = new ServerSocket(port);
			this.reactor = r;
		}
		
		public void run(){
			while(true){
				try{
					System.out.println("Listening on port ... " + serverSocket.getLocalPort());
					Socket socket = serverSocket.accept();	//blocks on a port to hear incoming requests
					
					System.out.println("Connection request received from "+socket.getRemoteSocketAddress());
					reactor.add(socket);
				} catch (IOException e) {
					System.out.println("Error listening to port "+serverSocket.getLocalPort());
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	
	/**
	 * An Concrete Event Handler which echoes back client request
	 */
	private class EchoEventHandler implements EventHandler {

		private Reactor reactor;
		
		public EchoEventHandler(Reactor r) {
			this.reactor = r;
		}

		@Override
		public void handle(Socket socket) {
			new anEventThread(socket).run();
			// remove from reactors list once request has been served
			reactor.remove(socket);
		}
		
		/**
		 * Spawns a new thread for each new service request
		 */
		private class anEventThread extends Thread {
			private Socket socket;
			
			public anEventThread(Socket socket) {
				this.socket = socket;
			}

			public void run(){
				try {
					DataInputStream in =
							new DataInputStream(socket.getInputStream());
					String message = in.readUTF();
					System.out.println("Client says: "+ message);
					DataOutputStream out =
							new DataOutputStream(socket.getOutputStream());
					out.writeUTF(message);	//echo back the same message
					socket.close();
				} catch(IOException e){
					System.out.println("Error handling service ...");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * General event handler interface
	 */
	private interface EventHandler {
		public void handle(Socket socket);		
	}
	
	
}
