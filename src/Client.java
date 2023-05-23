import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	static PrintWriter write;
	static BufferedReader read;
	public void go() throws IOException {
		String host="10.210.113.163";
		int port=9000;
		try {
			Socket server=new Socket(host,port);
			write=new PrintWriter(server.getOutputStream(), true);
			read = new BufferedReader(new InputStreamReader(server.getInputStream()));
			Scanner s=new Scanner(System.in);
			(new Thread() {
				  public void run() {
					  while(true) {
						try {
							String s=read.readLine();
							Screen.newMessage(s);
							
						} catch (IOException e) {
							//...
						}
					  }
				  }
				 }).start();
		}catch(IOException e) {
			System.out.println("Server is not Online...");
		}
	}
	public void write(String s) {
		write.println(s);
	}
}
