package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NodeList;

import android.util.Log;

public class Route 
{
	String name = "";
	String title = "";
	public List<String> stopTag;
	public List<String> stopTitle;
	StopList list = null;
	
	public Route(String str, StopList l)
	{
		name = str;
		list = l;
		Init();
	}
	
	public void setTitle(String str)
	{
		title = str;
		//Log.i("信息", "路线: " + str);
	}
	
	public void addSlot(int s, NodeList n)
	{
		for( int i = 0; i < s; i++ )
		{			
			stopTag.add(n.item( i * 2 + 1 ).getAttributes().item(0).getTextContent());
			stopTitle.add(list.checkTag(stopTag.get(i)));
			//Log.i("XXX", stopTag.get(i) + ", " + stopTitle.get(i) );
		}
	}
	
	public String checkTag( String tag )
	{
		for( int i = 0; i < stopTag.size(); i++ )
		{
			if( stopTag.get(i).equals(tag) )
			{
				return stopTitle.get(i);
			}
		}
		return "NONE";
	}
	
	public void Init()
	{
		stopTitle = new ArrayList<String>();
		stopTag = new ArrayList<String>();
		name = "";
		title = "";
	}
}
