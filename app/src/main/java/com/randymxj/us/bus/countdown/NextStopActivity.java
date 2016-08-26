package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class NextStopActivity extends AppCompatActivity implements
        OnInfoWindowClickListener,
        OnMapReadyCallback {

    /* Final */
    private static final String TAG = "@@@ NextStopActivity";
    private static final int MAX_STOP = 100;

    /* Variable */
    private RequestQueue requestQueue;

    private ArrayList<String> tagList = new ArrayList<>();
    private ArrayList<ItemNextStop> stopList = new ArrayList<>();

    private String agency_tag, route_tag, direction_tag;

    private ProgressDialog progress;

    private GoogleMap mMap;

    private double latMin, latMax, lonMin, lonMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_next_stop);
        mapFragment.getMapAsync(this);

        /* Setup UI Object */
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(MAX_STOP);
        progress.setProgress(0);
        progress.show();

        /* Initialization */
        requestQueue = Volley.newRequestQueue(this);

        // Load information
        SharedPreferences sharedPref = getSharedPreferences("selector", Activity.MODE_PRIVATE);
        agency_tag = sharedPref.getString(MainActivity.AGENCY_TAG, "");
        route_tag = sharedPref.getString(MainActivity.ROUTE_TAG, "");
        direction_tag = sharedPref.getString(MainActivity.DIRECTION_TAG, "");

        loadNextStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(this);

        // Customized Info Window Adapter
        mMap.setInfoWindowAdapter(new MapInfoAdapter(this));
    }

    /* Update Address from device location manager */
    private void updateAddress() {
        // Permission check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        if (stopList.isEmpty()) {
            return;
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        double my_latitude = lastKnownLocation.getLatitude();
        double my_longitude = lastKnownLocation.getLongitude();
        float my_accuracy = lastKnownLocation.getAccuracy();

        // Markers, polyline and Bounds
        PolylineOptions rectOptions = new PolylineOptions().color(ContextCompat.getColor(this, R.color.colorControlHighlight));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        float nearestDistance = -1;
        Marker nearestMarker = null;
        for (ItemNextStop stop : stopList) {

            String away;

            // Calculate distance from current location
            Location loc1 = new Location("");
            loc1.setLatitude(my_latitude);
            loc1.setLongitude(my_longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(Double.valueOf(stop.getLat()));
            loc2.setLongitude(Double.valueOf(stop.getLon()));

            float distanceInMeters = loc1.distanceTo(loc2);

            if (distanceInMeters > 1000) {
                away = "(" + String.valueOf((int) distanceInMeters / 1000) + " Km away)";
            } else {
                away = "(" + String.valueOf(distanceInMeters) + " m away)";
            }

            // UI Object
            LatLng my = new LatLng(Double.valueOf(stop.getLat()), Double.valueOf(stop.getLon()));
            Marker myMarker = mMap.addMarker(new MarkerOptions()
                    .position(my)
                    .title(stop.getTitle())
                    .snippet(away + "\nClick here to select")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            builder.include(myMarker.getPosition());

            rectOptions.add(my);

            stop.setMarker(myMarker);

            // Nearest
            if ((nearestDistance < 0) || (distanceInMeters < nearestDistance)) {
                nearestDistance = distanceInMeters;
                nearestMarker = myMarker;
            }
        }

        LatLngBounds bounds = builder.build();
        Polyline polyline = mMap.addPolyline(rectOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        if (nearestMarker != null) {
            nearestMarker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(nearestMarker.getPosition()));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // User select

        for (ItemNextStop stop : stopList) {
            if (stop.getMarker().equals(marker)) {
                // Load selector data
                SharedPreferences sharedPref = getMain().getSharedPreferences("selector", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString(MainActivity.STOP_TAG, stop.getTag());
                editor.putString(MainActivity.STOP_TITLE, stop.getTitle());
                editor.putString(MainActivity.STOP_ID, stop.getId());
                editor.putString(MainActivity.STOP_LAT, stop.getLat());
                editor.putString(MainActivity.STOP_LON, stop.getLon());

                editor.commit();

                setResult(MainActivity.RESULT_OK, null);
                finish();
            }
        }
    }

    /* Load NextBus Stop List */
    private void loadNextStop() {
        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a="
                + agency_tag + "&r=" + route_tag;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Have the content available in response
                        try {
                            // Parse stop and tag from XML
                            parseCandidate(response);
                            parseStop(response);

                            // Merge list to eliminate not needed stops
                            ArrayList<ItemNextStop> newList = new ArrayList<>();
                            for (String tag : tagList) {
                                for (ItemNextStop stop : stopList) {
                                    if ((stop.getTag() != null)
                                            && stop.getTag().equalsIgnoreCase(tag)) {
                                        newList.add(stop);
                                        break;
                                    }
                                }
                            }

                            stopList = newList;

                            // Update UI object
                            updateAddress();
                            progress.dismiss();
                        } catch (XmlPullParserException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    /* Process XML to generate a candidate list */
    private void parseCandidate(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            if ((eventType == XmlPullParser.START_TAG) && (name.equalsIgnoreCase("route"))) {
                latMin = Double.valueOf(parser.getAttributeValue("", "latMin"));
                latMax = Double.valueOf(parser.getAttributeValue("", "latMax"));
                lonMin = Double.valueOf(parser.getAttributeValue("", "lonMin"));
                lonMax = Double.valueOf(parser.getAttributeValue("", "lonMax"));
            }
            else if ((eventType == XmlPullParser.START_TAG) && (name.equalsIgnoreCase("direction"))) {
                break;
            }
            else if ((eventType == XmlPullParser.START_TAG) && (name.equalsIgnoreCase("stop"))) {
                String tag = parser.getAttributeValue("", "tag");
                String title = parser.getAttributeValue("", "title");
                String lat = parser.getAttributeValue("", "lat");
                String lon = parser.getAttributeValue("", "lon");
                String stopId = parser.getAttributeValue("", "stopId");

                // Insert into list
                stopList.add(new ItemNextStop(tag, title, stopId, lat, lon));
            }

            eventType = parser.next();
        }
    }

    /* Parse XML to generate tag list */
    private void parseStop(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));

        int eventType = parser.getEventType();
        boolean isDirection = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            if ((eventType == XmlPullParser.START_TAG) && (name.equalsIgnoreCase("direction"))) {
                String tag = parser.getAttributeValue("", "tag");
                if (tag.equalsIgnoreCase(direction_tag)) {
                    isDirection = true;
                }
            }
            else if ((eventType == XmlPullParser.END_TAG) && (name.equalsIgnoreCase("direction"))) {
                isDirection = false;
            }
            else if ((eventType == XmlPullParser.START_TAG) && (name.equalsIgnoreCase("stop"))) {
                if (isDirection) {
                    String tag = parser.getAttributeValue("", "tag");
                    tagList.add(tag);
                }
            }

            eventType = parser.next();
        }
    }

    /* Main getter */
    private NextStopActivity getMain() {
        return this;
    }
}
