package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

public class TrackerNode 
{
	public String a_tag, a_title;
	public String r_tag, r_title;
	public String d_tag, d_title, d_name, d_from, d_to;
	public String s_tag, s_title;
	
	public LinearLayout layout, time_layout;
	public TextView tv_agent, tv_route, tv_direction, tv_from, tv_to, tv_stop;
	
	TrackerNode( String in_a_tag, String in_a_title,
				 String in_r_tag, String in_r_title, 
				 String in_d_tag, String in_d_title, String in_d_name, String in_d_from, String in_d_to,
				 String in_s_tag, String in_s_title )
	{
		a_tag = in_a_tag; a_title = in_a_title;
		r_tag = in_r_tag; r_title = in_r_title;
		d_tag = in_d_tag; d_title = in_d_title; d_name = in_d_name; d_from = in_d_from; d_to = in_d_to;
		s_tag = in_s_tag; s_title = in_s_title;	
	}
	
	public void setLayout( LinearLayout la, LinearLayout lt )
	{
		layout = la;
		time_layout = lt;
	}
	
	public void setButton( TextView ta, TextView tr, TextView td, TextView tf, TextView tt, TextView ts )
	{
		tv_agent = ta;
		tv_route = tr;
		tv_direction = td; tv_from = tf; tv_to = tt;
		tv_stop = ts;
	}
}
