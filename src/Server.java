import java.net.*;
import java.io.*;

public class Server {
	static MyArrayList<ConnectedClient> clients=new MyArrayList<>();

	public static int n=3;
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
		for(int i=0;i<clients.size();i++) {
			clients.get(i).thread.start();
		}

				
			
		
	}
	static void broadcast(String s,PrintWriter self) {

		for(int i=0;i<clients.size();i++) {
			ConnectedClient client=clients.get(i);
			if(!client.write.equals(self)) client.write.println(s);
		}
	}
}