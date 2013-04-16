package com.randymxj.us.bus.countdown;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.randymxj.us.bus.countdown.R;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;
import com.google.analytics.tracking.android.EasyTracker;

import android.widget.LinearLayout;


public class MainActivity extends Activity implements OnClickListener 
{
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Time;	
	private LinearLayout Linear_Banner;
	
	private Parser XMLParser;
	
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
	            
	            case 10:
	            {
	            	break;
	            }
	            
	            case 11:
	            {
	            	break;
	            }
	
	            default:
	            	break;
            }  
        }  
    }; 
    
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
        
        layoutInflater = getLayoutInflater();
    	
    	Linear_Time = (LinearLayout)findViewById(R.id.Linear_Time);
    	Linear_Banner = (LinearLayout)findViewById(R.id.Linear_Banner);
    	
    	// Admob
		AdView adView = new AdView(this, AdSize.BANNER, "a1508ae1594d2fd");
		AdRequest adRequest = new AdRequest();
		//adRequest.addTestDevice("B0F76564FCF626C44ECD9339835DD7C4");

		Linear_Banner.addView(adView);
		adView.loadAd(adRequest);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		menu.removeGroup(0);
		
		//menu.add(Menu.NONE, Menu.FIRST + 0, 0, "CAO");	
		
		return true;
	}
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if( item.getItemId() ==  (Menu.FIRST + 0) )
		{
			
		}
	        
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{  
        super.onActivityResult(requestCode, resultCode, data);  
        
        if( resultCode == 10 )
        {
        	// Go to Route
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectRouteActivity.class);

			intent.putExtra("tag", conf.a_tag);
			intent.putExtra("title", conf.a_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 11 )
        {
        	// Go to Direction
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectDirectionActivity.class);
			
			intent.putExtra("agency_tag", conf.a_tag);
			intent.putExtra("agency_title", conf.a_title);
			intent.putExtra("route_tag", conf.r_tag);
			intent.putExtra("route_title", conf.r_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 12 )
        {
        	// Go to Stop
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectStopActivity.class);
			
			intent.putExtra("agency_tag", conf.a_tag);
			intent.putExtra("agency_title", conf.a_title);
			intent.putExtra("route_tag", conf.r_tag);
			intent.putExtra("route_title", conf.r_title);
			intent.putExtra("direction_tag", conf.d_tag);
			intent.putExtra("direction_title", conf.d_title);
			startActivityForResult(intent, 1);
        }else if( resultCode == 13 )
        {
        	// Setting finish
        }
        else if( resultCode == 20 )
        {
        	// Nothing
        }
        
        conf.WriteConfig();
    }
	
	private void updateList()
    {
		Linear_Time.removeAllViews();
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
