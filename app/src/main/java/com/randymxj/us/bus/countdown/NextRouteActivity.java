package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Activity for select a Route
 */
public class NextRouteActivity extends AppCompatActivity {

    /* Final */
    private static final String TAG = "@@@ NextRouteActivity";
    private static final int MAX_ROUTE = 100;
    /* Variable */
    private RequestQueue requestQueue;

    private ArrayList<ItemNextRoute> itemNextRoutes = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private LinearLayout layout_route;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_route);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_route);
        assert fab != null;
        fab.setVisibility(View.GONE);

        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* Setup UI Object */
        layoutInflater = getLayoutInflater();

        layout_route = (LinearLayout) findViewById(R.id.layout_route);
        assert layout_route != null;

        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(MAX_ROUTE);
        progress.setProgress(0);
        progress.show();

        /* Initialization */
        requestQueue = Volley.newRequestQueue(this);

        loadNextRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_NEXT) {
            if (resultCode == MainActivity.RESULT_OK) {
                setResult(MainActivity.RESULT_OK, null);
                finish();
            }
        }
    }

    /* Load NextBus Route List */
    private void loadNextRoute() {
        // Load information
        SharedPreferences sharedPref = getSharedPreferences("selector", Activity.MODE_PRIVATE);
        String agency_tag = sharedPref.getString(MainActivity.AGENCY_TAG, "");

        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" + agency_tag;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Have the content available in response
                        try {
                            parseXML(response);
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

    /* Parse */
    private void parseXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));

        int eventType = parser.getEventType();
        int currentRoute = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equalsIgnoreCase("route")) {
                    String route_tag = parser.getAttributeValue("", "tag");
                    String route_title = parser.getAttributeValue("", "title");

                    itemNextRoutes.add(new ItemNextRoute(this, route_tag, route_title));

                    currentRoute++;
                    progress.setProgress(currentRoute);
                }
            }
            eventType = parser.next();
        }

        progress.dismiss();
    }

    /* LayoutInflater getter */
    public LayoutInflater getInflater() {
        return layoutInflater;
    }

    /* Layout getter */
    public LinearLayout getLayout() {
        return  layout_route;
    }
}

