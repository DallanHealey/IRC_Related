import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Enumeration;

public class ServerThread implements Runnable
{
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private String message;
	private String userName;

	ServerThread(Socket socket)
	{
		this.socket = socket;
	}

	public void run()
	{
		try
		{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			Server.outputStreams.put(socket, out);

			// Get user name
			userName = in.readLine();
			System.out.println(userName.toString() + " has connected");

			sendToAll(userName.toString() + " has connected");
			Server.userList.add(userName.toString());
			System.out.println(Server.userList.toString());

			System.out.println("Connected Users: " + Server.userList.toString());
			sendToAll("Connected Users: " + Server.userList.toString());
			System.out.println("Num users: " + Server.clientsConnected);
			while (Server.isRunning)
			{
				message = in.readLine();

				if (message.equals(""))
				{

				}
				else
				{
					System.out.println(message);
					sendToAll(message);
				}
			}

		}
		catch (Exception e)
		{
			System.out.println("An error occured");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				System.out.println(userName + " has disconnected");
				Server.clientsConnected--;
				Server.userList.remove(Server.userList.indexOf(userName.toString()));
				Server.outputStreams.remove(out);
				if (Server.clientsConnected != 0)
				{
					sendToAll(userName + " has disconnected");
					sendToAll("Connected Users: " + Server.userList.toString());
				}

				in.close();
				out.close();
				socket.close();
			}
			catch (Exception e)
			{
				System.out.println("Everything didnt close right");
				e.printStackTrace();
			}
		}
	}

	protected void sendToAll(String message)
	{
		for (Enumeration<OutputStream> e = Server.outputStreams.elements(); e.hasMoreElements();)
		{
			out = (PrintStream) e.nextElement();
			out.println(message);
		}
	}

}
