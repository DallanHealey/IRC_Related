import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Hashtable;

import javax.swing.text.Element;

public class LinkListener extends MouseAdapter
{
	static Hashtable<URI, Point> links = new Hashtable<URI, Point>();

	@Override
	public void mouseClicked(MouseEvent e)
	{
		System.out.println("Test");
		try
		{

			Element link = Client.doc.getCharacterElement(Client.messages.viewToModel(e.getPoint()));
			URI urlLink = new URI(Client.command[2]);
			links.put(urlLink, e.getPoint());

			if (Desktop.isDesktopSupported() && links.get(e.getPoint()) == e.getPoint())
			{
				Desktop.getDesktop().browse(urlLink);
			}
			else
			{
				System.out.println("Desktop Not Supported");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
