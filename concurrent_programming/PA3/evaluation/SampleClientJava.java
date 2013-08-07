import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

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

public class SampleClient {
    public static void main(String args[])
    {
        TCPSocket tcpSocket = null;
        try
        {
            System.out.println("Setting up Connection");
            tcpSocket = new TCPSocket("127.0.0.1", 123);
            System.out.println("Sending Message: ");
            tcpSocket.sendLine("Test");
            System.out.println("Server says: ");
            String reply = tcpSocket.receiveLine();
            System.out.println(reply);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        if(tcpSocket != null)
        {
            System.out.println("TCP-connection closing");
            try
            {
                tcpSocket.close();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
}