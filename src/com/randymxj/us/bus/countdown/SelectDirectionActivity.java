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

public class SelectDirectionActivity extends Activity implements OnClickListener 
{
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Select_Direction_Blank;
	private TextView TextView_Select_Direction_Blank;
	
	private ScrollView Scroll_Direction_List;
	private LinearLayout Linear_Direction_List;
	
	private Parser XMLParser;
	
	String a_tag, a_title, r_tag, r_title;
	
	public ArrayList<DirectionNode> Directions = new ArrayList<DirectionNode>();
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
	            		Directions = XMLParser.Directions;
	            		Stops = XMLParser.Stops;
	            		updateList();
	            	}
	            	else
	            	{
	            		TextView_Select_Direction_Blank.setText("Error while loading direction list");
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
    	for( int i = 0; i < Directions.size(); i++ )
    	{
    		DirectionNode node = Directions.get(i);
    		if( v == node.layout )
    		{
    			Intent result = new Intent();
    			setResult(12, result);
    			result.putExtra("d_tag", node.tag);
    			result.putExtra("d_title", node.title);
    			result.putExtra("d_name", node.name);
    			result.putExtra("d_from", node.from_node.title);
    			result.putExtra("d_to", node.to_node.title);
                finish();
    		}
    	}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdirection);
        
        // Get intent message
  		Intent intent = getIntent();
  		a_tag = intent.getStringExtra("a_tag");
  		a_title = intent.getStringExtra("a_title");
  		r_tag = intent.getStringExtra("r_tag");
  		r_title = intent.getStringExtra("r_title");
  		
  		layoutInflater = getLayoutInflater();
      		
  		Linear_Select_Direction_Blank = (LinearLayout)findViewById(R.id.Linear_Select_Direction_Blank);
  		TextView_Select_Direction_Blank = (TextView)findViewById(R.id.TextView_Select_Direction_Blank);
     	
  		Scroll_Direction_List = (ScrollView)findViewById(R.id.Scroll_Direction_List);
  		Linear_Direction_List = (LinearLayout)findViewById(R.id.Linear_Direction_List);
         
        XMLParser = new Parser(this, a_tag, r_tag);
        XMLParser.start();
        
        setTitle("Select Direction");
    }
    
    private void updateList()
    {
    	Linear_Direction_List.removeAllViews();
    	
    	Linear_Select_Direction_Blank.setVisibility(View.GONE);

    	for( int i = 0; i < Directions.size(); i++ )
    	{
    		DirectionNode node = Directions.get(i);
						
    		node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button_direction, null) );
    		node.setButton((TextView)node.layout.findViewById(R.id.TextView_Selector),
    					   (TextView)node.layout.findViewById(R.id.TextView_Selector_From),
    					   (TextView)node.layout.findViewById(R.id.TextView_Selector_To));
    		node.setStartStop(checkStop(node.Stops.get(0)), checkStop(node.Stops.get(node.Stops.size()-1)));
			node.layout.setOnClickListener(this);
			Linear_Direction_List.addView(node.layout);
    	}
    }
    
    public StopNode checkStop(String tag)
    {
    	for( int i = 0; i < Stops.size(); i++ )
    	{
    		if( Stops.get(i).tag.equalsIgnoreCase( tag ))
    		{
    			return Stops.get(i);
    		}
    	}
    	
    	return null;
    }
    
}
