package com.randymxj.us.bus.countdown;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class SelectStopActivity extends FragmentActivity implements OnClickListener, OnMarkerClickListener 
{
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Select_Stop_Blank;
	private TextView TextView_Select_Stop_Blank;
	
	private Parser XMLParser;
	
	GoogleMap mMap;
	private Spinner spinner;  
    private ArrayAdapter<String> adapter;
	
	String a_tag, a_title, r_tag, r_title, d_tag, d_title, d_name, d_from, d_to;
	String s_tag, s_title;
	
	private Button button_ok;
	private boolean isSelectByMarker = false;
	
	public ArrayList<StopNode> Stops = new ArrayList<StopNode>();

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
	            		Stops.clear();
	            		
	            		ArrayList<DirectionNode> Directions = XMLParser.Directions;
	            		ArrayList<StopNode> AllStops = XMLParser.Stops;
	            		
	            		// Get the selected direction
	            		DirectionNode d_node = null;
	            		for( int i = 0; i < Directions.size(); i++ )
	            		{
	            			if( Directions.get(i).tag.equalsIgnoreCase( d_tag ))
	            			{
	            				d_node = Directions.get(i);
	            				break;
	            			}
	            		}
	            		
	            		double MaxLat = 0, MinLat = 0, MaxLon = 0, MinLon = 0;
	            		
	            		for( int j = 0; j < d_node.Stops.size(); j++ )
	            		{
	            			for( int k = 0; k < AllStops.size(); k++ )
	            			{
	            				if( d_node.Stops.get(j).equalsIgnoreCase( AllStops.get(k).tag ))
	            				{
	            					Stops.add(AllStops.get(k));
	            					
	            					double lat = Double.valueOf( AllStops.get(k).lat );
	    	            			double lon = Double.valueOf( AllStops.get(k).lon );
    	            				
	    	            			// Store MAX/MIN value
	    	            			if( Stops.size() == 1 )
	    	            			{
	    	            				MaxLat = MinLat = lat;
	    	            				MaxLon = MinLon = lon;
	    	            			}
	    	            			else
	    	            			{
	    	            				if( lat > MaxLat )
	    	            					MaxLat = lat;
	    	            				if( lat < MinLat )
	    	            					MinLat = lat;
	    	            				if( lon > MaxLon )
	    	            					MaxLon = lon;
	    	            				if( lon < MinLon )
	    	            					MinLon = lon;	    	            				
	    	            			}
	            				}
	            			}
	            		}
	            		
	            		if( mMap != null )
	            		{	
		            		// Add maker into the map	            	
		            		drawMarkerAndLine();
		            		
		            		// Calculate the zoom and center of the map
		            		/*
		            		double ave_lat = ( MaxLat + MinLat )/2;
		            		double ave_lon = ( MaxLon + MinLon )/2;
		            		double ZoomLevel = 0;
		            		if( ( MaxLat - MinLat ) > ( MaxLon - MinLon ) )
		            			ZoomLevel = 10 - ( ( MaxLat - MinLat - 0.2 ) * 20 );
		            		else
		            			ZoomLevel = 10 - ( ( MaxLon - MinLon - 0.2 ) * 20 );
		            		*/

		            		DisplayMetrics dm = new DisplayMetrics();
		            		getWindowManager().getDefaultDisplay().getMetrics(dm);
		            		int widthPixels= dm.widthPixels;
		            		
		            		mMap.moveCamera( 
		            				CameraUpdateFactory.newLatLngBounds(
		            						new LatLngBounds( 
		            								new LatLng(MinLat, MinLon), 
		            								new LatLng(MaxLat, MaxLon) ), widthPixels, widthPixels, 100) );
		            		
		            		//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ave_lat, ave_lon), (float)ZoomLevel));
	            		}
	            		
	            		updateList();
	            	}
	            	else
	            	{
	            		TextView_Select_Stop_Blank.setText("Error while loading stop list");
	            	}
	            	
	            	break;
	            }
	
	            default:
	            	break;
            }  
        }  
    }; 
    
    @Override
	public boolean onMarkerClick(Marker arg0) 
    {
		for( int i = 0; i < Stops.size(); i++ )
		{
			if( arg0.equals( Stops.get(i).marker ) )
			{
				isSelectByMarker = true;
				spinner.setSelection( i + 1 );
			}
		}
		
		return false;
	}
    
    @Override
	public void onClick(View v) 
    {	
    	Intent result = new Intent();
		setResult(13, result);
		result.putExtra("s_tag", s_tag);
		result.putExtra("s_title", s_title);
        finish();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectstop);
        
        // Get intent message
  		Intent intent = getIntent();
  		a_tag = intent.getStringExtra("a_tag");
  		a_title = intent.getStringExtra("a_title");
  		r_tag = intent.getStringExtra("r_tag");
  		r_title = intent.getStringExtra("r_title");
  		d_tag = intent.getStringExtra("d_tag");
  		d_title = intent.getStringExtra("d_title");
  		
  		layoutInflater = getLayoutInflater();
      		
  		Linear_Select_Stop_Blank = (LinearLayout)findViewById(R.id.Linear_Select_Stop_Blank);
  		TextView_Select_Stop_Blank = (TextView)findViewById(R.id.TextView_Select_Stop_Blank);
        
        button_ok = (Button)findViewById(R.id.Button_Stop_Ok);
  		button_ok.setOnClickListener(this);
  		button_ok.setEnabled(false);
        
        if (mMap == null) 
        {
            mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if( mMap != null )
            {
            	mMap.setMyLocationEnabled(true);
            	mMap.setOnMarkerClickListener(this);
            }
        }
        
        XMLParser = new Parser(this, a_tag, r_tag);
        XMLParser.start();
        
        setTitle("Select Stop");
    }
    
    private void updateList()
    {
    	Linear_Select_Stop_Blank.setVisibility(View.GONE);   	
    	
    	spinner = (Spinner)findViewById(R.id.Spinner_Stop);  
        String[] array = new String[Stops.size() + 1];
        array[0] = "Please select a stop";
        for( int i = 0; i < Stops.size(); i++ )
    	{
        	array[i+1] = Stops.get(i).title;
    	}
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, array);  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinner.setAdapter(adapter); 
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()  
        {  
            @Override  
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
            {  
            	if( arg2 > 0 )
            	{
            		button_ok.setEnabled(true);
            		
            		if( !isSelectByMarker && mMap != null )
            		{
            			double Lat = Double.valueOf( Stops.get(arg2 - 1).lat );
            			double Lon = Double.valueOf( Stops.get(arg2 - 1).lon );
            			mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Lat, Lon))); 
            			
            			Stops.get(arg2 - 1).marker.showInfoWindow();
            		
            		}
            		
            		s_tag = Stops.get(arg2 - 1).tag;
            		s_title = Stops.get(arg2 - 1).title;
            		arg0.setVisibility(View.VISIBLE); 
            		
            		isSelectByMarker = false;
            	}
            	else
            	{
            		button_ok.setEnabled(false);
            	}
                
            }  
  
            @Override  
            public void onNothingSelected(AdapterView<?> arg0) 
            {  
                // TODO Auto-generated method stub  
                  
            }
              
        }); 
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent result = new Intent();
			setResult(23, result); 
            finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
    public void drawMarkerAndLine()
    {
    	mMap.clear();
		for( int i = 0; i < Stops.size(); i++ )
		{
			double lat = Double.valueOf( Stops.get(i).lat );
			double lon = Double.valueOf( Stops.get(i).lon );
			String title = Stops.get(i).title;
			
			if( i > 0 )
			{
				double l_lat = Double.valueOf( Stops.get(i-1).lat );
				double l_lon = Double.valueOf( Stops.get(i-1).lon );
			
    			PolylineOptions lineOptions = new PolylineOptions()
    				.add(new LatLng(l_lat, l_lon))
    				.add(new LatLng(lat, lon))
    				.color(Color.GREEN);
    			
    			mMap.addPolyline(lineOptions);
			}

			MarkerOptions markerOption = null;
			if( i == 0 )
			{
				markerOption = new MarkerOptions().position(new LatLng(lat, lon))
					.title(title)
					.snippet(Stops.get(i).stopid)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			}
			else if( i == Stops.size() - 1 )
			{
				markerOption = new MarkerOptions().position(new LatLng(lat, lon))
    					.title(title)
    					.snippet(Stops.get(i).stopid)
    					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
			}
			else
			{
				markerOption = new MarkerOptions().position(new LatLng(lat, lon))
    					.title(title)
    					.snippet(Stops.get(i).stopid)
    					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			}
			
			Marker marker  = mMap.addMarker( markerOption );
			Stops.get(i).setMarker(marker);
		}
    }
    
}
