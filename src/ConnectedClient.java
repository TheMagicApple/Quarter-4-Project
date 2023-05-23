import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedClient {
	public Socket client;
	public BufferedReader read;
	public PrintWriter write;
	public Thread thread;
	public ConnectedClient(Socket client) throws IOException {
		this.client=client;
		read = new BufferedReader(new InputStreamReader(client.getInputStream()));
		write = new PrintWriter(client.getOutputStream(), true);
		thread=(new Thread() {
			  public void run() {
				  while(true) {
					  try {
						 String s=read.readLine();
						 Server.broadcast(s,write);
					  } catch (Exception e) {
						System.out.println("Client Disconnected.");
						break;
					  }	
				  }
			  }
		});
	}
}
