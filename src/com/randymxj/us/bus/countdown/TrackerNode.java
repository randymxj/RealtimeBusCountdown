package com.randymxj.us.bus.countdown;

import com.randymxj.us.bus.countdown.R;
import com.randymxj.us.bus.countdown.R.id;
import com.randymxj.us.bus.countdown.R.layout;

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
	public String c_title;
	
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
	            	time_layout.removeAllViews();
	            	
	            	if( XMLParser.error_code == 0 )
	            	{
	            		for( int i = 0; i < XMLParser.Times.size(); i++ )
	            		{
	            			TimeNode node = XMLParser.Times.get(i);
	            			node.setLayout( (LinearLayout)main.layoutInflater.inflate(R.layout.button_time, null) );
	            			node.setTextView((TextView)node.layout.findViewById(R.id.TextView_Timer));
	            			
	            			time_layout.addView(node.layout);
	            		}
	            		if( !timer.isAlive() )
	            			timer.start();
	            		
	            		if( XMLParser.Times.size() > 0 )
	            		{
	            			String text = r_title + ": " + XMLParser.Times.get(0).time;
	            			Toast.makeText(main.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	            		}
	            		else
	            		{
	            			LinearLayout layout = (LinearLayout)main.layoutInflater.inflate(R.layout.button_time, null);
	            			TextView tv = (TextView)layout.findViewById(R.id.TextView_Timer);
	            			tv.setText( "No data available at this time" );
	            			layout.setBackgroundColor(0xFF1BA1E2);
	            			time_layout.addView(layout);
	            		}
	            	}
	            	else
	            	{
	            		LinearLayout layout = (LinearLayout)main.layoutInflater.inflate(R.layout.button_time, null);
            			TextView tv = (TextView)layout.findViewById(R.id.TextView_Timer);
            			
            			if( XMLParser.error_code == 404 )
            			{
            				tv.setText( "No data available at this time" );
	            			layout.setBackgroundColor(0xFF1BA1E2);
            			}
            			else
            			{
            				tv.setText( "Error while check for data" );
            				layout.setBackgroundColor(0xFFE51400);
            			}
            			
            			time_layout.addView(layout);            			
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
				 String in_s_tag, String in_s_title,
				 String in_c_title )
	{
		a_tag = in_a_tag; a_title = in_a_title;
		r_tag = in_r_tag; r_title = in_r_title;
		d_tag = in_d_tag; d_title = in_d_title; d_name = in_d_name; d_from = in_d_from; d_to = in_d_to;
		s_tag = in_s_tag; s_title = in_s_title;	
		c_title = in_c_title;
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

		tv_agent.setText(c_title);
		
		if( !d_name.equalsIgnoreCase(d_title) )
		{
			if( !d_name.equalsIgnoreCase("") )
			{
				tv_direction.setText( "Direction: " + d_name + " - " + d_title );
			}
			else
			{
				tv_direction.setText( "Direction: " + d_title );
			}
		}
		else
		{
			tv_direction.setText( "Direction: " + d_title );
		}
		
		tv_from.setText("Origin: " + d_from);
		tv_to.setText("Destination: " + d_to);
		tv_stop.setText("[ Stop: " + s_title + " ]");
	}
	
	public void setActivity( MainActivity m )
	{
		main = m;
	}
	
	public void loadTime()
	{
		XMLParser = new Parser(main , this, a_tag, r_tag, s_tag);
		XMLParser.start();
		
		LinearLayout layout = (LinearLayout)main.layoutInflater.inflate(R.layout.button_time, null);
		TextView tv = (TextView)layout.findViewById(R.id.TextView_Timer);
		tv.setText( "Loading" );
		time_layout.addView(layout);
	}
	
	public void clean()
	{
		timer.work = false;
		timer.interrupt();
	}
}
