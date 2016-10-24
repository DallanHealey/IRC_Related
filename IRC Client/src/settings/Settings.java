package settings;

import client.Client;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings
{
    static Scanner settingsReader;
    public static ArrayList<String> settings = new ArrayList<String>();
    static JFrame frame;
    static final String appData = System.getenv("APPDATA") + "\\.ircsettings.txt";
    public static BufferedWriter settingsWriter;

    private static void createJFrame()
    {
        frame = new JFrame("Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField username = new JTextField();

        frame.add(usernameLabel).setBounds(0, 0, 75, 75);
        frame.add(username).setBounds(100, 100, 100, 100);
        frame.setVisible(true);
    }

    public static void init() throws IOException
    {
        try
        {
            settingsReader = new Scanner(new File(appData));
        }
        catch(Exception e)
        {
            settingsWriter = new BufferedWriter(new FileWriter(appData));
            settingsWriter.close();
            settingsReader = new Scanner(new File(appData));
        }

        if(!settingsReader.hasNextLine())
        {
            settingsWriter = new BufferedWriter(new FileWriter(appData));
            settingsWriter.write("IP: localhost");

            settings.add("IP");
            settings.add("localhost");

            settingsWriter.close();
        }
    }

    public static void readInSettings() throws FileNotFoundException
    {
        try
        {
            String[] currentLine;

            do
            {
                currentLine = settingsReader.nextLine().split(": ");
                settings.add(currentLine[0]);
                settings.add(currentLine[1]);
            } while(settingsReader.hasNextLine());
        }
        catch(Exception e)
        {
        }

        settingsReader.close();
    }

    public static void setClientSettings()
    {
        if(settings.contains("IP"))
            Client.ip = settings.get(settings.indexOf("IP") + 1);
        if(settings.contains("Username"))
        {
            Client.name = settings.get(settings.indexOf("Username") + 1);
            System.out.println("Name: " + Client.name);
        }
    }

    public static void writeSettings() throws IOException
    {
        // System.out.println(settings.toString());
        if(settings.isEmpty())
            return;

        FileUtils.forceDelete(new File(appData));
        settingsWriter = new BufferedWriter(new FileWriter(appData));

        for(int i = 0; i < settings.size(); i += 2)
        {
            settingsWriter.write(settings.get(i) + ": " + settings.get(i + 1));
            settingsWriter.newLine();
        }

        settingsWriter.close();

    }

    public static void changeSettings(String key, String value)
    {
        if(settings.isEmpty())
            return;
        else if(key.equals("IP"))
        {
            settings.remove(settings.indexOf(key) + 1);
            settings.add(settings.indexOf(key) + 1, value);
        }
        else if(key.equals("Username"))
        {
            if(settings.indexOf("Username") < 0)
                addSetting(key, value);
            else
            {
                settings.remove(settings.indexOf(key) + 1);
                System.out.println("Username1: " + settings.toString());
                settings.add(settings.indexOf(key) + 1, value);
                System.out.println("Username2: " + settings.toString());
            }
        }
    }

    private static void addSetting(String key, String value)
    {
        settings.add(key);
        settings.add(value);
    }
}
