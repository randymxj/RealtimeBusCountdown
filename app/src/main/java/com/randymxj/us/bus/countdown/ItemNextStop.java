package com.randymxj.us.bus.countdown;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Xiaojing on 7/29/2016.
 */
public class ItemNextStop {

    /* Final */
    private static final String TAG = "@@@ ItemNextStop";

    /* Variable */
    private String tag, title, id, lat, lon;
    private Marker marker;

    /* Constructor */

    public ItemNextStop(String tag, String title, String id, String lat, String lon) {
        this.tag = tag;
        this.title = title;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
