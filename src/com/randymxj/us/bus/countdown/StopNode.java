package com.randymxj.us.bus.countdown;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StopNode 
{
	public String tag = "";
	public String title = "";
	public String lat = "";
	public String lon = "";
	public String stopid = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	
	StopNode( String inTag, String inTitle, String inLat, String inLon, String inStopid )
	{
		tag = inTag;
		title = inTitle;
		lat = inLat;
		lon = inLon;
		stopid = inStopid;
	}
	
	public void setLayout( LinearLayout l )
	{
		layout = l;
	}
	
	public void setButton( TextView t )
	{
		tv_text = t;
		tv_text.setText(title);
	}
	
}
