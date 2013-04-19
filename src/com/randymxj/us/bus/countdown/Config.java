package com.randymxj.us.bus.countdown;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Config 
{
	private Activity main;
	
	public int refresh_time = 30;
	
	ArrayList<TrackerNode> Trackers = new ArrayList<TrackerNode>();
	
	public Config(Activity m)
	{
		main = m;
	}
	
	public void WriteTracker()
	{
		String path = "tracker.xml";	
		try 
		{
			FileOutputStream fos = main.openFileOutput(path, Context.MODE_PRIVATE);	
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder db = dbf.newDocumentBuilder();  
	        Document doc = db.newDocument();  
	        doc.setXmlVersion("2.0");

	        Element tracker = doc.createElement("tracker"); 
	        doc.appendChild(tracker);
	        
	        for( int i = 0; i < Trackers.size(); i++ )
	        {
	        	TrackerNode node = Trackers.get(i);
	        	
	        	Element item = doc.createElement("item");
	        	tracker.appendChild(item);

	        	// Agency
	        	Element item_a_tag = doc.createElement("a_tag");
	        	item_a_tag.setTextContent(node.a_tag);
	        	item.appendChild(item_a_tag);
	        	
	        	Element item_a_title = doc.createElement("a_title");
	        	item_a_title.setTextContent(node.a_title);
	        	item.appendChild(item_a_title);
	        	
	        	// Route
	        	Element item_r_tag = doc.createElement("r_tag");
	        	item_r_tag.setTextContent(node.r_tag);
	        	item.appendChild(item_r_tag);
	        	
	        	Element item_r_title = doc.createElement("r_title");
	        	item_r_title.setTextContent(node.r_title);
	        	item.appendChild(item_r_title);
	        	
	        	// Direction
	        	Element item_d_tag = doc.createElement("d_tag");
	        	item_d_tag.setTextContent(node.d_tag);
	        	item.appendChild(item_d_tag);
	        	
	        	Element item_d_title = doc.createElement("d_title");
	        	item_d_title.setTextContent(node.d_title);
	        	item.appendChild(item_d_title);
	        	
	        	Element item_d_name = doc.createElement("d_name");
	        	item_d_name.setTextContent(node.d_name);
	        	item.appendChild(item_d_name);
	        	
	        	Element item_d_from = doc.createElement("d_from");
	        	item_d_from.setTextContent(node.d_from);
	        	item.appendChild(item_d_from);
	        	
	        	Element item_d_to = doc.createElement("d_to");
	        	item_d_to.setTextContent(node.d_to);
	        	item.appendChild(item_d_to);
	        	
	        	// Stop
	        	Element item_s_tag = doc.createElement("s_tag");
	        	item_s_tag.setTextContent(node.s_tag);
	        	item.appendChild(item_s_tag);
	        	
	        	Element item_s_title = doc.createElement("s_title");
	        	item_s_title.setTextContent(node.s_title);
	        	item.appendChild(item_s_title);
	        	
	        	// Custom
	        	Element item_c_title = doc.createElement("c_title");
	        	item_c_title.setTextContent(node.c_title);
	        	item.appendChild(item_c_title);
	        	
	        }
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();  
            transformer.setOutputProperty("indent", "yes");  
            transformer.transform( new DOMSource(doc), new StreamResult(fos) );  
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void ReadTracker()
	{
		Trackers.clear();
		String path = "tracker.xml";			
		try 
		{
			FileInputStream fis = main.openFileInput( path );

			if( fis.available() == 0 )
			{
				return;
			}
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder = factory.newDocumentBuilder();  
	        Document document = builder.parse(fis);
			
			Node tracker = document.getDocumentElement();
			NodeList tracker_item = tracker.getChildNodes();          
			
			// <tracker> level parser
			for( int i = 0; i < tracker_item.getLength(); i++ )
			{
				Node element = tracker_item.item(i);  
				if( element.getNodeName().equalsIgnoreCase("item") )
				{
					NodeList item = element.getChildNodes();
					String a_tag = "", a_title = "";
					String r_tag = "", r_title = "";
					String d_tag = "", d_title = "", d_name = "", d_from = "", d_to = "";
					String s_tag = "", s_title = "";
					String c_title = "";
					
					for( int j = 0; j < item.getLength(); j++ )
					{
						Node node = item.item(j);
						if( node.getNodeName().equalsIgnoreCase("a_tag") )
						{
							a_tag = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("a_title") )
						{
							a_title = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("r_tag") )
						{
							r_tag = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("r_title") )
						{
							r_title = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("d_tag") )
						{
							d_tag = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("d_title") )
						{
							d_title = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("d_name") )
						{
							d_name = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("d_from") )
						{
							d_from = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("d_to") )
						{
							d_to = node.getTextContent();
						}
						if( node.getNodeName().equalsIgnoreCase("s_tag") )
						{
							s_tag = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("s_title") )
						{
							s_title = node.getTextContent();
						}
						else if( node.getNodeName().equalsIgnoreCase("c_title") )
						{
							c_title = node.getTextContent();
						}
						
					}
					
					Trackers.add( new TrackerNode(a_tag, a_title,
							 r_tag, r_title,
							 d_tag, d_title, d_name, d_from, d_to,
							 s_tag, s_title,
							 c_title ));
				} 	
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	      
	}
	
	public void WriteConfig()
	{
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		preferences.edit().putInt("refresh_time", refresh_time).commit();
	}
	
	public void ReadConfig()
	{		
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		refresh_time = preferences.getInt("refresh_time", 30);
	}
}
