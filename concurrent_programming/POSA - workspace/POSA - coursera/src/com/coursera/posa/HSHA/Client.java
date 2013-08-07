package com.coursera.posa.HSHA;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class Client {

    protected String _host;
    protected int _port;
    protected Socket _socket;
    
    protected static String separator = System.lineSeparator();
    protected static String message = "The selfsame moment I could pray; " + separator +
    		"And from my neck so free " + separator +
    		"The albatross fell off, and sank " + separator +
    		"Like lead into the sea";
    protected static String _EOM = "EOM";

    /**
     * Creates a new SimpleClient object
     * @param host the server's host
     * @param port the server's port
     */
    public Client(String host, int port) {
        _host = host;
        _port = port;
    }

    /**
     * Connects to the server
     * @throws IOException in case of a connection failure
     */
    public void connect() throws IOException {
        _socket = new Socket(_host, _port);
        _socket.setSoTimeout(1000);
    }

    /**
     * Disconnects from the server
     * @throws IOException in the case of a disconnection failure
     */
    public void disconnect() throws IOException {
        _socket.close();
    }

    /**
     * Sends a message to the server
     * @param message the message to send
     * @throws IOException in the case of sending failure
     */
    public void send(String message) throws IOException {
        _socket.getOutputStream().write(message.getBytes());
        _socket.getOutputStream().flush();
    }

    /**
     * Receives information from the server
     * @return the received message, or null of no message received
     * @throws IOException in the case of reception failure
     */
    public String receive() {
        byte []buff = new byte[8192];
        int nBytes = 0;
		try {
			nBytes = _socket.getInputStream().read(buff);
		} catch (SocketTimeoutException e) {
			System.out.println("End of message");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        if (nBytes>0) {
            return new String(buff, 0, nBytes);
        }
        else {
            return null;
        }
    }

    public static void main(String args[]) {

        if (args.length!=2) {
            System.err.println("Usage: java SimpleClient <host> <port>");
            System.exit(1);
        }

        try {
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.connect();
            
            System.out.println("Sent \"" + message + "\"");
            client.send(message);

            while(true) {
            	String res = client.receive();          		
            	
				if(res!=null)
					System.out.println("Got  \"" + res + "\"");
				else break;
            }
            
            

            try {
            	Thread.sleep(500);
            }
            catch (InterruptedException ie) {
            	ie.printStackTrace(System.err);
            }

            client.disconnect();
        } catch (IOException io) {
            io.printStackTrace(System.err);
        }

    }
}
