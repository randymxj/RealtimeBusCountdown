package com.randymxj.us.bus.countdown;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;


public class MainActivity extends Activity implements OnClickListener, OnLongClickListener 
{
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
	public LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Track;	
	private LinearLayout Linear_Banner;
	
	private TrackerNode selected_item;
	
	private Parser XMLParser;
	
	String a_tag, a_title, r_tag, r_title, d_tag, d_title, d_name, d_from, d_to, s_tag, s_title;
	
	private Config conf = new Config(this);
    
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
	            		Toast.makeText(getApplicationContext(), "updated", Toast.LENGTH_SHORT).show();
	            	}
	            	
	            	break; 
	            }
	
	            default:
	            	break;
            }  
        }  
    }; 
    
    @Override
	public boolean onLongClick(View v)
    {
    	selected_item = null;

    	for( int i = 0; i < conf.Trackers.size(); i++ )
		{
			TrackerNode node = conf.Trackers.get(i);  
			if( v == node.layout )
				selected_item = node;
		}
        
		return false;
	}
    
    @Override
	public void onClick(View v) 
    {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        conf.ReadConfig();
        conf.ReadTracker();
        
        layoutInflater = getLayoutInflater();
    	
        Linear_Track = (LinearLayout)findViewById(R.id.Linear_Track);
    	Linear_Banner = (LinearLayout)findViewById(R.id.Linear_Banner);
    	
    	updateList();
    	
    	// Admob
		AdView adView = new AdView(this, AdSize.BANNER, "a1508ae1594d2fd");
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice("B0F76564FCF626C44ECD9339835DD7C4");

		Linear_Banner.addView(adView);
		adView.loadAd(adRequest);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		menu.removeGroup(0);
		
		menu.add(Menu.NONE, Menu.FIRST + 0, 0, "Add Track");	
		
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if( item.getItemId() ==  (Menu.FIRST + 0) )
		{
			// Go to Agency
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectAgencyActivity.class);
			
			startActivityForResult(intent, 1);
		}
	        
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{  
		super.onCreateContextMenu(menu, v, menuInfo);  
		if( selected_item != null )
		{
			menu.setHeaderTitle( selected_item.a_title + ": " +  selected_item.r_title );    
			menu.add(Menu.NONE, Menu.FIRST + 0, 0, "Delete");    
		}
	}
	
	@Override  
	public boolean onContextItemSelected(MenuItem item) 
	{  
		if( item.getItemId() == ( Menu.FIRST + 0 ) )
		{ 
			Linear_Track.removeView(selected_item.layout);
	    	conf.Trackers.remove(selected_item);
	    	conf.WriteTracker();
		}
		
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{  
        super.onActivityResult(requestCode, resultCode, data);  
        
        if( resultCode == 10 )
        {
        	a_tag = data.getStringExtra("a_tag");
        	a_title = data.getStringExtra("a_title");
        	
        	// Go to Route
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectRouteActivity.class);

			intent.putExtra("a_tag", a_tag);
			intent.putExtra("a_title", a_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 11 )
        {
        	r_tag = data.getStringExtra("r_tag");
        	r_title = data.getStringExtra("r_title");
        	
        	// Go to Direction
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectDirectionActivity.class);
			
			intent.putExtra("a_tag", a_tag);
			intent.putExtra("a_title", a_title);
			intent.putExtra("r_tag", r_tag);
			intent.putExtra("r_title", r_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 12 )
        {
        	d_tag = data.getStringExtra("d_tag");
        	d_title = data.getStringExtra("d_title");
        	d_name = data.getStringExtra("d_name");
        	d_from = data.getStringExtra("d_from");
        	d_to = data.getStringExtra("d_to");
        	
        	
        	// Go to Stop
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectStopActivity.class);
			
			intent.putExtra("a_tag", a_tag);
			intent.putExtra("a_title", a_title);
			intent.putExtra("r_tag", r_tag);
			intent.putExtra("r_title", r_title);
			intent.putExtra("d_tag", d_tag);
			intent.putExtra("d_title", d_title);
			startActivityForResult(intent, 1);
        }else if( resultCode == 13 )
        {
        	// Select Track
        	s_tag = data.getStringExtra("s_tag");
        	s_title = data.getStringExtra("s_title");
        	
        	conf.Trackers.add(new TrackerNode(a_tag, a_title,
					 r_tag, r_title,
					 d_tag, d_title, d_name, d_from, d_to,
					 s_tag, s_title));
        	
        	conf.WriteTracker();
        	
        	updateList();
        }
        else if( resultCode == 20 )
        {
        	// Nothing
        }

    }
	
	private void updateList()
    {
		Linear_Track.removeAllViews();
		
		for( int i = 0; i < conf.Trackers.size(); i++ )
		{
			// Node Level
			TrackerNode node = conf.Trackers.get(i);
			
			node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button_all, null) );
			node.setTimeLayout( (LinearLayout)node.layout.findViewById(R.id.Linear_Time) );
			node.setTextView((TextView)node.layout.findViewById(R.id.TextView_All_Agency),
							 (TextView)node.layout.findViewById(R.id.TextView_All_Direction),
							 (TextView)node.layout.findViewById(R.id.TextView_All_From),
							 (TextView)node.layout.findViewById(R.id.TextView_All_To),
							 (TextView)node.layout.findViewById(R.id.TextView_All_Stop));		
			node.layout.setOnClickListener(this);
			node.layout.setOnLongClickListener(this);
    		registerForContextMenu(node.layout);
    		
    		node.setActivity(this);
    		node.loadTime();
    		
    		Linear_Track.addView(node.layout);
		}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
            finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onStart() 
	{
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() 
	{
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

}
