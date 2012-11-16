package com.randymxj.us.bus.countdown;

import com.google.ads.AdRequest;

public class HeartBeat extends Thread 
{  
	private MainActivity main;
	public boolean work = false;
	public int count = 0;
	
	public HeartBeat(MainActivity m)
	{     	
		main = m;
		count = 0;
	}

    public void run() 
	{  
    	work = true;
    	
    	while( true )
    	{
    		if( work )
    		{
	    		try 
	    		{
	    			Thread.sleep(1000);
	    			
	    			//Heart beat update
	    			main.mHandler.obtainMessage(1).sendToTarget();
	    			
	    			count++;
	    			
	    			if( count >= 60 )
	    			{
	    				count = 0;
	    			}
	    			if( count == 30 )
	    			{
	    				//Req update XML
	        			main.mHandler.obtainMessage(0).sendToTarget();
	    			}
	    			
	    		} catch (InterruptedException e) 
	    		{
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
    		}
    	}
    	
    } 

}
