package listeners;

import client.Client;

import javax.swing.text.BadLocationException;
import java.util.TimerTask;

public class FocusTimer extends TimerTask
{

    @Override
    public void run()
    {
        // Client.focusDone = true;

        if(Client.isUnreadLine && Client.unreadLine != 0)
        {
            try
            {
                Client.messages.getDocument().remove(Client.unreadLine, 26);
                Client.isUnreadLine = false;
                Client.unreadLine = 0;
                // Client.focusDone = false;

            }
            catch(BadLocationException e1)
            {
            }
        }
    }
}
