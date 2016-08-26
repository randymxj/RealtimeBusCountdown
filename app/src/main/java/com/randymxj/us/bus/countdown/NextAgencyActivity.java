package com.randymxj.us.bus.countdown;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;

/**
 * Activity for select a Agency
 */
public class NextAgencyActivity extends AppCompatActivity {

    /* Final */
    private static final String TAG = "@@@ NextAgencyActivity";
    private static final int MAX_AGENCY = 69;

    /* Variable */
    private RequestQueue requestQueue;

    private ArrayList<ItemNextAgency> itemNextAgencies = new ArrayList<>();
    private ArrayList<String> itemNextRegions = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private LinearLayout layout_agency;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_agency);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_agency);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_agency);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRegion();
            }
        });

        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* Setup UI Object */
        layoutInflater = getLayoutInflater();

        layout_agency = (LinearLayout) findViewById(R.id.layout_agency);
        assert layout_agency != null;

        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(MAX_AGENCY);
        progress.setProgress(0);
        progress.show();

        /* Initialization */
        requestQueue = Volley.newRequestQueue(this);

        loadNextAgency();
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

    /* Load NextBus Agency List */
    private void loadNextAgency() {
        // Clean up first
        itemNextAgencies.clear();
        itemNextRegions.clear();
        getLayout().removeAllViews();

        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=agencyList";

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
        int currentAgency = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equalsIgnoreCase("agency")) {
                    String agency_tag = parser.getAttributeValue("", "tag");
                    String agency_title = parser.getAttributeValue("", "title");
                    String short_title = parser.getAttributeValue("", "short_title");
                    String region_title = parser.getAttributeValue("", "regionTitle");

                    itemNextAgencies.add(new ItemNextAgency(this, agency_tag, agency_title, region_title));

                    if ((region_title != null) && !itemNextRegions.contains(region_title)) {
                        itemNextRegions.add(region_title);
                    }

                    currentAgency++;
                    progress.setProgress(currentAgency);
                }
            }
            eventType = parser.next();
        }

        progress.dismiss();

        // Sort region list
        Comparator<String> comparator = new Comparator<String>(){
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        };
        Collections.sort(itemNextRegions, comparator);
    }

    /* Select agency region */
    private void selectRegion() {
        CharSequence[] items = new CharSequence[itemNextRegions.size()];
        for (int i = 0; i < itemNextRegions.size(); i++) {
            String str = itemNextRegions.get(i);
            items[i] = str;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_region);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                String str = itemNextRegions.get(index);
                int currentTail = 0;

                // Move the selected agency to the front
                for (ItemNextAgency agency : itemNextAgencies) {
                    if ((agency.getRegion_title() != null)
                            && agency.getRegion_title().equalsIgnoreCase(str)) {
                        getLayout().removeView(agency.getLayout());
                        getLayout().addView(agency.getLayout(), currentTail);
                        currentTail++;
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /* LayoutInflater getter */
    public LayoutInflater getInflater() {
        return layoutInflater;
    }

    /* Layout getter */
    public LinearLayout getLayout() {
        return  layout_agency;
    }
}
