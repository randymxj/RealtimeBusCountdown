package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

public class AgencyNode 
{
	public String tag = "";
	public String title = "";
	public String shortTitle = "";
	public String regionTitle = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	
	AgencyNode( String inTag, String inTitle, String inShortTitle, String inRegionTitle )
	{
		tag = inTag;
		title = inTitle;
		regionTitle = inRegionTitle;
		regionTitle = inShortTitle;
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
