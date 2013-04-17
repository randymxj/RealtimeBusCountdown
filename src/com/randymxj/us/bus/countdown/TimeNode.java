package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeNode 
{
	public String epochTime = "";
	public String seconds = "";
	public String dirTag = "";
	public String vehicle = "";
	
	public String time = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	
	TimeNode( String inEpochTime, String inSeconds, String inDirTag, String inVehicle )
	{
		epochTime = inEpochTime;
		seconds = inSeconds;
		dirTag = inDirTag;
		vehicle = inVehicle;
		
		setupTime();
	}
	
	public void setLayout( LinearLayout l )
	{
		layout = l;
	}
	
	public void setTextView( TextView t )
	{
		tv_text = t;
	}
	
	public void decreaseTime()
	{	
		setupTime();
		
		if( tv_text != null )
			tv_text.setText( time );
		
		int s = Integer.valueOf(seconds);
		s--;
		seconds = String.valueOf(s);
	}
	
	public void setupTime()
	{	
		int s = Integer.valueOf(seconds);
		
		if( s >= 0 )
		{
			int hh = s / 3600;
			int mm = ( s - hh * 3600 ) / 60;
			int ss = s - hh * 3600 - mm * 60;
			
			time = String.valueOf(hh) + " hrs " + 
				   String.valueOf(mm) + " mins " + 
				   String.valueOf(ss) + " secs";
		}
		else
		{
			time = "Bus is running away...";
		}
	}
}
