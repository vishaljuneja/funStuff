import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * A sample Message Processor
 * The Message Processor holds a received message, and a SocketChannel to the message sender;
 * this socket enables replying back to the sender.
 * This sample processor sends a simple reply back to the message sender, regardless of the message's content.
 * <B>You should either extend or rewrite this Message Processor to work properly with the assignment definition.</B>
 */
class MessageProcessorTask implements Task {
    protected String _message;
    protected SocketChannel _channel;

    /**
     * Creates a new MessageProcessorTask
     * @param message the messge, as receieved from the sender
     * @param channel a channel which will be used to reply to the message sender
     */
    public MessageProcessorTask(String message, SocketChannel channel) {
        _message = message;
        _channel = channel;
    }


    /**
     * Executes the task
     * This simple implementation simply replies the sender with a general reply.
     * @throws TaskFailedException in case of a failure while executing the task 
     */
    public void executeTask() throws TaskFailedException {
        String response = "Got your message!;";
        try {
            _channel.write(ByteBuffer.wrap(response.getBytes()));
        }
        catch (IOException io) {
            throw new TaskFailedException("I/O exception while processing the message: " + _message, io);
        }
    }
}
