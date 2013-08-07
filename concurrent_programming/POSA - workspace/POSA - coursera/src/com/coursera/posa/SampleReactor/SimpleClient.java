package com.coursera.posa.SampleReactor;

import java.net.Socket;
import java.io.IOException;

/**
 * A simple client implementation
 */
public class SimpleClient {

    protected String _host;
    protected int _port;
    protected Socket _socket;

    /**
     * Creates a new SimpleClient object
     * @param host the server's host
     * @param port the server's port
     */
    public SimpleClient(String host, int port) {
        _host = host;
        _port = port;
    }

    /**
     * Connects to the server
     * @throws IOException in case of a connection failure
     */
    public void connect() throws IOException {
        _socket = new Socket(_host, _port);
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
    public String receive() throws IOException {
        byte []buff = new byte[8192];
        int nBytes = _socket.getInputStream().read(buff);
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
            SimpleClient client = new SimpleClient(args[0], Integer.parseInt(args[1]));
            client.connect();
            for (int i=0; i<10; i++) {
                String message = "Hey!;";
                System.out.println("Sent \"" + message + "\"");
                client.send(message);

                String res = client.receive();
                System.out.println("Got  \"" + res + "\"");

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException ie) {
                    ie.printStackTrace(System.err);
                }
            }
            client.disconnect();
        } catch (IOException io) {
            io.printStackTrace(System.err);
        }

    }

}
