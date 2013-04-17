package com.randymxj.us.bus.countdown;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SelectRouteActivity extends Activity implements OnClickListener 
{
	private LayoutInflater layoutInflater;
	
	private LinearLayout Linear_Select_Route_Blank;
	private TextView TextView_Select_Route_Blank;
	
	private ScrollView Scroll_Route_List;
	private LinearLayout Linear_Route_List;
	
	private Parser XMLParser;
	
	String a_tag, a_title;
	
	ArrayList<RouteNode> Routes = new ArrayList<RouteNode>();
		
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
	            		Routes = XMLParser.Routes;
	            		updateList();
	            	}
	            	else
	            	{
	            		TextView_Select_Route_Blank.setText("Error while loading agency list");
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
    	for( int i = 0; i < Routes.size(); i++ )
    	{
    		RouteNode node = Routes.get(i);
    		if( v == node.layout )
    		{
    			Intent result = new Intent();
    			setResult(11, result);
    			result.putExtra("r_tag", node.tag);
    			result.putExtra("r_title", node.title);
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
 		a_tag = intent.getStringExtra("a_tag");
 		a_title = intent.getStringExtra("a_title");
 		
 		layoutInflater = getLayoutInflater();
     		
 		Linear_Select_Route_Blank = (LinearLayout)findViewById(R.id.Linear_Select_Agency_Blank);
 		TextView_Select_Route_Blank = (TextView)findViewById(R.id.TextView_Select_Agency_Blank);
    	
 		Scroll_Route_List = (ScrollView)findViewById(R.id.Scroll_Agency_List);
 		Linear_Route_List = (LinearLayout)findViewById(R.id.Linear_Agency_List);
        
        XMLParser = new Parser(this, a_tag);
        XMLParser.start();
        
        setTitle("Select Route");
    }
    
    private void updateList()
    {
    	Linear_Route_List.removeAllViews();
    	
    	Linear_Select_Route_Blank.setVisibility(View.GONE);

    	for( int i = 0; i < Routes.size(); i++ )
    	{
    		RouteNode node = Routes.get(i);
						
    		node.setLayout( (LinearLayout)layoutInflater.inflate(R.layout.button, null) );
    		node.setButton((TextView)node.layout.findViewById(R.id.TextView_Selector));
			node.layout.setOnClickListener(this);
			Linear_Route_List.addView(node.layout);
    	}
    }

}
