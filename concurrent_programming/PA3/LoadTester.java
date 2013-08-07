import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadTester {
	private int port;
	private String host = "127.0.0.1";
	private List<Socket> sockets = new ArrayList<Socket>();
	
	public LoadTester(int port){
		this.port = port;
	}
	
	public LoadTester(int port, String host){
		this.port = port;
		this.host = host;
	}
	
	public void start() {
		connectionTest();
		connectionLoadTest();
	}

	private void connectionLoadTest() {
		for(int i = 100; i<=10000; i+=1000) {
			connectionLoadTest(i);
		}
	}
	
	private void connectionLoadTest(int n){
		sockets = new ArrayList<Socket>();
		int refused = 0;
		for(int i=0; i<n; i++) {
			try {
				sockets.add(new Socket(host, port));
			} catch (IOException e) {
				refused++;
			}
		}
		System.out.println("connectionLoadTest with n="+n+" refused="+refused);
		
		for(Socket s: sockets) {
			if(!s.isClosed())
				try {
					s.close();
				} catch (IOException e) {}			
		}
	}

	private void connectionTest() {
		try {
			Socket s = new Socket(host, port);
			s.close();
		} catch (IOException e) {
			System.out.println("Test failed: error establishing connection to "+port);
			System.exit(1);
		}
	}
	
	public static void main(String args[]) {
		new LoadTester(4900).start();
	}
}
