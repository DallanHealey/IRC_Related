package listeners;

import client.Client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkListener extends MouseAdapter
{

    @Override
    public void mouseClicked(MouseEvent e)
    {
        System.out.println("Mouse coordinates: " + e.getX() + ", " + e.getY() + "\nCaret: " + Client.messages.getCaretPosition());

        try
        {
            if(Desktop.isDesktopSupported())
            {
                if((Client.links.get(Client.messages.getCaretPosition()) != null) && (e.getY() >= Client.messages.getCaretPosition() && e.getY() <= Client.messages.getCaretPosition() + 21))
                {
                    Desktop.getDesktop().browse(Client.links.get(Client.messages.getCaretPosition()));
                }
                else
                {
                    System.out.println("Did bnot wiork");
                }
            }
            else
                System.out.println("Desktop Not Supported");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
