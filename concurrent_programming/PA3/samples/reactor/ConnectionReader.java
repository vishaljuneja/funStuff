import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * Handles messages from clients
 */
public class ConnectionReader {
    public static final int BUFFER_SIZE = 256;
    public static final char MESSAGE_END = ';';

    protected SocketChannel _sChannel;
    protected String _incomingData;
    protected ThreadPool _pool;

    /**
     * Creates a new ConnectionReader object
     * @param sChannel the SocketChannel of the client
     * @param pool the ThreadPool to which new Tasks should be inserted
     */
    public ConnectionReader(SocketChannel sChannel, ThreadPool pool) {
        _sChannel = sChannel;
        _pool = pool;
        _incomingData = "";
    }

    /**
     * Reads messages from the client:
     * <UL>
     * <LI>Reads the entire SocketChannel's buffer
     * <LI>Separate the information into messges
     * <LI>For each message:
     * <UL>Creates a Task for the message
     * <LI>Inserts the Task to the ThreadPool
     * </UL>
     * </UL>
     * @throws IOException in case of an IOException during reading
     */
    public void read() throws IOException {

        SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
        System.out.println("Reading from " + address);

        //ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_SIZE);
        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);

        // Read the entire content of the socket
        while (true) {
            buf.clear();
            int numBytesRead = _sChannel.read(buf);

            // Closed channel
            if (numBytesRead == -1) {
                // No more bytes can be read from the channel
                System.out.println("client on " + address + " has disconnected");
                _sChannel.close();
                break;
            }

            // Read the buffer
            if (numBytesRead > 0) {
                //read the data
                buf.flip();
                String str = new String(buf.array(), 0, numBytesRead);
                _incomingData = _incomingData + str;
            }

            //end of message
            if (numBytesRead < BUFFER_SIZE) {
                break;
            }
        }

        // Parse the incoming data into buffer separate messages
        // and handle them
        while (true) {
            int pos = _incomingData.indexOf(MESSAGE_END);

            // No message end mark in the incoming data buffer
            if (pos==-1) {
                break;
            }

            // Extract one message, omit it from the incoming data buffer
            String message = _incomingData.substring(0, pos);
            _incomingData = pos==_incomingData.length()-1 ? "" : _incomingData.substring(pos+1);

            // Do something with the message
            System.out.println("Message " + message + " added to the pool");
            _pool.addTask(new MessageProcessorTask(message, _sChannel));
        }

    }




}
