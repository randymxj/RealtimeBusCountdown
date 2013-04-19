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

public class SelectAgencyActivity extends Activity implements OnClickListener 
{
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Select_Agency_Blank;
	private TextView TextView_Select_Agency_Blank;
	
	private ScrollView Scroll_Agency_List;
	private LinearLayout Linear_Agency_List;
	
	private Parser XMLParser;
	
	ArrayList<AgencyNode> Agencies = new ArrayList<AgencyNode>();
		
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
	            		Agencies = XMLParser.Agencies;
	            		updateList();
	            	}
	            	else
	            	{
	            		TextView_Select_Agency_Blank.setText("Error while loading agency list");
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
    	for( int i = 0; i < Agencies.size(); i++ )
    	{
    		AgencyNode node = Agencies.get(i);
    		if( v == node.layout )
    		{
    			Intent result = new Intent();
    			setResult(10, result);
    			result.putExtra("a_tag", node.tag);
    			result.putExtra("a_title", node.title);
    			result.putExtra("shortTitle", node.shortTitle);
    			result.putExtra("regionTitle", node.regionTitle);
                finish();
    		}
    	}
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectagency);
        
        // Get intent message
 		Intent intent = getIntent();
 		
 		layoutInflater = getLayoutInflater();
     		
 		Linear_Select_Agency_Blank = (LinearLayout)findViewById(R.id.Linear_Select_Agency_Blank);
 		TextView_Select_Agency_Blank = (TextView)findViewById(R.id.TextView_Select_Agency_Blank);
    	
 		Scroll_Agency_List = (ScrollView)findViewById(R.id.Scroll_Agency_List);
 		Linear_Agency_List = (LinearLayout)findViewById(R.id.Linear_Agency_List);
        
        XMLParser = new Parser(this);
        XMLParser.start();
        
        setTitle("Select Agency");
    }
    
    private void updateList()
    {
    	Linear_Agency_List.removeAllViews();
    	
    	Linear_Select_Agency_Blank.setVisibility(View.GONE);

    	for( int i = 0; i < Agencies.size(); i++ )
    	{
    		AgencyNode node = Agencies.get(i);
						
    		node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button, null) );
    		node.setButton((TextView)node.layout.findViewById(R.id.TextView_Button));
			node.layout.setOnClickListener(this);
			Linear_Agency_List.addView(node.layout);
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
