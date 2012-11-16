package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Agency 
{
	public List<String> agencyTitle;
	public List<String> agencyTag;
	
	public Agency ()
	{
		Init();
	}
	
	public void insert( String title, String tag )
	{
		agencyTitle.add(title);
		agencyTag.add(tag);
		
		//Log.i("Agency", tag + ": " + title);
	}
	
	public String checkTag( String tag )
	{
		for( int i = 0; i < agencyTitle.size(); i++ )
		{
			if( agencyTag.get(i).equals(tag) )
			{
				return agencyTitle.get(i);
			}
		}
		return "NONE";
	}
	
	public void Init()
	{
		agencyTitle = new ArrayList<String>();
		agencyTag = new ArrayList<String>();
	}

}
