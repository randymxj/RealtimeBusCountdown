package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteNode 
{
	public String tag = "";
	public String title = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	
	RouteNode( String inTag, String inTitle )
	{
		tag = inTag;
		title = inTitle;
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
