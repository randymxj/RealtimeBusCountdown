package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class StopList 
{	
	public List<String> stopTitle;
	public List<String> stopTag;
	
	public StopList()
	{
		Init();
	}
	
	public void insert( String title, String tag )
	{
		stopTitle.add(title);
		stopTag.add(tag);
		
		//Log.i("StopList", tag + ": " + title);
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
	}

}
