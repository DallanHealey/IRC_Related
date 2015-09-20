import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.text.Position;

public class LinkListener extends MouseAdapter
{
	Position pos;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		try
		{
			if (Desktop.isDesktopSupported())
			{
				System.out.println("Mouse coords: " + e.getPoint().getY());
				System.out.println("URL coords: " + Client.links.get(Client.messages.getCaretPosition()));

				if ((Client.links.get(Client.messages.getCaretPosition()) != null) || Client.links.get(Client.messages.getCaretPosition()) != null)
				{
					Desktop.getDesktop().browse(Client.links.get(Client.messages.getCaretPosition()));
				}
				else
				{
					System.out.println("No link at that location");
				}
			}
			else
			{
				System.out.println("Desktop Not Supported");
			}

		}
		catch (

		Exception ex)

		{
			ex.printStackTrace();
		}
	}
}
