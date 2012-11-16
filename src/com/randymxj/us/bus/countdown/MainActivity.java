package com.randymxj.us.bus.countdown;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.randymxj.us.bus.countdown.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.ads.*;

import android.widget.LinearLayout;


public class MainActivity extends Activity 
{
	public Button BLabel_Debug, BLabel_Route, BLabel_Inbound, BLabel_Outbound, 
				  BLabel_BUS0, BLabel_BUS1, BLabel_BUS2;
			
	public Parser XMLParser;
	
	public String cur_bus, cur_stop, cur_agen;
	
    private Spinner spinner_routes, spinner_inbound, spinner_outbound, spinner_agencies;
    private ArrayAdapter<String> adapter_routes, adapter_inbound, adapter_outbound, adapter_agencies;
    
    public HeartBeat heart;
    
	public Handler mHandler = new Handler() 
	{  
		public void handleMessage (Message msg) 
        {  
            switch(msg.what) {  
            case 0:  //Update XML
            	XMLUpdate();
            	break;  
            	
            case 1:  //Heartbeat
            	heartbeatUpdate();
            	break;
            	
            case 10: //update agency list
            	updateAgencyList();
            	
            case 11: //update route list
            	updateRouteList();
            	break;
            	
            case 12: //update stop list
            	updateStopList();
            	break;

            default:
            	break;
            }  
        }  
    }; 

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        spinner_routes=(Spinner)findViewById(R.id.spinner_routes);
        spinner_inbound=(Spinner)findViewById(R.id.spinner_inbound);
        spinner_outbound=(Spinner)findViewById(R.id.spinner_outbound);
        spinner_agencies=(Spinner)findViewById(R.id.spinner_agencies);
        
        BLabel_Debug = (Button)findViewById(R.id.BLabel_Debug);
        BLabel_Route = (Button)findViewById(R.id.BLabel_Route);
        BLabel_Inbound = (Button)findViewById(R.id.BLabel_Inbound);
        BLabel_Outbound = (Button)findViewById(R.id.BLabel_Outbound);
        BLabel_BUS0 = (Button)findViewById(R.id.BLabel_BUS0);
        BLabel_BUS1 = (Button)findViewById(R.id.BLabel_BUS1);
        BLabel_BUS2 = (Button)findViewById(R.id.BLabel_BUS2);
        
        cur_bus = "";
        cur_stop = ""; 
        cur_agen = "";
        
        XMLParser = new Parser(this);
        XMLParser.start();
        XMLParser.ConfigRead();
        XMLParser.setupTask(1, cur_agen, cur_bus, cur_stop);
        
        heart = new HeartBeat(this);
        heart.start();
        heart.work = true;
        
        //adRequest.addTestDevice("C49865D50D17B55BDCDDB2A8CE265582");
        //C49865D50D17B55BDCDDB2A8CE265582
        //B420D6FD3E2D1F479CDFA8F51152B9E5
        
