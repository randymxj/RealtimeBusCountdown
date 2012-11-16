package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class RouteList 
{
	public List<String> routeTitle;
	public List<String> routeTag;
	
	public RouteList ()
	{
		Init();
	}
	
	public void insert( String title, String tag )
	{
		routeTitle.add(title);
		routeTag.add(tag);
		
		//Log.i("StopList", tag + ": " + title);
	}
	
	public String checkTag( String tag )
	{
		for( int i = 0; i < routeTitle.size(); i++ )
		{
			if( routeTag.get(i).equals(tag) )
			{
				return routeTitle.get(i);
			}
		}
		return "NONE";
	}
	
	public void Init()
	{
		routeTitle = new ArrayList<String>();
		routeTag = new ArrayList<String>();
	}

}
