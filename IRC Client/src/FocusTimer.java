import java.util.TimerTask;

import javax.swing.text.BadLocationException;

public class FocusTimer extends TimerTask
{

	@Override
	public void run()
	{
		// Client.focusDone = true;

		if (Client.unreadLine != 0)
		{
			try
			{
				Client.messages.getDocument().remove(Client.unreadLine, 26);
				Client.isUnreadLine = false;
				Client.unreadLine = 0;
				// Client.focusDone = false;

			}
			catch (BadLocationException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
