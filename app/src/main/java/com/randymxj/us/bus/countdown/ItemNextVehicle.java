package com.randymxj.us.bus.countdown;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by xiaojma on 8/3/2016.
 */
public class ItemNextVehicle {

    /* Final */
    private static final String TAG = "@@@ ItemNextVehicle";

    /* Variable */
    private String id, lat, lon, heading;
    private Marker marker;

    /* Constructor */

    public ItemNextVehicle(String id, String lat, String lon, String heading) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.heading = heading;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
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

    public String getHeading() {
        return heading;
    }
}
