import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class Server
{
	public static Hashtable<Socket, OutputStream> outputStreams = new Hashtable<Socket, OutputStream>();

	public static boolean isRunning = true;

	public static JTextPane messages;
	public static JScrollBar vBar;
	private static String ipAddress;

	public static int clientsConnected = 0;

	protected static ServerSocket SERVERSOCKET;

	protected static List<String> userList = new ArrayList<String>();

	public static void main(String[] args) throws IOException, BadLocationException
	{
		try
		{
			ipAddress = " - Local IP: " + InetAddress.getLocalHost().getHostAddress();
			System.out.println("Local IP: " + ipAddress);
		} catch (Exception e)
		{
			System.out.println("No Local IP Address");
		}
/*
		JFrame window = new JFrame("IRC Server" + ipAddress);
		window.setSize(450, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		messages = new JTextPane();
		messages.setEditable(false);

		JPanel noWrapPanel = new JPanel(new BorderLayout());
		noWrapPanel.setPreferredSize(new Dimension(175, 200));
		noWrapPanel.add(messages);

		JScrollPane scrollPane = new JScrollPane(noWrapPanel);
		scrollPane.setViewportView(messages);
		vBar = scrollPane.getVerticalScrollBar();
		vBar.setValue(vBar.getMaximum());

		// window.add(messages);
		window.add(scrollPane);
		window.setVisible(true);
*/
		SERVERSOCKET = new ServerSocket(444);
		Socket socket;
		System.out.println("Waiting for a client...");
//		messages.getDocument().insertString(messages.getDocument().getLength(), "Server started. Waiting for client...\n", null);

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
