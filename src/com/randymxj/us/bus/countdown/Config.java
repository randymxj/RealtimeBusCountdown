package com.randymxj.us.bus.countdown;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Config 
{
	private Activity main;
	
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
	}
	
	public void WriteConfig()
	{
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		//preferences.edit().putString("a_tag", a_tag).commit();
	}
	
	public void ReadConfig()
	{		
		SharedPreferences preferences = main.getSharedPreferences("profile", Activity.MODE_PRIVATE);
		
		//a_tag = preferences.getString("a_tag", "");
	}
}
