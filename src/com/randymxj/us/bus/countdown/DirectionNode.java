package com.randymxj.us.bus.countdown;

import java.util.ArrayList;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DirectionNode 
{
	public String tag = "";
	public String title = "";
	public String name = "";
	public String useForUI = "";
	
	public LinearLayout layout;
	public TextView tv_text;
	public TextView tv_text_from, tv_text_to;
	
	public StopNode from_node, to_node;
	
	public ArrayList<String> Stops = new ArrayList<String>();
	
	DirectionNode( String inTag, String inTitle, String inName, String inUseForUI )
	{
		tag = inTag;
		title = inTitle;
		name = inName;
		useForUI = inUseForUI;
		
		Stops.clear();
	}
	
	public void addStop(String stopTag)
	{
		Stops.add(stopTag);
	}
	
	public void setLayout( LinearLayout l )
	{
		layout = l;
	}
	
	public void setButton( TextView t, TextView tf, TextView tt)
	{
		tv_text = t;
		tv_text.setText("[" + name + "] " + title);
		
		tv_text_from = tf;		
		tv_text_to = tt;
	}
	
	public void setStartStop(StopNode sta, StopNode sto)
	{
		from_node = sta;
		to_node = sto;
		tv_text_from.setText("[From] " + sta.title);
		tv_text_to.setText("[To] " + sto.title);
	}
	
}
