package client;

import listeners.BoopTimer;
import listeners.FocusTimer;
import listeners.IconTimer;
import listeners.LinkListener;
import settings.Settings;
import update.Update;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Timer;

public class Client
{
    final static String VERSION = "1.1";

    public final static int ICON_NORMAL = 0;
    public final static int ICON_YELLOW = 1;
    public static int iconStatus = ICON_NORMAL;

    public static boolean isUnreadLine = false;
    public static int unreadLine;

    public static String name;
    public static String ip;
    public static JFrame frame;
    public static boolean isRunning = true;

    public static String[] command;
    public static String prevMessage;

    public static boolean soundOn = true;
    static Timer boopTimer;
    public static int timerSpeed = 15000;
    public static boolean timerDone = true;

    public static Timer iconTimer;

    protected static Style defaultStyle;
    protected static Style linkStyle;
    public static JTextPane messages;
    protected static StyledDocument doc;

    public static Timer focusTimer;
    public static int focusSpeed = 15000;
    public static boolean focusDone = false;

    static BufferedReader in;
    static PrintStream out;
    public static String messageText;
    static Socket socket;

    public static Hashtable<Integer, URI> links = new Hashtable<Integer, URI>();

    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException, BadLocationException, URISyntaxException, InterruptedException
    {
        Settings.init();
        Settings.readInSettings();
        Settings.setClientSettings();

        AudioClip clip = Applet.newAudioClip(Client.class.getResource("/navi.wav"));
        frame = new JFrame();
        frame.setIconImage(ImageIO.read(Client.class.getResource("/icon.png")));

        ip = getIP();

        try
        {
            new Update("http://" + ip, VERSION, true, "IRC_Client.jar", "IRC_Client_New.jar");
        }
        catch(Exception e)
        {
            System.out.println("Update server not running");
        }

        name = getUsername("Please enter a username");
        frame.setTitle("IRC Client - " + name);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set Layout
        BorderLayout borderLayout = new BorderLayout();
        frame.setLayout(borderLayout);

        // Components and their properties
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.setPreferredSize(new Dimension(175, 200));

        messages = new JTextPane();
        messages.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        doc = messages.getStyledDocument();
        defaultStyle = messages.addStyle("default", null);
        linkStyle = messages.addStyle("link", defaultStyle);

        StyleConstants.setForeground(linkStyle, Color.BLUE);
        StyleConstants.setUnderline(linkStyle, true);

        noWrapPanel.add(messages);

        messages.setEditable(false);
        messages.setFocusable(false);
        // messages.setLineWrap(true);
        // messages.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.setViewportView(messages);

        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setValue(vBar.getMaximum());
        messages.addMouseListener(new LinkListener());
        JTextField message = new JTextField();
        message.setToolTipText("Type message to send to other users. Press enter to send.");
        message.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                // StyleConstants.setBold(defaultStyle, true);

                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    message.setText("");
                }

                if(e.getKeyCode() == KeyEvent.VK_UP)
                {
                    message.setText(prevMessage);
                }

                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    prevMessage = message.getText();

                    if(message.getText().isEmpty() || message.getText() == null)
                        return;
                    else if(message.getText().contains("!clear"))
                    {
                        messages.setText("");
                        message.setText("");
                    }
                    else if(message.getText().equals("!quit") || message.getText().equals("!q"))
                    {
                        try
                        {
                            Settings.writeSettings();
                        }
                        catch(IOException e1)
                        {
                            e1.printStackTrace();
                        }
                        System.exit(0);
                    }
                    else if(message.getText().equals("!settings"))
                    {
                        // Settings.createJFrame();
                        message.setText("");
                        try
                        {
                            messages.getDocument().insertString(messages.getDocument().getLength(), "Work in Progress\n", defaultStyle);
                        }
                        catch(BadLocationException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                    else if(message.getText().contains("!sound"))
                    {
                        command = message.getText().split(" ");
                        if(command[1].equals("on"))
                        {
                            soundOn = true;
                            try
                            {
                                messages.getDocument().insertString(messages.getDocument().getLength(), "Sound is now on\n", defaultStyle);
                            }
                            catch(BadLocationException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        else if(command[1].equals("off"))
                        {
                            soundOn = false;
                            try
                            {
                                messages.getDocument().insertString(messages.getDocument().getLength(), "Sound is now off\n", defaultStyle);
                            }
                            catch(BadLocationException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        else
                        {
                            try
                            {
                                messages.getDocument().insertString(messages.getDocument().getLength(), "Error with command. Try '!sound on' or '!sound off'\n", defaultStyle);
                            }
                            catch(BadLocationException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        prevMessage = message.getText();
                        message.setText("");
                    }
                    else if(message.getText().contains("!timer"))
                    {
                        command = message.getText().split(" ");
                        try
                        {
                            timerSpeed = Integer.parseInt(command[1]) * 1000;
                            messages.getDocument().insertString(messages.getDocument().getLength(), "Timer has been changed to " + timerSpeed / 1000 + " seconds\n", defaultStyle);
                            message.setText("");
                        }
                        catch(Exception e1)
                        {
                            System.out.println("Error. Please enter an int");
                            try
                            {
                                messages.getDocument().insertString(messages.getDocument().getLength(), "Error chaning timer. Please enter an intto change time to", defaultStyle);
                            }
                            catch(BadLocationException e2)
                            {
                                e2.printStackTrace();
                            }
                            e1.printStackTrace();
                            message.setText("");
                        }
                    }
                    else if(message.getText().contains("!checkForUpdate"))
                    {
                        message.setText("");
                        checkForUpdate();
                    }
                    else
                    {
                        out.println(name + ": " + message.getText());
                        message.setText("");
                    }
                }
                vBar.setValue(vBar.getMaximum() + 1);
                messages.setCaretPosition(messages.getDocument().getLength());
            }
        });

        JTextArea usersConnected = new JTextArea();
        usersConnected.setEditable(false);
        usersConnected.setLineWrap(true);
        usersConnected.setWrapStyleWord(true);
        usersConnected.setBounds(300, 0, 100, 100);
        usersConnected.setFocusable(false);

        // Add components
        frame.add(usersConnected, BorderLayout.EAST);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(message, BorderLayout.SOUTH);
        usersConnected.setAlignmentY(JFrame.CENTER_ALIGNMENT);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    // System.out.println("Writing Settings");
                    Settings.writeSettings();
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                    System.exit(0);
                }
            }
        });
        frame.addWindowFocusListener(new WindowAdapter()
        {
            public void windowGainedFocus(WindowEvent e)
            {
                try
                {
                    iconTimer.cancel();
                }
                catch(Exception e1)
                {
                }

                focusTimer = new Timer();
                focusTimer.schedule(new FocusTimer(), focusSpeed);
                try
                {
                    frame.setIconImage(ImageIO.read(Client.class.getResource("/icon.png")));
                    iconStatus = ICON_NORMAL;
                }
                catch(IOException e1)
                {
                }
            }
        });
        boopTimer = new Timer();
        boopTimer.schedule(new BoopTimer(), timerSpeed);

