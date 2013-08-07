
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wojciech
 */
public class SimpleClient {
    public static void main(String[] args) throws UnknownHostException, IOException {
        int port = 13300;
        if (args.length > 0) {
            port = Integer.parseInt(args[1]);
        }
        Socket client = new Socket("localhost", port);
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(output));
        DataInputStream input = new DataInputStream(client.getInputStream());
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        byte[] buffer = new byte[1000];
        while(true)
        {
            String message = consoleReader.readLine();
            System.out.println("READ INPUT: " + message);
            outputWriter.write(message);
            outputWriter.newLine();
            outputWriter.flush();
            int bytesRead = input.read(buffer);
            System.out.print("SERVER REPLY: ");
            System.out.println(new String(buffer, 0, bytesRead));
        }
    }
}
