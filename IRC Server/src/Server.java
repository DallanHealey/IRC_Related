import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class Server
{
	public static Hashtable<Socket, OutputStream> outputStreams = new Hashtable<Socket, OutputStream>();

	public static boolean isRunning = true;

	public static JTextPane messages;

	public static int clientsConnected = 0;

	protected static ServerSocket SERVERSOCKET;

	protected static List<String> userList = new ArrayList<String>();

	public static void main(String[] args) throws IOException, BadLocationException
	{
		JFrame window = new JFrame("IRC Server");
		window.setSize(400, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		messages = new JTextPane();
		messages.setEditable(false);
		window.add(messages);
		window.setVisible(true);

		SERVERSOCKET = new ServerSocket(444);
		Socket socket;
		System.out.println("Waiting for a client...");
		messages.getDocument().insertString(messages.getDocument().getLength(), "Server started. Waiting for client...\n", null);

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
