package com.randymxj.us.bus.countdown;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Activity for display the Schedule
 */
public class NextScheduleActivity extends AppCompatActivity {

    /* Final */
    private static final String TAG = "@@@ NextScheduleAct";
    private static final int MAX_SCHEDULE = 100;
    /* Variable */
    private RequestQueue requestQueue;

    private ArrayList<Route> itemNextRoutes = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private LinearLayout layout_schedule;
    private ProgressDialog progress;

    private String agency_tag, route_tag, stop_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_schedule);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_schedule);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRoute();
            }
        });

        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /* Setup UI Object */
        layoutInflater = getLayoutInflater();

        layout_schedule = (LinearLayout) findViewById(R.id.layout_schedule);
        assert layout_schedule != null;

        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(MAX_SCHEDULE);
        progress.setProgress(0);
        progress.show();

        /* Initialization */
        requestQueue = Volley.newRequestQueue(this);

        /* Load Intent Extra */
        Bundle extras = getIntent().getExtras();

        if(extras != null) {

            agency_tag = extras.getString(MainActivity.AGENCY_TAG);
            route_tag = extras.getString(MainActivity.ROUTE_TAG);
            stop_tag = extras.getString(MainActivity.STOP_TAG);

            loadNextSchedule();
        }
    }

    /* Load NextBus Direction List */
    private void loadNextSchedule() {
        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=schedule&" +
                "a=" + agency_tag + "&r=" + route_tag;

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

        Route currentRoute = null;
        boolean isHeader = false;

        int eventType = parser.getEventType();
        int currentSchedule = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equalsIgnoreCase("route")) {
                    String scheduleClass = parser.getAttributeValue("", "scheduleClass");
                    String serviceClass = parser.getAttributeValue("", "serviceClass");
                    String direction = parser.getAttributeValue("", "direction");

                    currentRoute = new Route(scheduleClass, serviceClass, direction);
                    itemNextRoutes.add(currentRoute);
                }
                else if (name.equalsIgnoreCase("header")) {
                    isHeader = true;
                }
                else if (name.equalsIgnoreCase("stop")) {
                    String tag = parser.getAttributeValue("", "tag");
                    parser.next();
                    String text = parser.getText();

                    if (currentRoute != null) {
                        if (isHeader) {
                            currentRoute.itemNextStops.add(new Stop(tag, text));
                        } else {
                            currentRoute.addSchedule(tag, text);
                        }
                    }
                }

                currentSchedule++;
                progress.setProgress(currentSchedule);
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (name.equalsIgnoreCase("header")) {
                    isHeader = false;
                }
            }
            eventType = parser.next();
        }

        progress.dismiss();

        selectRoute();
    }

    /* Select Schedule Route */
    private void selectRoute() {
        CharSequence[] items = new CharSequence[itemNextRoutes.size()];
        for (int i = 0; i < itemNextRoutes.size(); i++) {
            Route r = itemNextRoutes.get(i);
            String str = "(" + r.scheduleClass + ") " + r.serviceClass + " - " + r.direction;
            items[i] = str;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_route);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                Route r = itemNextRoutes.get(index);
                if (r != null) {
                    selectStop(r);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /* Select Stop */
    private void selectStop(Route r) {
        final Route route = r;

        CharSequence[] items = new CharSequence[r.itemNextStops.size()];
        for (int i = 0; i < r.itemNextStops.size(); i++) {
            Stop s = r.itemNextStops.get(i);
            String str = s.text;
            items[i] = str;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_stop);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                Stop s = route.itemNextStops.get(index);

                layout_schedule.removeAllViews();

                for (String str : s.itemNextSchedules) {
                    LinearLayout card_layout = (LinearLayout) getInflater().inflate(R.layout.card_schedule, null);
                    TextView tv_title = (TextView) card_layout.findViewById(R.id.tv_item_title);
                    tv_title.setText(str);
                    layout_schedule.addView(card_layout);
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
        return  layout_schedule;
    }

    /* Internal Class route */
    class Route {
        public String scheduleClass, serviceClass, direction;
        public ArrayList<Stop> itemNextStops = new ArrayList<>();

        public Route(String scheduleClass, String serviceClass, String direction) {
            this.scheduleClass = scheduleClass;
            this.serviceClass = serviceClass;
            this.direction = direction;
        }

        public void addSchedule(String tag, String text) {
            for (Stop stop : itemNextStops) {
                if (stop.tag.equalsIgnoreCase(tag)) {
                    stop.itemNextSchedules.add(text);
                }
            }
        }
    }

    /* Internal Class stop */
    class Stop {
        public String tag, text;
        public ArrayList<String> itemNextSchedules = new ArrayList<>();

        public Stop(String tag, String text) {
            this.tag = tag;
            this.text = text;
        }
    }
}

