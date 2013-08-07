/***
 * 	The purpose of this assignment is to deepen your understanding of the  Wrapper Facade pattern, 
 *  the Reactor pattern and the (Acceptor role of the)  Acceptor-Connector pattern in the context of Java. In particular, 
 *  you will write a platform-independent reactive server program that accepts a connection from a client
 *  and echoes back what the client sent. 
 *	The reactive server program should do the following activities:
 *	Create an EchoServerHandler so that it echoes back to the client whatever the client sends it. 
 *	When the clients request arrives you need to decide how to handle it in order accept the request and set 
 *  up the connection. 
 *	When data arrives from the client EchoSeverHandler should be called and echo back the client's input either (a) 
 *  a "chunk" at a time or (b) a "line" at a time (i.e., until the symbols "\n", "\r", or "\r\n" are read), 
 *  rather than a character at a time.
 *	For this assignment, you can simply exit the server process when you're done, which will close down all the open sockets. 
 *	Make sure your solution clearly indicates which classes play which roles in the Wrapper Façade, Reactor, 
 *  and/or Acceptor-Connector patterns.
 */
	import java.io.*;
import java.net.*;
	 
	public class Main
	{
		public static void main(String args[])
		{
			Socket socket = null;
			int port_number = Integer.parseInt(args.length == 0 ? "4000" : args[0]);
			
			try	{
				// open a socket connection
				new Server( port_number );			
				socket = new Socket("127.0.0.1", port_number);
	
				// instantiate a wrapper to handle IO channel
				Wrapper_facade iostream = new Wrapper_facade(socket);				
				
				String message = iostream.getInputStream().readLine();
				System.out.print("Message received : " + message);
				
				iostream.closeStreams();
			}
			catch(Exception e) { System.out.println("exc1: " + e.getMessage() + " ");}
		}
	}
	
/** Patterns implementation */	
	
	// Separating IO concerns
	class Wrapper_facade
	{
		BufferedReader in = null;
		PrintStream out = null;
		public Wrapper_facade(Socket socket) throws IOException {
				in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
				out = new PrintStream(socket.getOutputStream(), true);
		}
	
		public BufferedReader getInputStream() {
			return in;
		}
		
		public PrintStream getOutputStream() {
			return out;
		}
		
		public void closeStreams() throws IOException {
			in.close();
			out.close();
		}
	}
	
	class Acceptor {
		private Socket connectionAccepted;
		
		public Acceptor(ServerSocket server) throws IOException {
			System.out.println("MsgAcceptor: Waiting for a connection...");
			connectionAccepted = server.accept();
			System.out.println("MsgAcceptor: Connection accepted from: "+ connectionAccepted.getInetAddress());
		}
		
		public Socket getConnection(){
			return connectionAccepted;
		}
		
	}
	
	
	class Connector {
		private Client client;
		public Connector(Socket connection) throws IOException {
			client = new Client(connection);
			System.out.println("MsgConnector: connection established");
		}
		
		public Client getClient() {
			return client;
		}
	}
	
/** Client/Server models */	
	class Server extends Thread
	{
		private ServerSocket Server;
		private Acceptor acc = null;
		private Socket client = null;
		private Connector ServiceHandler = null;
		
		// Starts the server
		public Server(int port_number) throws Exception
		{
			Server = new ServerSocket(port_number);
			System.out.println("Server is listening on port 4000.");
			this.start();
		}
		
		public void run()
		{
			while(true)
			{
				try {
					acc = new Acceptor(Server);
					client = acc.getConnection();
					ServiceHandler = new Connector(client);
					System.out.println("MsgServer: ServiceHandler intialized, id: " + ServiceHandler.getClient().getId());
				}
				catch(Exception e) {}
			}
		}
	}
	 
	class Client extends Thread
	{
		private Socket client = null;
		private BufferedReader in = null;
		private PrintStream out = null;
		public Client() {}
		
		public Client(Socket clientSocket) {
			client = clientSocket;
			
			try {
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintStream(client.getOutputStream(), true);
			} catch(Exception e1) {
				try { client.close(); } 
				catch(Exception e) { System.out.println("exc: " + e.getMessage());}
				return;
			}
			
			this.start();
		}
		
		public void close(PrintStream out, BufferedReader in, Socket client) {
			try {
				out.flush();
				out.close();
				in.close();
				client.close();
			} catch(Exception e) { System.out.println("exc: " + e.getMessage());}
		}
		
		public void run() {
			try {
				out.println("Message from Client! Echo succeded");
				close(out,in,client);
			} catch(Exception e) {}
		}
	}