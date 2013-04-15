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
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Setting_Agency;
	private TextView TextView_Setting_Agency, TextView_Setting_Agency_Attach;
	
	private LinearLayout Linear_Setting_Route;
	private TextView TextView_Setting_Route, TextView_Setting_Route_Attach;
	
	private LinearLayout Linear_Setting_Direction;
	private TextView TextView_Setting_Direction, TextView_Setting_Direction_Attach,
			TextView_Setting_Direction_From, TextView_Setting_Direction_To;
	
	private LinearLayout Linear_Setting_Stop;
	private TextView TextView_Setting_Stop, TextView_Setting_Stop_Attach;
	
	private LinearLayout Linear_Time;
	
	private LinearLayout Linear_Banner;
	
	private Parser XMLParser;
	
	private ArrayList<TimeNode> Times = new ArrayList<TimeNode>();
	private Timer timer;
	
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
	            		Times.clear();
	            		Times = XMLParser.Times;
	            		updateList();
	            		
	            		Toast.makeText(getApplicationContext(), "updated", Toast.LENGTH_SHORT).show();
	            	}
	            	
	            	break; 
	            }
	            
	            case 10:
	            {
	            	for( int i = 0; i < Times.size(); i++ )
	            	{
	            		Times.get(i).decreaseTime();
	            	}
	            	break;
	            }
	            
	            case 11:
	            {
	            	startParser();
	            	
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
		if( v == Linear_Setting_Agency )
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectAgencyActivity.class);
			
			startActivityForResult(intent, 1);
		}
		else if( v == Linear_Setting_Route )
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectRouteActivity.class);

			intent.putExtra("tag", conf.a_tag);
			intent.putExtra("title", conf.a_title);
			startActivityForResult(intent, 1);
		}
		else if( v == Linear_Setting_Direction )
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectDirectionActivity.class);
			
			intent.putExtra("agency_tag", conf.a_tag);
			intent.putExtra("agency_title", conf.a_title);
			intent.putExtra("route_tag", conf.r_tag);
			intent.putExtra("route_title", conf.r_title);
			startActivityForResult(intent, 1);
		}
		else if( v == Linear_Setting_Stop )
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectStopActivity.class);
			
			intent.putExtra("agency_tag", conf.a_tag);
			intent.putExtra("agency_title", conf.a_title);
			intent.putExtra("route_tag", conf.r_tag);
			intent.putExtra("route_title", conf.r_title);
			intent.putExtra("direction_tag", conf.d_tag);
			intent.putExtra("direction_title", conf.d_title);
			startActivityForResult(intent, 1);
		}
		
		Times.clear();
		Linear_Time.removeAllViews();
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        conf.ReadConfig();
        
        layoutInflater = getLayoutInflater();
        
        Linear_Setting_Agency = (LinearLayout)findViewById(R.id.Linear_Setting_Agency);
        Linear_Setting_Agency.setOnClickListener(this);
    	TextView_Setting_Agency = (TextView)findViewById(R.id.TextView_Setting_Agency);
    	TextView_Setting_Agency_Attach = (TextView)findViewById(R.id.TextView_Setting_Agency_Attach);
    	TextView_Setting_Agency_Attach.setText(conf.a_title);
    	
    	Linear_Setting_Route = (LinearLayout)findViewById(R.id.Linear_Setting_Route);
    	Linear_Setting_Route.setOnClickListener(this);
    	TextView_Setting_Route = (TextView)findViewById(R.id.TextView_Setting_Route);
    	TextView_Setting_Route_Attach = (TextView)findViewById(R.id.TextView_Setting_Route_Attach);
    	TextView_Setting_Route_Attach.setText(conf.r_title);
    	
    	Linear_Setting_Direction = (LinearLayout)findViewById(R.id.Linear_Setting_Direction);
    	Linear_Setting_Direction.setOnClickListener(this);
    	TextView_Setting_Direction = (TextView)findViewById(R.id.TextView_Setting_Direction);
    	TextView_Setting_Direction_Attach = (TextView)findViewById(R.id.TextView_Setting_Direction_Attach);
    	TextView_Setting_Direction_Attach.setText("[" + conf.d_name + "] " + conf.d_title);
    	TextView_Setting_Direction_From = (TextView)findViewById(R.id.TextView_Setting_Direction_From);
    	TextView_Setting_Direction_From.setText("[From] " + conf.d_from);
    	TextView_Setting_Direction_To = (TextView)findViewById(R.id.TextView_Setting_Direction_To);
    	TextView_Setting_Direction_To.setText("[To] " + conf.d_to);
    	
    	Linear_Setting_Stop = (LinearLayout)findViewById(R.id.Linear_Setting_Stop);
    	Linear_Setting_Stop.setOnClickListener(this);
    	TextView_Setting_Stop = (TextView)findViewById(R.id.TextView_Setting_Stop);
    	TextView_Setting_Stop_Attach = (TextView)findViewById(R.id.TextView_Setting_Stop_Attach);
    	TextView_Setting_Stop_Attach.setText(conf.s_title);
    	
    	Linear_Time = (LinearLayout)findViewById(R.id.Linear_Time);
    	
    	Linear_Banner = (LinearLayout)findViewById(R.id.Linear_Banner);
    	
    	if( conf.a_tag.equalsIgnoreCase("") )
    	{
    		// Fresh start
        	
        	TextView_Setting_Agency_Attach.setText("Please select agency");
        	TextView_Setting_Route_Attach.setText("Please select route");
        	TextView_Setting_Direction_Attach.setText("Please select direction");
        	TextView_Setting_Stop_Attach.setText("Please select stop");
        	
    		Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectAgencyActivity.class);
			
			startActivityForResult(intent, 1);
    	}
    	else
    	{
    		TextView_Setting_Agency_Attach.setText(conf.a_title);
    		
        	TextView_Setting_Route_Attach.setText(conf.r_title);
        	
        	TextView_Setting_Direction_Attach.setText("[" + conf.d_name + "] " + conf.d_title);
        	TextView_Setting_Direction_From.setText("[From] " + conf.d_from);
        	TextView_Setting_Direction_To.setText("[To] " + conf.d_to);
        	
        	TextView_Setting_Stop_Attach.setText(conf.s_title);
        	
        	startParser();
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		menu.removeGroup(0);
		
		//menu.add(Menu.NONE, Menu.FIRST + 0, 0, "test");	
		
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
        	conf.a_tag = data.getStringExtra("tag");
        	conf.a_title = data.getStringExtra("title");
        	TextView_Setting_Agency_Attach.setText(conf.a_title);
        	
        	// Clear
        	conf.r_tag = ""; conf.r_title = "";
			conf.d_tag = ""; conf.d_title = ""; conf.d_from = ""; conf.d_to = "";
			conf.s_tag = ""; conf.s_title = "";
			
        	TextView_Setting_Route_Attach.setText("Please select route");
        	TextView_Setting_Direction_Attach.setText("Please select direction");
        	TextView_Setting_Direction_From.setText("[From] ");
        	TextView_Setting_Direction_To.setText("[To] ");
        	TextView_Setting_Stop_Attach.setText("Please select stop");
        	
        	Linear_Setting_Route.setClickable(true);
        	Linear_Setting_Route.setBackground(getResources().getDrawable(R.drawable.button_bg_light));
        	Linear_Setting_Direction.setClickable(false);
        	Linear_Setting_Direction.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Stop.setClickable(false);
        	Linear_Setting_Stop.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	
        	// Go to Route
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectRouteActivity.class);

			intent.putExtra("tag", conf.a_tag);
			intent.putExtra("title", conf.a_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 11 )
        {
        	conf.r_tag = data.getStringExtra("tag");
        	conf.r_title = data.getStringExtra("title");
        	TextView_Setting_Route_Attach.setText(conf.r_title);
        	
        	// Clear
			conf.d_tag = ""; conf.d_title = ""; conf.d_from = ""; conf.d_to = "";
			conf.s_tag = ""; conf.s_title = "";
			
        	TextView_Setting_Direction_Attach.setText("Please select direction");
        	TextView_Setting_Direction_From.setText("[From] ");
        	TextView_Setting_Direction_To.setText("[To] ");
        	TextView_Setting_Stop_Attach.setText("Please select stop");
        	
        	Linear_Setting_Route.setClickable(true);
        	Linear_Setting_Route.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Direction.setClickable(true);
        	Linear_Setting_Direction.setBackground(getResources().getDrawable(R.drawable.button_bg_light));
        	Linear_Setting_Stop.setClickable(false);
        	Linear_Setting_Stop.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	
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
        	conf.d_tag = data.getStringExtra("tag");
        	conf.d_title = data.getStringExtra("title");
        	conf.d_name = data.getStringExtra("name");
        	conf.d_from = data.getStringExtra("from");
        	conf.d_to = data.getStringExtra("to");
        	TextView_Setting_Direction_Attach.setText("[" + conf.d_name + "] " + conf.d_title);
        	TextView_Setting_Direction_From.setText("[From] " + conf.d_from);
        	TextView_Setting_Direction_To.setText("[To] " + conf.d_to);
        	
        	// Clear
			conf.s_tag = ""; conf.s_title = "";
        	TextView_Setting_Stop_Attach.setText("Please select stop");

        	Linear_Setting_Route.setClickable(true);
        	Linear_Setting_Route.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Direction.setClickable(true);
        	Linear_Setting_Direction.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Stop.setClickable(true);
        	Linear_Setting_Stop.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	
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
        	conf.s_tag = data.getStringExtra("tag");
        	conf.s_title = data.getStringExtra("title");
        	TextView_Setting_Stop_Attach.setText(conf.s_title);
        	
        	// Clear
        	Linear_Setting_Route.setClickable(true);
        	Linear_Setting_Route.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Direction.setClickable(true);
        	Linear_Setting_Direction.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	Linear_Setting_Stop.setClickable(true);
        	Linear_Setting_Stop.setBackground(getResources().getDrawable(R.drawable.button_bg));
        	
        	// Setting finish
        	startParser();
        }
        
        conf.WriteConfig();
    }
	
	private void updateList()
    {
		Linear_Time.removeAllViews();

    	for( int i = 0; i < Times.size(); i++ )
    	{
    		TimeNode node = Times.get(i);
						
    		node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button_time, null) );
    		node.setButton((TextView)node.layout.findViewById(R.id.TextView_Selector));
			Linear_Time.addView(node.layout);
    	}
    }
	
	private void startParser()
	{
		if( !conf.a_tag.equalsIgnoreCase("") && !conf.r_tag.equalsIgnoreCase("") && !conf.s_tag.equalsIgnoreCase("") )
		{
			XMLParser = new Parser(this, conf.a_tag, conf.r_tag, conf.s_tag);
			XMLParser.start();

			if( timer == null )
			{
				timer = new Timer(this);
				timer.start();
			}
			
		}
		
		if( Linear_Banner.getChildCount() == 0 )
		{
			// Admob
			AdView adView = new AdView(this, AdSize.BANNER, "a1508ae1594d2fd");
			AdRequest adRequest = new AdRequest();
			adRequest.addTestDevice("B0F76564FCF626C44ECD9339835DD7C4");
	
			Linear_Banner.addView(adView);
			adView.loadAd(adRequest);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			timer.work = false;
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
