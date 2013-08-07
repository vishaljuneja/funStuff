import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * example usage:
 * java ServerClient 127.0.0.1 5810 "echo this back"
 */ 

public class SimpleServerClient {

	public static void main(String args[]){
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String message = args[2];
		try
		{
			System.out.println("Establishing connection on port " + port);
			Socket client = new Socket(ip, port);
			
			System.out.println("Connection established " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out =
					new DataOutputStream(outToServer);

			out.writeUTF(message+" 1");
			InputStream inFromServer = client.getInputStream();
			DataInputStream in =
					new DataInputStream(inFromServer);
			System.out.println("Server says " + in.readUTF());
			
			Thread.sleep(1000L);
			
			out.writeUTF(message+" 2");
			System.out.println("Server says " + in.readUTF());
						
			client.close();
			//Thread.sleep(100000L);
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}

