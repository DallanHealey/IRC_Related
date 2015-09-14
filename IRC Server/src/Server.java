import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Server
{
	public static Hashtable<Socket, OutputStream> outputStreams = new Hashtable<Socket, OutputStream>();

	public static boolean isRunning = true;

	public static int clientsConnected = 0;

	protected static ServerSocket SERVERSOCKET;

	protected static List<String> userList = new ArrayList<String>();

	public static void main(String[] args) throws IOException
	{
		SERVERSOCKET = new ServerSocket(444);
		Socket socket;
		System.out.println("Waiting for a client...");

		while (isRunning)
		{
			socket = SERVERSOCKET.accept();
			ServerThread server = new ServerThread(socket);
			Thread t = new Thread(server);
			t.start();
			clientsConnected++;

		}
		Server.isRunning = false;
		SERVERSOCKET.close();

	}

}
