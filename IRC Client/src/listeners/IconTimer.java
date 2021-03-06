package listeners;

import client.Client;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.TimerTask;

public class IconTimer extends TimerTask
{

    @Override
    public void run()
    {
        try
        {
            if(Client.iconStatus == Client.ICON_NORMAL)
            {
                Client.frame.setIconImage(ImageIO.read(Client.class.getResource("/icon_yellow.png")));
                Client.iconStatus = Client.ICON_YELLOW;
            }
            else
            {
                Client.frame.setIconImage(ImageIO.read(Client.class.getResource("/icon.png")));
                Client.iconStatus = Client.ICON_NORMAL;
            }
        }
        catch(IOException e)
        {
        }

        Client.iconTimer.schedule(new IconTimer(), 2000);
    }
}
