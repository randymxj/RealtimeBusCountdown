package com.randymxj.us.bus.countdown;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;


public class MainActivity extends Activity implements OnClickListener, OnLongClickListener, DialogInterface.OnClickListener 
{	
	public LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Track;	
	private LinearLayout Linear_Banner;
	private LinearLayout Linear_Todo;
	
	private TrackerNode selected_item;
	private int Dialog_type = 0;
	private EditText EditText_dialog;
	
	String a_tag, a_title, r_tag, r_title, d_tag, d_title, d_name, d_from, d_to, s_tag, s_title;
	
	private Config conf = new Config(this);
    
    @Override
	public void onClick(DialogInterface dialog, int which) 
    {
    	if( Dialog_type == 1 )
		{
    		if(which == Dialog.BUTTON_POSITIVE)
    		{
    			selected_item.c_title = EditText_dialog.getText().toString();
    			selected_item.tv_agent.setText(selected_item.c_title);
    			
    			conf.WriteTracker();
    		}
		}
    	else if( Dialog_type == 2 )
		{
    		if(which == Dialog.BUTTON_POSITIVE)
    		{
    			conf.refresh_time = Integer.valueOf( EditText_dialog.getText().toString() );
    			conf.WriteConfig();
    			
    			for( int i = 0; i < conf.Trackers.size(); i++ )
    			{
    				conf.Trackers.get(i).setRefreshTime(conf.refresh_time);
    			}
    		}
		}
		
		Dialog_type = 0;		
	}
    
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
    	if( v == Linear_Todo )
    	{
    		// Go to Agency
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectAgencyActivity.class);
			
			startActivityForResult(intent, 1);
    	}
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
    	
    	Linear_Todo = (LinearLayout)findViewById(R.id.Linear_Fresh_Todo);
		TextView tv = (TextView)findViewById(R.id.TextView_Fresh_Todo);
		tv.setText("Press to add your first bus countdown");
		Linear_Todo.setOnClickListener(this);
		Linear_Todo.setVisibility(LinearLayout.GONE);
    	
    	updateList();
    	
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
		
		menu.add(Menu.NONE, Menu.FIRST + 0, 0, "Add a Bus/Stop");
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "Configure refresh time");	
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "Exit");	
		
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
		else if( item.getItemId() ==  (Menu.FIRST + 1) )
		{
			// Rename group
			Dialog_type = 2; // Type 2 Set refresh time
			EditText_dialog = new EditText(this);
			EditText_dialog.setText(String.valueOf(conf.refresh_time));
			AlertDialog.Builder input = new AlertDialog.Builder(this);  
			input.setTitle("Configure refresh time in second");
			input.setIcon(android.R.drawable.ic_dialog_info);
			input.setView(EditText_dialog);
			input.setPositiveButton("Ok", this);
			input.setNegativeButton("Cancel", null);
			input.create().show();
		}
		else if( item.getItemId() ==  (Menu.FIRST + 2) )
		{
			for( int i = 0; i < conf.Trackers.size(); i++ )
			{
				TrackerNode node = conf.Trackers.get(i);
				node.clean();
			}
            finish();
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
			menu.add(Menu.NONE, Menu.FIRST + 0, 0, "Refresh");
			menu.add(Menu.NONE, Menu.FIRST + 1, 1, "Rename");
			menu.add(Menu.NONE, Menu.FIRST + 2, 2, "Delete");
		}
	}
	
	@Override  
	public boolean onContextItemSelected(MenuItem item) 
	{ 	
		if( item.getItemId() == ( Menu.FIRST + 0 ) )
		{
			selected_item.loadTime();
		}
		if( item.getItemId() == ( Menu.FIRST + 1 ) )
		{ 
			// Rename group
			Dialog_type = 1; // Type 1 Rename
			EditText_dialog = new EditText(this);
			EditText_dialog.setText(selected_item.c_title);
			AlertDialog.Builder input = new AlertDialog.Builder(this);  
			input.setTitle("Rename");
			input.setIcon(android.R.drawable.ic_dialog_info);
			input.setView(EditText_dialog);
			input.setPositiveButton("Ok", this);
			input.setNegativeButton("Cancel", null);
			input.create().show();
		}
		else if( item.getItemId() == ( Menu.FIRST + 2 ) )
		{ 
			selected_item.clean();
			Linear_Track.removeView(selected_item.layout);
	    	conf.Trackers.remove(selected_item);
	    	conf.WriteTracker();
	    	
	    	if( conf.Trackers.size() == 0 )
	    	{
	    		Linear_Todo.setVisibility(LinearLayout.VISIBLE);
	    	}
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
        	Linear_Todo.setVisibility(LinearLayout.GONE);
        	
        	// Select Track
        	s_tag = data.getStringExtra("s_tag");
        	s_title = data.getStringExtra("s_title");
        	
        	TrackerNode node = new TrackerNode( a_tag, a_title,
					 r_tag, r_title,
					 d_tag, d_title, d_name, d_from, d_to,
					 s_tag, s_title, 
					 a_title + ": " + r_title );
        	
        	conf.Trackers.add( node );
        	conf.WriteTracker();
        	
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
    		
    		node.setRefreshTime(conf.refresh_time);
    		
    		node.setActivity(this);
    		node.loadTime();
    		
    		Linear_Track.addView(node.layout);
        }
        else if( resultCode == 20 )
        {
        	// Back from select agency
        }
        else if( resultCode == 21 )
        {
        	// Back from select route
        	// Go to Agency
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectAgencyActivity.class);			
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 22 )
        {
        	// Back from select direction
        	// Go to Route
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectRouteActivity.class);
			intent.putExtra("a_tag", a_tag);
			intent.putExtra("a_title", a_title);
			startActivityForResult(intent, 1);
        }
        else if( resultCode == 23 )
        {
        	// Back from select stop
        	// Go to Direction
        	Intent intent = new Intent();
			intent.setClass(MainActivity.this, SelectDirectionActivity.class);		
			intent.putExtra("a_tag", a_tag);
			intent.putExtra("a_title", a_title);
			intent.putExtra("r_tag", r_tag);
			intent.putExtra("r_title", r_title);
			startActivityForResult(intent, 1);
        }

    }
	
	private void updateList()
    {
		Linear_Track.removeAllViews();
		
		if( conf.Trackers.size() > 0 )
		{
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
	    		
	    		node.setRefreshTime(conf.refresh_time);
	    		
	    		node.setActivity(this);
	    		node.loadTime();
	    		
	    		Linear_Track.addView(node.layout);
			}
		}
		else
		{
			Linear_Todo.setVisibility(LinearLayout.VISIBLE);
		}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			for( int i = 0; i < conf.Trackers.size(); i++ )
			{
				TrackerNode node = conf.Trackers.get(i);
				node.clean();
			}
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
	public void onResume() 
	{
		super.onResume();
	}

	@Override
	public void onStop() 
	{
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

}
