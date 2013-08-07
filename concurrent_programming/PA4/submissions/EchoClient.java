package patterns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is a simple client which allows users to send messages to the Echo
 * server which echos is back without any modifications. To run this client use
 * "java EchoClient portNumber" where portNumber is an integer and is the port
 * number on which the Echo server is listening. The port number should be less
 * than 65536.
 * 
 * @author SomeOne
 */
public class EchoClient {
    public static void main(String[] args) throws IOException {
        int port = 8080;

        if (args.length < 1) {
            System.out.println("Usage : java EchoClient portNumber");
            System.out.println("Using 8080 as the port number if available");
        } else {
            try {
                port = Integer.parseInt(args[0]);
                if (port >= 65536)
                    throw new NumberFormatException();
            } catch (NumberFormatException numExcep) {
                System.out.println("Usage : java EchoClient portNumber");
                System.out.println("The portNumber has to be an integer less than 65536");
                System.out.println("Using 8080 as the port number if available");
                port = 8080;
            }
        }

        Socket clientSocket;
        PrintWriter writer;
//        BufferedWriter writerBuf;
        BufferedReader reader;

        try {
            clientSocket = new Socket("localhost", port);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
//            writerBuf = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to open client socket for reading and writing");
            return;
        }

        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        System.out.println("Type \"quit\" without quotes to quit the client program");
        System.out.println("Type your text to be echoed back by the server");

        while ((userInput = userInputReader.readLine()) != null) {
            if (userInput.equalsIgnoreCase("quit"))
                break;

            writer.println(userInput);
//            writerBuf.write(userInput);
//            writerBuf.flush();
            char []readBuffer = new char[userInput.length() * 2 + 10];
            int readLen = reader.read(readBuffer);
            String readString = new String (readBuffer);
//            System.out.println("Server responded with :" + reader.readLine());
            System.out.println("Server responded with " + readLen + " bytes :" + readString);
        }

        writer.close();
//        writerBuf.close();
        reader.close();
        userInputReader.close();
        clientSocket.close();
    }
}
