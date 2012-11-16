package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class BusList 
{	
	public int[] time;
	public List<String> vehicle;
	public int count;
	
	public BusList()
	{
		Init();
	}
	
	public void insert( String t, String v )
	{
		time[count] = Integer.valueOf(t);
		vehicle.add(v);
		count++;
		
		//Log.i("AAA", t + ", " + v);
	}
	
	public void Init()
	{
		time = new int[10];
		vehicle = new ArrayList<String>();
		count = 0;
	}

}
