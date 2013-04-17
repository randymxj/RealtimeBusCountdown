package com.randymxj.us.bus.countdown;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TrackerNode 
{
	public String a_tag, a_title;
	public String r_tag, r_title;
	public String d_tag, d_title, d_name, d_from, d_to;
	public String s_tag, s_title;
	
	public LinearLayout layout, time_layout;
	public TextView tv_agent, tv_direction, tv_from, tv_to, tv_stop;
	
	private MainActivity main;
	private Parser XMLParser;
	
	private Timer timer = new Timer(this);
	
	public Handler mHandler = new Handler() 
	{  
		public void handleMessage (Message msg) 
        {  
            switch(msg.what)
            {  
	            case 1:
	            {
	            	if( XMLParser.error_code == 0 )
	            	{
	            		time_layout.removeAllViews();

	            		for( int i = 0; i < XMLParser.Times.size(); i++ )
	            		{
	            			TimeNode node = XMLParser.Times.get(i);
	            			node.setLayout( (LinearLayout)main.layoutInflater.inflate(R.layout.button_time, null) );
	            			node.setTextView((TextView)node.layout.findViewById(R.id.TextView_Timer));
	            			
	            			time_layout.addView(node.layout);
	            		}
	            		if( !timer.isAlive() )
	            			timer.start();
	            		Toast.makeText(main.getApplicationContext(), "updated: " + s_tag, Toast.LENGTH_SHORT).show();
	            	}
	            	
	            	break; 
	            }
	            
	            case 10:
	            {
	            	for( int i = 0; i < XMLParser.Times.size(); i++ )
            		{
            			XMLParser.Times.get(i).decreaseTime();
            		}
	            	break;
	            }
	            
	            case 11:
	            {
	            	loadTime();
	            	break;
	            }

	            default:
	            	break;
            }  
        }  
    }; 
    
	
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
	
	public void setLayout( LinearLayout la )
	{
		layout = la;
	}
	
	public void setTimeLayout( LinearLayout lt )
	{
		time_layout = lt;
	}
	
	public void setTextView( TextView ta, TextView td, TextView tf, TextView tt, TextView ts )
	{
		tv_agent = ta;
		tv_direction = td; tv_from = tf; tv_to = tt;
		tv_stop = ts;
		
		tv_agent.setText(a_title + ": " + r_title);
		tv_direction.setText( d_name + " - " + d_title);
		tv_from.setText("Origin: " + d_from);
		tv_to.setText("Destination: " + d_to);
		tv_stop.setText("Stop: " + s_title);
	}
	
	public void setActivity( MainActivity m )
	{
		main = m;
	}
	
	public void loadTime()
	{
		XMLParser = new Parser(main , this, a_tag, r_tag, s_tag);
		XMLParser.start();
	}
}
