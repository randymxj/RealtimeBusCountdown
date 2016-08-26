package com.randymxj.us.bus.countdown;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Class of Tracker Item
 */
public class ItemNextTracker {

    /* Final */
    private static final String TAG = "@@@ ItemTracker";

    /* Variable */
    private MainActivity main;
    private String title;
    private String agency_tag, agency_title, region_title;
    private String route_tag, route_title;
    private String direction_tag, direction_title, direction_name;
    private String stop_tag, stop_title, stop_lat, stop_lon;

    private LinearLayout layout, view_counter;
    private TextView tv_title, tv_route, tv_stop, tv_direction;

    private RequestQueue requestQueue;

    private ArrayList<ItemNextCounter> itemNextCounters = new ArrayList<>();

    /* Constructor */
    public ItemNextTracker(MainActivity main, String title,
                           String agency_tag, String agency_title, String region_title,
                           String route_tag, String route_title,
                           String direction_tag, String direction_title, String direction_name,
                           String stop_tag, String stop_title, String stop_lat, String stop_lon) {
        this.main = main;
        this.title = title;
        this.agency_tag = agency_tag;
        this.agency_title = agency_title;
        this.region_title = region_title;
        this.route_tag = route_tag;
        this.route_title = route_title;
        this.direction_tag = direction_tag;
        this.direction_title = direction_title;
        this.direction_name = direction_name;
        this.stop_tag = stop_tag;
        this.stop_title = stop_title;
        this.stop_lat = stop_lat;
        this.stop_lon = stop_lon;
    }

