package com.randymxj.us.bus.countdown;

public class Timer extends Thread
{
	private TrackerNode main;
	
	public boolean work = true;
	
	Timer(TrackerNode m)
	{
		main = m;
	}
	
	public void run() 
	{
		int count = 0;
		
		while( work )
		{
			try 
			{
				count++;
				
				if( count == 30 )
				{
					count = 0;
					main.mHandler.obtainMessage(11).sendToTarget();
				}
				else
				{
					main.mHandler.obtainMessage(10).sendToTarget();
				}
				
				sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