        try
        {
            connectToServer(ip);

            message.grabFocus();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
            out.println(name);

            while(Client.isRunning)
            {
                if(vBar.getValue() != vBar.getMaximum())
                {
                    vBar.setValue(vBar.getMaximum() + 1);
                    messages.setCaretPosition(messages.getDocument().getLength());
                }

                if(!frame.isFocused())
                {
                    // StyleConstants.setBold(defaultStyle, true);

                    if(soundOn && timerDone)
                    {
                        clip.play();
                        timerDone = false;
                        boopTimer.schedule(new BoopTimer(), timerSpeed);
                    }
                    iconTimer = new Timer();
                    iconTimer.schedule(new IconTimer(), 0, 2000);
                }
                messageText = in.readLine();
                System.out.println(messageText);

                if(messageText.contains("Connected Users:"))
                {
                    usersConnected.setText("");
                    usersConnected.setText(messageText);
                }
                else if(messageText.contains("!link"))
                {
                    // Caret position increases by 21 from end of previous line
                    // to end of current line
                    command = messageText.split(" ");
                    URI uri = new URI(command[2]);
                    messages.getDocument().insertString(messages.getDocument().getLength(), command[0] + " ", defaultStyle);
                    messages.getDocument().insertString(messages.getDocument().getLength(), uri + "\n", linkStyle);
                    links.put(messages.getCaretPosition(), uri);
                    System.out.println("Link caret position: " + messages.getCaretPosition());
                }
                else
                {
                    if(!frame.isFocused())
                    {
                        // StyleConstants.setBold(defaultStyle, true);

						/*
                         * if (!isUnreadLine) { unreadLine =
						 * messages.getDocument().getLength() - 1;
						 * messages.getDocument().insertString(messages.
						 * getDocument().getLength(),
						 * "-------------------------\n", defaultStyle);
						 * isUnreadLine = true; }
						 */

                        messages.getDocument().insertString(messages.getDocument().getLength(), messageText + "\n", defaultStyle);
                        vBar.setValue(vBar.getMaximum() + 1);
                        messages.setCaretPosition(messages.getDocument().getLength());
                    }
                    else
                    {
                        // StyleConstants.setBold(defaultStyle, false);
						/*
						 * if (isUnreadLine) unreadLine += messageText.length()
						 * + 1;
						 */
                        messages.getDocument().insertString(messages.getDocument().getLength(), messageText + "\n", defaultStyle);
                        vBar.setValue(vBar.getMaximum() + 1);
                        messages.setCaretPosition(messages.getDocument().getLength());
                    }
                }
            }
        }
        catch(Exception e)
        {
            while(true)
            {
                int option = JOptionPane.showConfirmDialog(null, "Server is not running or your internet is not connected.\nWould you like to enter a new IP?", "Error", JOptionPane.OK_OPTION);
                if(option == 0)
                {
                    ip = getIP();
                    connectToServer(ip);
                }
                else
                    System.exit(0);
            }
        }
        finally
        {
            in.close();
            out.close();
            socket.close();
            isRunning = false;
            System.exit(0);
        }
    }

    private static String getUsername(String message)
    {
        String name = JOptionPane.showInputDialog(frame, "Enter a username:", Client.name);
        if(name == null || name.isEmpty())
            System.exit(0);
        System.out.println(name);

        System.out.println(Settings.settings.toString());
        Settings.changeSettings("Username", name);
        System.out.println(Settings.settings.toString());
        return name;
    }

    private static String getIP()
    {
        String ip = JOptionPane.showInputDialog(frame, "Please enter IP:", Client.ip);
        if(ip == null || ip.isEmpty())
            System.exit(0);
        System.out.println(ip);

        if(Settings.settings.get(Settings.settings.indexOf("IP") + 1) != ip)
            Settings.changeSettings("IP", ip);

        return ip;
    }

    private static boolean connectToServer(String ip)
    {
        try
        {
            socket = new Socket(ip, 444);
            frame.setVisible(true);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private static boolean checkForUpdate()
    {
        try
        {
            new Update("http://" + ip, VERSION, true, "IRC_Client.jar", "IRC_Client_New.jar");
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Update server not running");
            JOptionPane.showMessageDialog(null, "Failed to fetch update server. Please try again later.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
