package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeNode 
{
	public String epochTime = "";
	public String seconds = "";
	public String dirTag = "";
	public String vehicle = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	
	TimeNode( String inEpochTime, String inSeconds, String inDirTag, String inVehicle )
	{
		epochTime = inEpochTime;
		seconds = inSeconds;
		dirTag = inDirTag;
		vehicle = inVehicle;
	}
	
	public void setLayout( LinearLayout l )
	{
		layout = l;
	}
	
	public void setTextView( TextView t )
	{
		tv_text = t;
		tv_text.setText("~");
	}
	
	public void decreaseTime()
	{
		int s = Integer.valueOf(seconds);
		
		int hh = s / 3600;
		int mm = ( s - hh * 3600 ) / 60;
		int ss = s - hh * 3600 - mm * 60;
		
		tv_text.setText( String.valueOf(hh) + " hrs " + 
						 String.valueOf(mm) + " mins " + 
						 String.valueOf(ss) + " secs");
		
		s--;
		seconds = String.valueOf(s);
	}
}
