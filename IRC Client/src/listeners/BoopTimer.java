package listeners;

import java.util.TimerTask;

import client.Client;

public class BoopTimer extends TimerTask
{

	@Override
	public void run()
	{
		Client.timerDone = true;
	}
}
