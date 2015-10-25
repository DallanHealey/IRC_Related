import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Client
{
	final static int ICON_NORMAL = 0;
	final static int ICON_YELLOW = 1;
	static int iconStatus = ICON_NORMAL;

	static String name;
	static String ip;
	static JFrame frame;
	static boolean isRunning = true;
	static boolean soundOn = true;
	static String[] command;
	static String prevMessage;

	static Timer boopTimer;
	static int timerSpeed = 15000;
	static boolean timerDone = true;

	static Timer iconTimer;

	protected static Style defaultStyle;
	protected static Style linkStyle;
	protected static JTextPane messages;
	protected static StyledDocument doc;

	static Timer focusTimer;
	static int focusSpeed = 3000;
	static boolean focusDone = false;

	static BufferedReader in;
	static PrintStream out;
	static String messageText;
	static Socket socket;

	protected static Hashtable<Integer, URI> links = new Hashtable<Integer, URI>();

	public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException, BadLocationException, URISyntaxException
	{
		AudioClip clip = Applet.newAudioClip(Client.class.getResource("/navi.wav"));
		frame = new JFrame();
		frame.setIconImage(ImageIO.read(Client.class.getResource("/icon.png")));
		ip = getIp();
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
		message.setToolTipText("Type message to send to other users. Press enter to send");
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
				StyleConstants.setBold(defaultStyle, true);

				if (e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					message.setText("");

				}

				if (e.getKeyCode() == KeyEvent.VK_UP)
				{
					message.setText(prevMessage);

				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					prevMessage = message.getText();

					if (message.getText().isEmpty() || message.getText() == null)
					{

					} else if (message.getText().contains("!clear"))
					{
						messages.setText("");
						message.setText("");

					} else if (message.getText().equals("!quit"))
					{

						System.exit(0);
					} else if (message.getText().contains("!sound"))
					{
						command = message.getText().split(" ");
						if (command[1].equals("on"))
						{
							soundOn = true;
							try
							{
								messages.getDocument().insertString(messages.getDocument().getLength(), "Sound is now on\n", defaultStyle);
							} catch (BadLocationException e1)
							{
								e1.printStackTrace();
							}
						} else if (command[1].equals("off"))
						{
							soundOn = false;
							try
							{
								messages.getDocument().insertString(messages.getDocument().getLength(), "Sound is now off\n", defaultStyle);
							} catch (BadLocationException e1)
							{
								e1.printStackTrace();
							}
						} else
						{
							try
							{
								messages.getDocument().insertString(messages.getDocument().getLength(), "Error with command. Try '!sound on' or '!sound off'\n", defaultStyle);
							} catch (BadLocationException e1)
							{
								e1.printStackTrace();
							}
						}
						prevMessage = message.getText();
						message.setText("");
					} else if (message.getText().contains("!timer"))
					{
						command = message.getText().split(" ");
						try
						{
							timerSpeed = Integer.parseInt(command[1]) * 1000;
							messages.getDocument().insertString(messages.getDocument().getLength(), "Timer has been changed to " + timerSpeed / 1000 + " seconds\n", defaultStyle);
							message.setText("");

						} catch (Exception e1)
						{

							System.out.println("Error. Please enter an int");
							try
							{
								messages.getDocument().insertString(messages.getDocument().getLength(), "Error chaning timer. Please enter an intto change time to", defaultStyle);
							} catch (BadLocationException e2)
							{
								e2.printStackTrace();
							}
							e1.printStackTrace();
							message.setText("");
						}
					} else
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
		frame.addWindowFocusListener(new WindowAdapter()
		{
			public void windowGainedFocus(WindowEvent e)
			{
				iconTimer.cancel();
				focusTimer.schedule(new FocusTimer(), focusSpeed);
				try
				{
					frame.setIconImage(ImageIO.read(Client.class.getResource("/icon.png")));
					iconStatus = ICON_NORMAL;
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		boopTimer = new Timer();
		boopTimer.schedule(new BoopTimer(), timerSpeed);

		focusTimer = new Timer();

		// Needs to be last
		frame.setVisible(true);
		message.grabFocus();

		try

		{
			socket = new Socket(ip, 444);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream(), true);
			out.println(name);

			while (Client.isRunning)
			{

				if (vBar.getValue() != vBar.getMaximum())
				{
					vBar.setValue(vBar.getMaximum() + 1);
					messages.setCaretPosition(messages.getDocument().getLength());
				}

				if (!frame.isFocused())
				{
					StyleConstants.setBold(defaultStyle, true);

					if (soundOn && timerDone)
					{
						clip.play();
						timerDone = false;
						boopTimer.schedule(new BoopTimer(), timerSpeed);
					}
					iconTimer = new Timer();
					iconTimer.schedule(new IconTimer(), 0, 1000);
					// frame.setIconImage(ImageIO.read(Client.class.getResource("/icon_yellow.png")));
				}
				messageText = in.readLine();
				System.out.println(messageText);

				if (messageText.contains("Connected Users:"))
				{
					usersConnected.setText("");
					usersConnected.setText(messageText);

				} else if (messageText.contains("!link"))
				{
					command = messageText.split(" ");
					URI uri = new URI(command[2]);
					messages.getDocument().insertString(messages.getDocument().getLength(), command[0] + " ", defaultStyle);
					messages.getDocument().insertString(messages.getDocument().getLength(), uri + "\n", linkStyle);
					System.out.println(messages.getCaretPosition());
					links.put(messages.getCaretPosition(), uri);
				} else
				{
					if (!frame.isFocused())
					{
						StyleConstants.setBold(defaultStyle, true);

						messages.getDocument().insertString(messages.getDocument().getLength(), messageText + "\n", defaultStyle);
						vBar.setValue(vBar.getMaximum() + 1);
						messages.setCaretPosition(messages.getDocument().getLength());
					} else
					{
						StyleConstants.setBold(defaultStyle, false);

						messages.getDocument().insertString(messages.getDocument().getLength(), messageText + "\n", defaultStyle);
						vBar.setValue(vBar.getMaximum() + 1);
						messages.setCaretPosition(messages.getDocument().getLength());

					}
				}
			}

		} catch (

		UnknownHostException e)

		{
			e.printStackTrace();
		} catch (

		IOException e)

		{
			e.printStackTrace();
		} finally

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
		String name = JOptionPane.showInputDialog(frame, message, "Enter a username", JOptionPane.QUESTION_MESSAGE);
		if (name == null || name.isEmpty())
			System.exit(0);
		System.out.println(name);

		return name;
	}

	private static String getIp()
	{
		String ip = JOptionPane.showInputDialog(frame, "Please enter IP", "localhost");
		if (ip == null || ip.isEmpty())
			System.exit(0);
		System.out.println(ip);
		return ip;
	}
}
