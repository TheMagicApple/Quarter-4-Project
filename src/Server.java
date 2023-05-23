import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server {
	static ArrayList<ConnectedClient> clients=new ArrayList<>();

	public static int n=2;
	
	public static void main(String[] args) throws IOException {
		int portNumber = 9000;
		ServerSocket serverSocket = new ServerSocket(portNumber);
		for(int i=0;i<n;i++) {
			Socket client = serverSocket.accept();
			System.out.println("Client "+i+" Connected.");
			ConnectedClient c=new ConnectedClient(client);
			c.write.println("Player"+i);
			clients.add(c);
			broadcast("Join"+i,null);
		}
		for(ConnectedClient client:clients) {
			client.thread.start();
		}
	
		
	}
	static void broadcast(String s,PrintWriter self) {
		for(ConnectedClient client:clients) {
			if(!client.write.equals(self)) client.write.println(s);
		}
	}
}