package com.coursera.posa.SampleReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * An implementation of the Reactor pattern
 */
public class Reactor extends Thread {

    protected int _port;
    protected int _poolSize;
    protected ThreadPool _pool;
    protected volatile boolean _shouldRun = true;

    /**
     * Creates a new Reactor
     * @param poolSize the number of WorkerThreads to include in the ThreadPool
     * @param port the port to bind the Reactor to
     * @throws IOException if some I/O problems arise during connection
     */
    public Reactor(int poolSize, int port) throws IOException {
        _port = port;
        _poolSize = poolSize;
    }

    /**
     * Main operation of the Reactor:
     * <UL>
     * <LI>Uses the <CODE>Selector.select()</CODE> method to find new requests from clients
     * <LI>For each request in the selection set:
     * <UL>If it is <B>acceptable</B>, use the ConnectionAcceptor to accept it,
     * create a new ConnectionReader for it register it to the Selector
     * <LI>If it is <B>readable</B>, use the ConnectionReader to read it,
     * extract messages and insert them to the ThreadPool
     * </UL>
     */
    public void run() {
        try {
            // Create & start the ThreadPool
            _pool = new ThreadPool(_poolSize);
            _pool.startPool();

            // Create a non-blocking server socket channel and bind to to the Reactor port
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(_port));

            // Create the selector and bind the server socket to it
            Selector selector = Selector.open();
            ssChannel.register(selector, SelectionKey.OP_ACCEPT, new ConnectionAcceptor(selector, ssChannel, _pool));

            while (_shouldRun) {
                // Wait for an event
                selector.select();

                // Get list of selection keys with pending events
                Iterator it = selector.selectedKeys().iterator();

                // Process each key
                while (it.hasNext()) {
                    // Get the selection key
                    SelectionKey selKey = (SelectionKey)it.next();

                    // Remove it from the list to indicate that it is being processed
                    it.remove();

                    // Check if it's a connection request
                    if (selKey.isValid() && selKey.isAcceptable()) {
                        ConnectionAcceptor connectionAcceptor = (ConnectionAcceptor)selKey.attachment();
                        connectionAcceptor.accept();
                    }
                    // Check if a message has been sent
                    if (selKey.isValid() && selKey.isReadable()) {
                        ConnectionReader connectionReader = (ConnectionReader)selKey.attachment();
                        connectionReader.read();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            stopReactor();
        }
        stopReactor();
    }

    /**
     * Returns the listening port of the Reactor
     * @return the listening port of the Reactor
     */
    public int getPort() {
        return _port;
    }

    /**
     * Stops the Reactor activity, including the Reactor thread and the Worker
     * Threads in the Thread Pool.
     */
    public void stopReactor(){
        _shouldRun = false;
        _pool.stopPool();
    }

    public static void main(String args[]) {
    	if (args.length!=2) {
    		System.err.println("Usage: java Reactor <thread pool size> <port>");
    		System.exit(1);
    	}
    	
        try {
            Reactor reactor = new Reactor(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            reactor.start();
            System.out.println("Reactor is ready on port " + reactor.getPort());
            reactor.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