        //Spinner Agencies
        spinner_agencies.setVisibility(View.VISIBLE);
        adapter_agencies = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, XMLParser.agency.agencyTitle);
        adapter_agencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_agencies.setAdapter(adapter_agencies); 
        
        spinner_agencies.setOnItemSelectedListener
        (
        		new Spinner.OnItemSelectedListener()
        		{

                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
                        {
                        	if( arg2 > 0 )
                        	{
	                            // TODO Auto-generated method stub
	                            BLabel_Debug.setText("Selected Agency " + XMLParser.agency.agencyTitle.get(arg2-1));
	                            arg0.setVisibility(View.VISIBLE);
	                                
	                            cur_agen = XMLParser.agency.agencyTag.get(arg2-1);
	                                
	                            XMLParser.setupTask(2, cur_agen, cur_bus, cur_stop);
                        	}

                        	spinner_routes.setSelection(0);
                        	spinner_inbound.setSelection(0);
                        	spinner_outbound.setSelection(0);

                        	XMLParser.busList.Init();
                        	
                        	spinner_routes.setEnabled(false);
                    		spinner_inbound.setEnabled(false);
                    		spinner_outbound.setEnabled(false);
                        }

						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
                
        		}
        );
        
        //Spinner Routes
        spinner_routes.setVisibility(View.VISIBLE);
        adapter_routes = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, XMLParser.RList.routeTitle);
        adapter_routes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_routes.setAdapter(adapter_routes); 
        
        spinner_routes.setOnItemSelectedListener
        (
        		new Spinner.OnItemSelectedListener()
        		{

                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
                        {
                        	if( arg2 > 0 )
                        	{
                        		// TODO Auto-generated method stub
                                BLabel_Debug.setText("Selected Route " + XMLParser.RList.routeTitle.get(arg2-1));
                                BLabel_Outbound.setBackgroundColor(0xFF1BA1E2);
                                BLabel_Inbound.setBackgroundColor(0xFF1BA1E2);
                                //XMLParser.RList.RouteTitle[arg2] is the title of the route
                                arg0.setVisibility(View.VISIBLE);
                                    
                                cur_bus = XMLParser.RList.routeTag.get(arg2-1);
                                    
                                XMLParser.setupTask(3, cur_agen, cur_bus, cur_stop);
                        	}
                        	
                        	spinner_inbound.setSelection(0);
                        	spinner_outbound.setSelection(0);
                        	
                        	XMLParser.busList.Init();
                        	
                        	spinner_inbound.setEnabled(false);
                    		spinner_outbound.setEnabled(false);
                        }

						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
                
        		}
        );
        
        //Spinner Inbound
        spinner_inbound.setVisibility(View.VISIBLE);
        adapter_inbound = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, XMLParser.inbound.stopTitle);
        adapter_inbound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_inbound.setAdapter(adapter_inbound); 
        
        spinner_inbound.setOnItemSelectedListener
        (
        		new Spinner.OnItemSelectedListener()
        		{

                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
                        {
                        	if( arg2 > 0 )
                        	{
                        		// TODO Auto-generated method stub
                                BLabel_Debug.setText("Selected Direction 1 - Stop " + XMLParser.inbound.stopTag.get(arg2 - 1 ));
                                BLabel_Inbound.setBackgroundColor(0xFF339933);
                                BLabel_Outbound.setBackgroundColor(0xFF1BA1E2);
                                arg0.setVisibility(View.VISIBLE);
                                
                                cur_stop = XMLParser.inbound.stopTag.get(arg2 - 1);
                                    
                                XMLParser.setupTask(4, cur_agen, cur_bus, cur_stop);
                        	}
                        }

						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
                
        		}
        );
        
        //Spinner Outbound
        spinner_outbound.setVisibility(View.VISIBLE);
        adapter_outbound = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, XMLParser.outbound.stopTitle);
        adapter_outbound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_outbound.setAdapter(adapter_outbound); 
        
        spinner_outbound.setOnItemSelectedListener
        (
        		new Spinner.OnItemSelectedListener()
        		{

                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
                        {     
                        	if( arg2 > 0 )
                        	{
                        		// TODO Auto-generated method stub
                                BLabel_Debug.setText("Selected Direction 2 - Stop " + XMLParser.outbound.stopTag.get(arg2 - 1));
                                BLabel_Outbound.setBackgroundColor(0xFF339933);
                                BLabel_Inbound.setBackgroundColor(0xFF1BA1E2);
                                arg0.setVisibility(View.VISIBLE);
                                    
                                cur_stop = XMLParser.outbound.stopTag.get(arg2 - 1);
                                    
                                XMLParser.setupTask(4, cur_agen, cur_bus, cur_stop);
                        	}
                        }

						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
                
        		}
        );
        
        BLabel_Debug.setOnClickListener(new OnClickListener(){
        	 
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
 
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void updateAgencyList()
    {
    	adapter_agencies.clear();
    
    	adapter_agencies.add( "Please select public transportation agency" );
    	
    	for( int i = 0; i < XMLParser.agency.agencyTag.size(); i++ )
        {
        	adapter_agencies.add(XMLParser.agency.agencyTitle.get(i));      	
        	
        	if( XMLParser.agency.agencyTag.get(i).equalsIgnoreCase(cur_agen) )
        	{
        		spinner_agencies.setSelection(i+1);
        	}
        }
    	
    }
    
    public void updateRouteList()
    {
    	adapter_routes.clear();
    	
    	adapter_routes.add( "Please select route" );
    	
    	for( int i = 0; i < XMLParser.RList.routeTag.size(); i++ )
        {
        	adapter_routes.add(XMLParser.RList.routeTitle.get(i));
        	
        	if( XMLParser.RList.routeTag.get(i).equalsIgnoreCase(cur_bus) )
        	{
        		spinner_routes.setSelection(i+1);
        	}
        }
    	
    	spinner_routes.setEnabled(true);
    }
    
    public void updateStopList()
    {
    	adapter_inbound.clear();
        adapter_outbound.clear();
        
        adapter_inbound.add( "Please select stop" );
        adapter_outbound.add( "Please select stop" );
        
        BLabel_Inbound.setText("Direction 1: " + XMLParser.inbound.title);
        BLabel_Outbound.setText("Direction 2: " + XMLParser.outbound.title);
        
        for( int i = 0; i < XMLParser.inbound.stopTag.size(); i++ )
        {
        	adapter_inbound.add(XMLParser.inbound.stopTitle.get(i));
        	
        	if( XMLParser.inbound.stopTag.get(i).equalsIgnoreCase(cur_stop) )
        	{
        		spinner_inbound.setSelection(i+1);
        	}
        }
        
        for( int i = 0; i < XMLParser.outbound.stopTag.size(); i++ )
        {
        	adapter_outbound.add(XMLParser.outbound.stopTitle.get(i));
        	
        	if( XMLParser.outbound.stopTag.get(i).equalsIgnoreCase(cur_stop) )
        	{
        		spinner_outbound.setSelection(i+1);
        	}
        }
        
		spinner_inbound.setEnabled(true);
		spinner_outbound.setEnabled(true);
    }
    
    public void heartbeatUpdate()
    {
    	BLabel_BUS0.setText("--");
    	BLabel_BUS0.setBackgroundColor(0xff00ABA9);
    	BLabel_BUS1.setText("--");
    	BLabel_BUS1.setBackgroundColor(0xff00ABA9);
    	BLabel_BUS2.setText("--");
    	BLabel_BUS2.setBackgroundColor(0xff00ABA9);
    	
    	if( XMLParser.busList.count > 0 )
    	{
    		BLabel_BUS0.setText(String.valueOf(XMLParser.busList.time[0]/60) + " m " + String.valueOf(XMLParser.busList.time[0]%60) + " s ");
    		XMLParser.busList.time[0]--;
    		BLabel_BUS0.setBackgroundColor(0xff339933);
    	}
    	if( XMLParser.busList.count > 1 )
    	{
    		BLabel_BUS1.setText(String.valueOf(XMLParser.busList.time[1]/60) + " m " + String.valueOf(XMLParser.busList.time[1]%60) + " s ");
    		XMLParser.busList.time[1]--;
    		BLabel_BUS1.setBackgroundColor(0xff339933);
    	}
    	if( XMLParser.busList.count > 2 )
    	{
    		BLabel_BUS2.setText(String.valueOf(XMLParser.busList.time[2]/60) + " m " + String.valueOf(XMLParser.busList.time[2]%60) + " s ");
    		XMLParser.busList.time[2]--;
    		BLabel_BUS2.setBackgroundColor(0xff339933);
    	}
    }
    
    public void XMLUpdate()
    {
    	if( cur_agen != "" && cur_bus != "" )
    		XMLParser.setupTask(4, cur_agen, cur_bus, cur_stop);
    }
}
