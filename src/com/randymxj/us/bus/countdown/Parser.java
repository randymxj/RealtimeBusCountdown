package com.randymxj.us.bus.countdown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
	private MainActivity main = null;
	public RouteList RList;
	public StopList SList;
	public Route inbound, outbound;
	public BusList busList;
	public Agency agency;
	public int task;
	public String bus, stop, agen;

	public Parser(MainActivity m) 
	{
		main = m; 
		
		SList = new StopList();
		RList = new RouteList();
		inbound = new Route("Inbound", SList);
		outbound = new Route("Outbound", SList);
		busList = new BusList();
		agency = new Agency();
		
		task = 0;
	}
	
	public void run() 
	{  
		while( true )
    	{
			try 
    		{
    			Thread.sleep(1000);
    			
    			if( task == 1 )
    			{
    				ParseAgency();
    				main.mHandler.obtainMessage(10).sendToTarget();
    			}
    			else if( task == 2 )
    			{
    				ParseRoutes();
    				main.mHandler.obtainMessage(11).sendToTarget();
    			}
    			else if( task == 3 )
    			{
    				ParseConfig();
    				main.mHandler.obtainMessage(12).sendToTarget();
    			}
    			else if( task == 4 )
    			{
    				ParseBus();
    				ConfigWrite(main.cur_agen, main.cur_bus, main.cur_stop);
    			}
    			
    			task = 0;
    			
    		} catch (InterruptedException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}	
    } 
	
	public void setupTask(int i, String a, String b, String s)
	{
		task = i;
		agen = a;
		bus = b;
		stop = s;
	}
	
	private void ParseAgency()
	{
		try 
		{
			agency.Init();
			
			String xml="http://webservices.nextbus.com/service/publicXMLFeed?command=agencyList";
			URL url = new URL(xml);
			InputStream in = url.openStream();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(in); 

            Element root = document.getDocumentElement();
            NodeList nodelist = root.getChildNodes();
            int size = nodelist.getLength();  
            for( int i = 0; i < size; i++ )
            {
            	Node element = nodelist.item(i);  
            	
            	if( element.getNodeName().equals("agency") )
            	{
            		agency.insert(element.getAttributes().item(1).getTextContent(), element.getAttributes().item(0).getTextContent());
            	}
            }
            		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void ParseRoutes()
	{
		try 
		{
			RList.Init();
			
			String xml="http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" + agen;
			URL url = new URL(xml);
			InputStream in = url.openStream();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(in); 

            Element root = document.getDocumentElement();
            NodeList nodelist = root.getChildNodes();
            int size = nodelist.getLength();  
            for( int i = 0; i < size; i++ )
            {
            	Node element = nodelist.item(i);  
            	
            	if( element.getNodeName().equals("route") )
            	{
            		RList.insert(element.getAttributes().item(1).getTextContent(), element.getAttributes().item(0).getTextContent());
            	}
            }
            		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void ParseConfig()
	{
		try 
		{
			inbound.Init();
			outbound.Init();
			
			String xml="http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=" + agen + "&r=" + bus;
			URL url = new URL(xml);
			InputStream in = url.openStream();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(in); 

            Element root = document.getDocumentElement();
            Node route = root.getFirstChild().getNextSibling();  
            
            NodeList nodelist = route.getChildNodes();
            int size = nodelist.getLength();  
            for( int i = 0; i < size; i++ )
            {
            	Node element = nodelist.item(i);  
            	
            	if( element.getNodeName().equals("stop") )
            	{
            		SList.insert(element.getAttributes().item(1).getTextContent(), element.getAttributes().item(0).getTextContent());
            	}
            	
            	if( element.getNodeName().equals("direction") )
            	{
            		//element.getAttributes().item(1).getTextContent() Title
            		//element.getAttributes().item(2).getTextContent() Name
            		
            		if( inbound.title == "" || element.getAttributes().getNamedItem("title").getTextContent().equals( inbound.title ) )
            		{
            			inbound.setTitle(element.getAttributes().getNamedItem("title").getTextContent());
            			NodeList stops = element.getChildNodes();
                		int numOfStops = stops.getLength()/2;
                		inbound.addSlot(numOfStops, stops);
            		}
            		else if( outbound.title == "" || element.getAttributes().getNamedItem("title").getTextContent().equals( outbound.title ) )
            		{
            			outbound.setTitle(element.getAttributes().getNamedItem("title").getTextContent());
            			NodeList stops = element.getChildNodes();
                		int numOfStops = stops.getLength()/2;
                		outbound.addSlot(numOfStops, stops);
            		}
            	}
            }
            
            Log.i("XXX", "Stop Size: " + String.valueOf(SList.stopTag.size()));
            Log.i("XXX", "Inbound Size: " + String.valueOf(inbound.stopTag.size()));
            Log.i("XXX", "Outbound Size: " + String.valueOf(outbound.stopTag.size()));
            		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void ParseBus()
	{
		try 
		{
			busList.Init();
			
			Log.i("BUS", "Parsing BUS for " + agen + ", " + bus + ", Stop for " + stop);
			
			String xml="http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=" + agen + "&r=" + bus + "&s=" + stop + "&useShortTitles=true";
			URL url = new URL(xml);
			InputStream in = url.openStream();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(in); 

            Element root = document.getDocumentElement();
            
            if(!root.hasChildNodes())
            {
            	Log.i("BUS", "溢出 1");
            	return;
            }
            
            if(root.getFirstChild().getNextSibling() == null)
            {
            	Log.i("BUS", "溢出 2");
            	return;
            }
            
            if(!root.getFirstChild().getNextSibling().hasChildNodes())
            {
            	Log.i("BUS", "溢出 3");
            	return;
            }
            
            if(root.getFirstChild().getNextSibling().getFirstChild().getNextSibling() == null)
            {
            	Log.i("BUS", "溢出 4");
            	return;
            }
            
            if(root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().hasChildNodes())
            {
            	NodeList nodelist = root.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getChildNodes();
            
	            int size = nodelist.getLength();  
	            for( int i = 0; i < size; i++ )
	            {
	            	Node element = nodelist.item(i);  
	            	
	            	if( element.getNodeName().equals("prediction") )
	            	{
	            		busList.insert(element.getAttributes().item(1).getTextContent(), element.getAttributes().item(6).getTextContent());
	            	}
	            }
            
            }
            		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void ConfigWrite(String a, String r, String s)
	{
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder db = dbf.newDocumentBuilder();  
	        Document doc = db.newDocument();  
	        
	        Element root = doc.createElement("USRTBUS"); 
	        doc.appendChild(root);
	        
	        Element config = doc.createElement("Configs");  
	        config.setAttribute("Agent", a);  
	        config.setAttribute("Route", r); 
	        config.setAttribute("Stop", s);  
            root.appendChild(config);  
            
            Transformer t = TransformerFactory.newInstance().newTransformer();  
            t.setOutputProperty("indent", "yes");  
            t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Android/data/busconfig.xml")));  
            		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
	
	public void ConfigRead()
	{
		try 
		{					
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/busconfig.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document document = builder.parse(file); 

            Element root = document.getDocumentElement();
            Node element = root.getFirstChild().getNextSibling();

            if( element.hasAttributes() )
            {
            	main.cur_agen = element.getAttributes().getNamedItem("Agent").getTextContent();
            	main.cur_bus = element.getAttributes().getNamedItem("Route").getTextContent();
            	main.cur_stop = element.getAttributes().getNamedItem("Stop").getTextContent(); 
            }
            
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
