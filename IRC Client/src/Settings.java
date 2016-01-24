import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Settings
{
	public static PrintWriter settingsWriter;
	static Scanner settingsReader;
	public static Hashtable<String, String> settings;
	static JFrame frame;
	static final String appData = System.getenv("APPDATA") + "\\.ircsettings.txt";

	public static void main(String[] args) throws FileNotFoundException
	{
		settingsWriter = new PrintWriter(new File(appData + "/ircsettings.txt"));
		settingsWriter.write("IP: localhost\n");
		settingsWriter.close();
		// readInSettings();
		// setSettings();
		// createJFrame();
	}

	private static void createJFrame()
	{
		frame = new JFrame("Settings");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 400);
		JButton button = new JButton("Test");
		frame.add(button);
		frame.pack();
		frame.setVisible(true);
	}

	public static void readInSettings() throws FileNotFoundException
	{
		try
		{
			String[] currentLine;

			settingsReader = new Scanner(new File(appData + "/ircsettings.txt"));

			do
			{
				currentLine = settingsReader.nextLine().split(": ");
				System.out.println(currentLine[0]);
				settings.put(currentLine[0], currentLine[1]);

			} while (settingsReader.hasNextLine());
		}
		catch (Exception e)
		{
		}

		settingsReader.close();
	}

	public static void setSettings()
	{
		if (settings.containsKey("IP"))
			Client.ip = settings.get("IP");
		else
			System.out.println("No values");
	}
}
