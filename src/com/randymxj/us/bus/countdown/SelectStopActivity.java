package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SelectStopActivity extends Activity implements OnClickListener 
{
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Select_Stop_Blank;
	private TextView TextView_Select_Stop_Blank;
	
	private ScrollView Scroll_Stop_List;
	private LinearLayout Linear_Stop_List;
	
	private Parser XMLParser;
	
	String a_tag, a_title, r_tag, r_title, d_tag, d_title, d_name, d_from, d_to;
	
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
	            		
	            		for( int j = 0; j < d_node.Stops.size(); j++ )
	            		{
	            			for( int k = 0; k < AllStops.size(); k++ )
	            			{
	            				if( d_node.Stops.get(j).equalsIgnoreCase( AllStops.get(k).tag ))
	            				{
	            					Stops.add(AllStops.get(k));
	            				}
	            			}
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
	public void onClick(View v) 
    {
    	for( int i = 0; i < Stops.size(); i++ )
    	{
    		StopNode node = Stops.get(i);
    		if( v == node.layout )
    		{
    			Intent result = new Intent();
    			setResult(13, result);
    			result.putExtra("s_tag", node.tag);
    			result.putExtra("s_title", node.title);
                finish();
    		}
    	}
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
     	
  		Scroll_Stop_List = (ScrollView)findViewById(R.id.Scroll_Stop_List);
  		Linear_Stop_List = (LinearLayout)findViewById(R.id.Linear_Stop_List);
         
        XMLParser = new Parser(this, a_tag, r_tag);
        XMLParser.start();
        
        setTitle("Select Stop");
    }
    
    private void updateList()
    {
    	Linear_Stop_List.removeAllViews();
    	
    	Linear_Select_Stop_Blank.setVisibility(View.GONE);

    	for( int i = 0; i < Stops.size(); i++ )
    	{
    		StopNode node = Stops.get(i);
						
    		node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button, null) );
    		node.setButton((TextView)node.layout.findViewById(R.id.TextView_Selector));
			node.layout.setOnClickListener(this);
			Linear_Stop_List.addView(node.layout);
    	}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent result = new Intent();
			setResult(20, result); 
            finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
}
