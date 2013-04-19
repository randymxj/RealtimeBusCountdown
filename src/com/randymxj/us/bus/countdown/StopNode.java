package com.randymxj.us.bus.countdown;

import com.google.android.gms.maps.model.Marker;

public class StopNode 
{
	public String tag = "";
	public String title = "";
	public String lat = "";
	public String lon = "";
	public String stopid = "";
	
	public Marker marker;
	
	StopNode( String inTag, String inTitle, String inLat, String inLon, String inStopid )
	{
		tag = inTag;
		title = inTitle;
		lat = inLat;
		lon = inLon;
		stopid = inStopid;
	}
	
	public void setMarker( Marker m )
	{
		marker = m;
	}
	
}