    public ItemNextTracker(MainActivity main, JSONObject trackerObj) {

        this.main = main;

        try {
            title = trackerObj.getString(MainActivity.TITLE);
            agency_tag = trackerObj.getString(MainActivity.AGENCY_TAG);
            agency_title = trackerObj.getString(MainActivity.AGENCY_TITLE);
            region_title = trackerObj.getString(MainActivity.REGION_TITLE);
            route_tag = trackerObj.getString(MainActivity.ROUTE_TAG);
            route_title = trackerObj.getString(MainActivity.ROUTE_TITLE);
            direction_tag = trackerObj.getString(MainActivity.DIRECTION_TAG);
            direction_title = trackerObj.getString(MainActivity.DIRECTION_TITLE);
            direction_name = trackerObj.getString(MainActivity.DIRECTION_NAME);
            stop_tag = trackerObj.getString(MainActivity.STOP_TAG);
            stop_title = trackerObj.getString(MainActivity.STOP_TITLE);
            stop_lat = trackerObj.getString(MainActivity.STOP_LAT);
            stop_lon = trackerObj.getString(MainActivity.STOP_LON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Setup UI object
        layout = (LinearLayout) main.getInflater().inflate(R.layout.card_tracker, null);
        view_counter = (LinearLayout) layout.findViewById(R.id.card_counter_view);
        tv_title = (TextView) layout.findViewById(R.id.tv_item_title);
        tv_route = (TextView) layout.findViewById(R.id.tv_item_route);
        tv_stop = (TextView) layout.findViewById(R.id.tv_item_stop);
        tv_direction = (TextView) layout.findViewById(R.id.tv_item_direction);

        Button btn_refresh = (Button) layout.findViewById(R.id.btn_option_refresh);
        Button btn_more = (Button) layout.findViewById(R.id.btn_option_more);

        // Data
        String route = "Route: " + route_title;
        String stop = "Stop: " + stop_title;
        String direction = "Direction: " + direction_name + " - " + direction_title;

        tv_title.setText(title);
        tv_route.setText(route);
        tv_stop.setText(stop);
        tv_direction.setText(direction);

        // Listener
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        // Button
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(getMain().getLayout(), "Refreshing", Snackbar.LENGTH_LONG);

                snackbar.show();

                loadNextPrediction();
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int schedule = 0;
                final int path = 1;
                final int location = 2;
                final int rename = 3;
                final int remove = 4;
                final CharSequence[] items = {
                        getMain().getResources().getString(R.string.schedule),
                        getMain().getResources().getString(R.string.path),
                        getMain().getResources().getString(R.string.location),
                        getMain().getResources().getString(R.string.rename),
                        getMain().getResources().getString(R.string.remove)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getMain());
                builder.setTitle(R.string.more);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == schedule) {
                            menu_schedule();
                        }
                        else if (item == path) {
                            menu_path();
                        }
                        else if (item == location) {
                            menu_location();
                        }
                        else if (item == rename) {
                            menu_rename();
                        }
                        else if (item == remove) {
                            menu_remove();
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        /* Load data */
        requestQueue = Volley.newRequestQueue(getMain());

        loadNextPrediction();
    }

    public JSONObject getJsonObject() {
        JSONObject trackerObj = new JSONObject();

        try {
            trackerObj.put(MainActivity.TITLE, title);
            trackerObj.put(MainActivity.AGENCY_TAG, agency_tag);
            trackerObj.put(MainActivity.AGENCY_TITLE, agency_title);
            trackerObj.put(MainActivity.REGION_TITLE, region_title);
            trackerObj.put(MainActivity.ROUTE_TAG, route_tag);
            trackerObj.put(MainActivity.ROUTE_TITLE, route_title);
            trackerObj.put(MainActivity.DIRECTION_TAG, direction_tag);
            trackerObj.put(MainActivity.DIRECTION_TITLE, direction_title);
            trackerObj.put(MainActivity.DIRECTION_NAME, direction_name);
            trackerObj.put(MainActivity.STOP_TAG, stop_tag);
            trackerObj.put(MainActivity.STOP_TITLE, stop_title);
            trackerObj.put(MainActivity.STOP_LAT, stop_lat);
            trackerObj.put(MainActivity.STOP_LON, stop_lon);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trackerObj;
    }

    /* Load NextBus Prediction */
    public void loadNextPrediction() {
        String url ="http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a="
                   + agency_tag + "&r=" + route_tag + "&s=" + stop_tag;

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

        // Clear the list
        itemNextCounters.clear();

        // Clear the view
        view_counter.removeAllViews();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(response));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equalsIgnoreCase("prediction")) {
                    String epochTime = parser.getAttributeValue("", "epochTime");
                    String seconds = parser.getAttributeValue("", "seconds");
                    String minutes = parser.getAttributeValue("", "minutes");
                    String isDeparture = parser.getAttributeValue("", "isDeparture");
                    String affectedByLayover = parser.getAttributeValue("", "affectedByLayover");
                    String vehicle = parser.getAttributeValue("", "vehicle");
                    String block = parser.getAttributeValue("", "block");
                    String tripTag = parser.getAttributeValue("", "tripTag");

                    ItemNextCounter counter = new ItemNextCounter(getMain(), seconds, vehicle);

                    itemNextCounters.add(counter);
                    view_counter.addView(counter.getLayout());
                }
            }
            eventType = parser.next();
        }
    }

    /* Counter */
    public void tick() {
        for (ItemNextCounter counter : itemNextCounters) {
            counter.updateUI();
        }
    }

    /* Menu handler */
    private void menu_schedule() {
        Intent intent = new Intent(getMain(), NextScheduleActivity.class);

        intent.putExtra(MainActivity.AGENCY_TAG, agency_tag);
        intent.putExtra(MainActivity.ROUTE_TAG, route_tag);
        intent.putExtra(MainActivity.STOP_TAG, stop_tag);

        getMain().startActivity(intent);
    }

    private void menu_path() {
        Intent intent = new Intent(getMain(), NextPathActivity.class);

        intent.putExtra(MainActivity.AGENCY_TAG, agency_tag);
        intent.putExtra(MainActivity.ROUTE_TAG, route_tag);
        intent.putExtra(MainActivity.DIRECTION_TAG, direction_tag);

        getMain().startActivity(intent);
    }

    private void menu_location() {
        Intent intent = new Intent(getMain(), NextLocationActivity.class);

        intent.putExtra(MainActivity.AGENCY_TAG, agency_tag);
        intent.putExtra(MainActivity.ROUTE_TAG, route_tag);
        intent.putExtra(MainActivity.DIRECTION_TAG, direction_tag);

        getMain().startActivity(intent);
    }

    private void menu_rename() {
        LayoutInflater layoutInflater = getMain().getInflater();

        View mView = layoutInflater.inflate(R.layout.dialog_rename, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getMain());
        alertDialogBuilderUserInput.setView(mView);

        final EditText edit_rename = (EditText) mView.findViewById(R.id.edit_rename);
        alertDialogBuilderUserInput
                .setTitle(R.string.rename_title)
                .setCancelable(false)
                .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String str = edit_rename.getText().toString();
                        getTracker().setTitle(str);
                        getMain().writeTracker();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.dismiss();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    private void menu_remove() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getMain());

        builder.setTitle(R.string.remove_title)
                .setMessage(R.string.remove_message)
                .setCancelable(false)
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getMain().removeTracker(getTracker());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
    }

    /* Self getter */
    private ItemNextTracker getTracker() {
        return this;
    }

    /* Layout getter */
    public LinearLayout getLayout() {
        return layout;
    }

    /* Main Activity getter */
    private MainActivity getMain() {
        return main;
    }
}
