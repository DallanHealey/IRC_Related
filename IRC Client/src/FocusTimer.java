import java.util.TimerTask;

public class FocusTimer extends TimerTask
{

	@Override
	public void run()
	{
		Client.focusDone = true;
	}

}
