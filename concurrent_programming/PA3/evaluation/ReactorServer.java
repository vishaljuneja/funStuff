import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

interface EventHandler
{
    void handleInput() throws IOException;
    ServerSocket getHandler();
}

interface SynchronousEventDemultiplexer
{
    List<ServerSocket> select(Collection<ServerSocket> listeners);
}

interface Reactor
{
    void handleEvents() throws IOException;
    void registerHandler(EventHandler handler);
    void removeHandler(EventHandler handler);
}

class TCPSocket  //Wrapper for Socket
{
    private Socket socket;
    private BufferedReader istream;
    private BufferedWriter ostream;
    public TCPSocket(String serverAddress, int serverPort)
            throws UnknownHostException, IOException
    {
        socket = new Socket(serverAddress, serverPort);
        initializeStreams();
    }
    public TCPSocket(Socket socket) throws IOException
    {
        this.socket = socket;
        initializeStreams();
    }
    public void sendLine(String s) throws IOException
    {
        ostream.write(s);
        ostream.newLine();
        ostream.flush();
    }
    public String receiveLine() throws IOException
    {
        return istream.readLine();
		//return istream.read();
    }
    public void close() throws IOException
    {
        socket.close();
    }
    private void initializeStreams() throws IOException
    {
        ostream = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        istream = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }
}

class EchoServiceHandler implements EventHandler
{
    private ServerSocket serverSocket;
    private TCPSocket tcpSocket;
    private String ip;
    int port;

    public EchoServiceHandler(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        serverSocket=new ServerSocket(port);
        System.out.println("Server running...");
        System.out.println("Waiting for clients...");
    }


    public void handleInput() throws IOException {
        tcpSocket=new TCPSocket(serverSocket.accept());

        while(true) {
            try {
                String receivedMessage = getData(tcpSocket);
                if (receivedMessage != "")
                    echoData(receivedMessage, tcpSocket);
                else {
                    System.out.println("Closing connection");
                    tcpSocket.close();
                    break;
                }
            } catch (Exception ex) {
                System.out.println("Error...");
                break;
            }
        }
    }

    private void echoData(String receivedMessage, TCPSocket socket) throws IOException {
        System.out.println("Echoing back: " + receivedMessage);
        try {
            if(receivedMessage!="")
                socket.sendLine(receivedMessage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getData(TCPSocket tcpSocket) throws IOException {
        String received = tcpSocket.receiveLine();
        if(received!=null)
        {
            System.out.println("Received from Client: " + received);
            return received;
        }
        return "";
    }

    public ServerSocket getHandler() {
        return serverSocket;
    }
}

class SynchronousEventDemultiplexerClass implements  SynchronousEventDemultiplexer
{
    public List<ServerSocket> select(Collection<ServerSocket> listeners) {
        List<ServerSocket> list = new LinkedList<ServerSocket>();
        for (ServerSocket socket : listeners) {

            if (socket!=null) {
                list.add(socket);
            }
        }
        return list;
    }
}

class ReactorClass implements Reactor
{
    private SynchronousEventDemultiplexer evtDemultiplexer;
    private Map<ServerSocket,EventHandler> handlers;

    public ReactorClass(SynchronousEventDemultiplexer evtDemultiplexer)
    {
       this.evtDemultiplexer=evtDemultiplexer;
       handlers = new HashMap<ServerSocket, EventHandler>() ;
    }

    public void handleEvents() throws IOException {
        while(true)
        {
            List<ServerSocket> listeners = evtDemultiplexer.select(handlers.keySet());
            for(ServerSocket listener : listeners)
            {
                  handlers.get(listener).handleInput();
            }
        }
    }


    public void registerHandler(EventHandler handler) {
        handlers.put(handler.getHandler(),handler);
    }


    public void removeHandler(EventHandler handler) {
        handlers.remove(handler.getHandler());
    }
}
public class ReactorServer {
    public static void main(final String[] args) throws IOException {
        EchoServiceHandler client = new EchoServiceHandler("127.0.0.1", 123);
        SynchronousEventDemultiplexer sync = new SynchronousEventDemultiplexerClass() ;
        Reactor dispatcher = new ReactorClass(sync);

        dispatcher.registerHandler(client);
        dispatcher.handleEvents();
    }
}
