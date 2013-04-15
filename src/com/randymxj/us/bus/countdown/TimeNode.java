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
	
	public void setButton( TextView t )
	{
		tv_text = t;
		tv_text.setText(seconds);
	}
	
	public void decreaseTime()
	{
		int s = Integer.valueOf(seconds);
		s--;
		seconds = String.valueOf(s);
		tv_text.setText(seconds);
	}
}
