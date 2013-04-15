package com.randymxj.us.bus.countdown;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class Parser extends Thread
{
	/* Error code
	 * 0   : No error
	 * 401 : Timeout
	 * 402 : IO Error
	 * 403 : File not found
	 * 404 : No prediction data
	 */
	public int error_code = -1;
	
	private SelectAgencyActivity main_SelectAgencyActivity = null;
	private SelectRouteActivity main_SelectRouteActivity = null;
	private MainActivity main_MainActivity = null;
	private SelectDirectionActivity main_SelectDirectionActivity = null;
	private SelectStopActivity main_SelectStopActivity = null;

	private int file_size = 0;
	
	private int work_type = -1;
	
	public ArrayList<AgencyNode> Agencies = new ArrayList<AgencyNode>();
	
	public ArrayList<RouteNode> Routes = new ArrayList<RouteNode>();
	private String routeTag;
	
	public ArrayList<StopNode> Stops = new ArrayList<StopNode>();
	public ArrayList<DirectionNode> Directions = new ArrayList<DirectionNode>();
	private String agencyTag;
	
	private String stopTag;
	
	public ArrayList<TimeNode> Times = new ArrayList<TimeNode>();
	
	public Parser(SelectAgencyActivity m) 
	{
		main_SelectAgencyActivity = m;
		work_type = 0;
	}
	
	public Parser(SelectRouteActivity m, String tag) 
	{
		main_SelectRouteActivity = m;
		routeTag = tag;
		work_type = 1;
	}
	
	public Parser(MainActivity m, String a_tag, String r_tag, String s_tag) 
	{
		main_MainActivity = m;
		agencyTag = a_tag;
		routeTag = r_tag;
		stopTag = s_tag;
		work_type = 2;
	}
	
	public Parser(SelectDirectionActivity m, String a_tag, String r_tag) 
	{
		main_SelectDirectionActivity = m;
		agencyTag = a_tag;
		routeTag = r_tag;
		work_type = 3;
	}
	
	public Parser(SelectStopActivity m, String a_tag, String r_tag) 
	{
		main_SelectStopActivity = m;
		agencyTag = a_tag;
		routeTag = r_tag;
		work_type = 4;
	}
	
	public void run() 
	{
		if( work_type == 0 )
		{
			parserConfig();			
			main_SelectAgencyActivity.mHandler.obtainMessage(1).sendToTarget();
		}
		else if( work_type == 1 )
		{
			parserRoute();			
			main_SelectRouteActivity.mHandler.obtainMessage(1).sendToTarget();
		}
		else if( work_type == 2 )
		{
			parserTime();			
			main_MainActivity.mHandler.obtainMessage(1).sendToTarget();
		}
		else if( work_type == 3 )
		{
			parserRouteConfig();			
			main_SelectDirectionActivity.mHandler.obtainMessage(1).sendToTarget();
		}
		else if( work_type == 4 )
		{
			parserRouteConfig();			
			main_SelectStopActivity.mHandler.obtainMessage(1).sendToTarget();
		}
    }
	
	public Document loadXML( String address )
	{
		error_code = 0;
		
		try 
		{
			URL url = new URL(address);
			
			URLConnection connection = url.openConnection();  
	        connection.setConnectTimeout(1000 * 2);  
	        connection.connect();  
	        InputStream in = connection.getInputStream();  
	    	
	        byte xml_file[] = new byte[1024 * 1024];
	        int package_size = 0;
	        file_size = 0;
	        while( package_size > -1 )
	        {
	        	package_size = in.read(xml_file, file_size, 64);
	        	if( package_size > -1 )
	        	{
	        		file_size += package_size;  
	        	}
	        	
	        	if( file_size > ( xml_file.length - 64 ) )
	        	{
	        		error_code = 404;
	        		break;
	        	}
	        }

            InputStream is = new ByteArrayInputStream(xml_file, 0, file_size);                      

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(is);
            
            return document;
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
	}
	
	public void parserConfig()
	{
		Agencies.clear();
		
		String path = "http://webservices.nextbus.com/service/publicXMLFeed?command=agencyList";
		Document document = loadXML( path );
		
		if( document == null )
		{
			return;
		}
		
		Node body = document.getDocumentElement();
		NodeList body_item = body.getChildNodes();
		
		// <agency> level parser
		for( int i = 0; i < body_item.getLength(); i++ )
		{
			Node element = body_item.item(i);  
			if( element.getNodeName().equalsIgnoreCase("agency") )
			{	
				String tag = "";
				String title = "";
				String shortTitle = "";
				String regionTitle = "";
				
				for( int j = 0; j < element.getAttributes().getLength(); j++ )
				{
					Node loop = element.getAttributes().item(j);

					if( loop.getNodeName().toString().equalsIgnoreCase("tag") )
					{
						tag = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("title") )
					{
						title = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("shortTitle") )
					{
						shortTitle = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("regionTitle") )
					{
						regionTitle = loop.getTextContent();
					}
				}
				
				Agencies.add( new AgencyNode(tag, title, shortTitle, regionTitle) );
			}
			      	
		}
	}
	
	public void parserRoute()
	{
		Routes.clear();
		
		String path = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" + routeTag;
		Document document = loadXML( path );
		
		if( document == null )
		{
			return;
		}
		
		Node body = document.getDocumentElement();
		NodeList body_item = body.getChildNodes();
		
		// <route> level parser
		for( int i = 0; i < body_item.getLength(); i++ )
		{
			Node element = body_item.item(i);  
			if( element.getNodeName().equalsIgnoreCase("route") )
			{	
				String tag = "";
				String title = "";
				
				for( int j = 0; j < element.getAttributes().getLength(); j++ )
				{
					Node loop = element.getAttributes().item(j);

					if( loop.getNodeName().toString().equalsIgnoreCase("tag") )
					{
						tag = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("title") )
					{
						title = loop.getTextContent();
					}
				}
				
				Routes.add( new RouteNode(tag, title) );
			}
			      	
		}
	}
	
	public void parserRouteConfig()
	{
		Stops.clear();
		Directions.clear();
		
		String path = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=" + agencyTag + "&r=" + routeTag;
		Document document = loadXML( path );

		if( document == null )
		{
			return;
		}
		
		Node body = document.getDocumentElement();
		NodeList body_item = body.getChildNodes();
		
		Node route = null;
		
		for( int i = 0; i < body_item.getLength(); i++ )
		{
			Node element = body_item.item(i);  
			if( element.getNodeName().equalsIgnoreCase("route") )
			{
				route = element;
				break;
			}
		}
		
		NodeList node_item = route.getChildNodes();
		
		// <route> level parser
		for( int i = 0; i < node_item.getLength(); i++ )
		{
			Node element = node_item.item(i);  
			if( element.getNodeName().equalsIgnoreCase("stop") )
			{	
				String tag = "";
				String title = "";
				String lat = "";
				String lon = "";
				String stopid = "";
				
				for( int j = 0; j < element.getAttributes().getLength(); j++ )
				{
					Node loop = element.getAttributes().item(j);

					if( loop.getNodeName().toString().equalsIgnoreCase("tag") )
					{
						tag = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("title") )
					{
						title = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("lat") )
					{
						lat = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("lon") )
					{
						lon = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("stopid") )
					{
						stopid = loop.getTextContent();
					}
				}
				Stops.add( new StopNode(tag, title, lat, lon, stopid) );
			}
			else if( element.getNodeName().equalsIgnoreCase("direction") )
			{
				String tag = "";
				String title = "";
				String name = "";
				String useforui = "";
				
				for( int j = 0; j < element.getAttributes().getLength(); j++ )
				{
					// Direction Attribute level
					Node loop = element.getAttributes().item(j);

					if( loop.getNodeName().toString().equalsIgnoreCase("tag") )
					{
						tag = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("title") )
					{
						title = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("name") )
					{
						name = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("useForUI") )
					{
						useforui = loop.getTextContent();
					}
				}
				
				DirectionNode direction = new DirectionNode(tag, title, name, useforui);
				
				NodeList stopList = element.getChildNodes(); 
				for( int k = 0; k < stopList.getLength(); k++ )
				{
					// Direction Stop level
					Node stop = stopList.item(k);
					if( stop.getNodeName().equalsIgnoreCase("stop") )
					{
						for( int m = 0; m < stop.getAttributes().getLength(); m++ )
						{
							// Direction Stop Attribute level
							Node stop_tag = stop.getAttributes().item(m);
							if( stop_tag.getNodeName().toString().equalsIgnoreCase("tag") )
							{
								direction.addStop(stop_tag.getTextContent());
							}
						}
					}
				}
				Directions.add( direction );
			}
			else if( element.getNodeName().equalsIgnoreCase("path") )
			{
				/*
				DirectionNode path = new PathNode();
				
				NodeList pointList = element.getChildNodes(); 
				for( int k = 0; k < pointList.getLength(); k++ )
				{
					// Path Point level
					Node stop = pointList.item(k);
					if( stop.getNodeName().equalsIgnoreCase("point") )
					{
						String lat = "";
						String lon = "";
						for( int m = 0; m < stop.getAttributes().getLength(); m++ )
						{
							// Path Point Attribute level
							Node point = stop.getAttributes().item(m);
							if( point.getNodeName().toString().equalsIgnoreCase("lat") )
							{
								lat = point.getTextContent();
							}
							else if( point.getNodeName().toString().equalsIgnoreCase("lon") )
							{
								lon = point.getTextContent();
							}
						}
						
						path.add( new PointNode(lat, lon) );
					}
				}
				Paths.add( path );
				*/
			}
			      	
		}
	}
	
	public void parserTime()
	{
		Routes.clear();
		
		String path = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=" + 
					  agencyTag + "&r=" + routeTag + "&s=" + stopTag;
		Document document = loadXML( path );

		if( document == null )
		{
			return;
		}
		
		Node body = document.getDocumentElement();
		NodeList body_item = body.getChildNodes();
		
		Node predictions = null;
		for( int i = 0; i < body_item.getLength(); i++ )
		{
			Node element = body_item.item(i);  
			
			if( element.getNodeName().equalsIgnoreCase("predictions" ) )
			{
				predictions = body_item.item(i);
			}
		}
		
		Node direction = null;
		for( int i = 0; i < predictions.getChildNodes().getLength(); i++ )
		{
			Node element = predictions.getChildNodes().item(i); 
			if( element.getNodeName().equalsIgnoreCase("direction") )
			{
				direction = predictions.getChildNodes().item(i);
			}
		}
		
		if( direction == null )
		{
			error_code = 404;
			return;
		}
		
		NodeList direction_item = direction.getChildNodes();
		
		// <route> level parser
		for( int i = 0; i < direction_item.getLength(); i++ )
		{
			Node element = direction_item.item(i);  
			if( element.getNodeName().equalsIgnoreCase("prediction") )
			{	
				String epochTime = "";
				String seconds = "";
				String dirTag = "";
				String vehicle = "";
				
				for( int j = 0; j < element.getAttributes().getLength(); j++ )
				{
					Node loop = element.getAttributes().item(j);

					if( loop.getNodeName().toString().equalsIgnoreCase("epochTime") )
					{
						epochTime = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("seconds") )
					{
						seconds = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("dirTag") )
					{
						dirTag = loop.getTextContent();
					}
					else if( loop.getNodeName().toString().equalsIgnoreCase("vehicle") )
					{
						vehicle = loop.getTextContent();
					}
				}
				
				Times.add(new TimeNode(epochTime, seconds, dirTag, vehicle));

			}
			      	
		}
	}

}
