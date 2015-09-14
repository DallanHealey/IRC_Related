import java.util.TimerTask;

public class BoopTimer extends TimerTask
{

	@Override
	public void run()
	{
		Client.timerDone = true;
	}

}
