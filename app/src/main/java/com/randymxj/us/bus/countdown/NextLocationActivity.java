package com.randymxj.us.bus.countdown;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by xiaojma on 8/3/2016.
 */
public class NextLocationActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    /* Final */
    private static final String TAG = "@@@ NextLocationAct";
    private static final int MAX_VEHICLE = 100;

    /* Variable */
    private RequestQueue requestQueue;

    private ArrayList<ItemNextVehicle> vehicleList = new ArrayList<>();

    private ProgressDialog progress;

    private GoogleMap mMap;

    private String agency_tag, route_tag, direction_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_next_location);
        mapFragment.getMapAsync(this);

        /* Setup UI Object */
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(MAX_VEHICLE);
        progress.setProgress(0);
        progress.show();

        /* Initialization */
        requestQueue = Volley.newRequestQueue(this);

        /* Load Intent Extra */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            agency_tag = extras.getString(MainActivity.AGENCY_TAG);
            route_tag = extras.getString(MainActivity.ROUTE_TAG);
            direction_tag = extras.getString(MainActivity.DIRECTION_TAG);

            loadNextLocation();
        }

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
    }

    /* Update Address from device location manager */
    private void updateAddress() {
        // Permission check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        if (vehicleList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.vehicle_none_title)
                    .setMessage(R.string.vehicle_none_msg)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ItemNextVehicle vehicle : vehicleList) {
            String away;

            // Calculate distance from current location
            Location loc1 = new Location("");
            loc1.setLatitude(my_latitude);
            loc1.setLongitude(my_longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(Double.valueOf(vehicle.getLat()));
            loc2.setLongitude(Double.valueOf(vehicle.getLon()));

            float distanceInMeters = loc1.distanceTo(loc2);

            if (distanceInMeters > 1000) {
                away = "(" + String.valueOf((int) distanceInMeters / 1000) + " Km away)";
            } else {
                away = "(" + String.valueOf(distanceInMeters) + " m away)";
            }

            // UI Object
            LatLng my = new LatLng(Double.valueOf(vehicle.getLat()), Double.valueOf(vehicle.getLon()));
            Marker myMarker = mMap.addMarker(new MarkerOptions()
                    .position(my)
                    .title(getResources().getString(R.string.vehicle) + " " + vehicle.getId())
                    .snippet(away)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            builder.include(myMarker.getPosition());

            vehicle.setMarker(myMarker);
        }

        LatLngBounds bounds = builder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        mMap.setTrafficEnabled(true);
    }

    /* Load Vehicle Location List */
    private void loadNextLocation() {
        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a="
                + agency_tag + "&r=" + route_tag + "&t=0";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Have the content available in response
                        try {
                            // Parse location from XML
                            parseLocation(response);

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

    /* Process XML to generate a location list */
    private void parseLocation(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equalsIgnoreCase("vehicle")) {
                    String id = parser.getAttributeValue("", "id");
                    String dirTag = parser.getAttributeValue("", "dirTag");
                    String lat = parser.getAttributeValue("", "lat");
                    String lon = parser.getAttributeValue("", "lon");
                    String heading = parser.getAttributeValue("", "heading");

                    if ((dirTag != null) && dirTag.equalsIgnoreCase(direction_tag)) {
                        vehicleList.add(new ItemNextVehicle(id, lat, lon, heading));
                    }
                }
            }
            eventType = parser.next();
        }
    }

    /* Main getter */
    private NextLocationActivity getMain() {
        return this;
    }
}
