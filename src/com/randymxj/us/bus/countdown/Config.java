package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.content.SharedPreferences;

public class Config 
{
	private Activity main;
	
	public String a_tag, a_title;
	public String r_tag, r_title;
	public String d_tag, d_title, d_name, d_from, d_to;
	public String s_tag, s_title;
	
	public Config(Activity m)
	{
		main = m;
	}
	
	public void WriteConfig()
	{
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		preferences.edit().putString("a_tag", a_tag).commit();
		preferences.edit().putString("a_title", a_title).commit();
		
		preferences.edit().putString("r_tag", r_tag).commit();
		preferences.edit().putString("r_title", r_title).commit();
		
		preferences.edit().putString("d_tag", d_tag).commit();
		preferences.edit().putString("d_title", d_title).commit();
		preferences.edit().putString("d_name", d_name).commit();
		preferences.edit().putString("d_from", d_from).commit();
		preferences.edit().putString("d_to", d_to).commit();
		
		preferences.edit().putString("s_tag", s_tag).commit();
		preferences.edit().putString("s_title", s_title).commit();
	}
	
	public void ReadConfig()
	{		
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		a_tag = preferences.getString("a_tag", "");
		a_title = preferences.getString("a_title", "");
		
		r_tag = preferences.getString("r_tag", "");
		r_title = preferences.getString("r_title", "");
		
		d_tag = preferences.getString("d_tag", "");
		d_title = preferences.getString("d_title", "");
		d_name = preferences.getString("d_name", "");
		d_from = preferences.getString("d_from", "");
		d_to = preferences.getString("d_to", "");
		
		s_tag = preferences.getString("s_tag", "");
		s_title = preferences.getString("s_title", "");
	}
}
