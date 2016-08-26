package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Main activity class
 */
public class MainActivity extends AppCompatActivity {

    /* Final */
    private static final String TAG = "@@@ MainActivity";

    public static final int REQUEST_NEXT = 700;
    public static final int RESULT_OK = 701;

    // Shared Preferences
    public static final String TITLE = "TITLE";
    public static final String AGENCY_TAG = "AGENCY_TAG";
    public static final String AGENCY_TITLE = "AGENCY_TITLE";
    public static final String REGION_TITLE = "REGION_TITLE";
    public static final String ROUTE_TAG = "ROUTE_TAG";
    public static final String ROUTE_TITLE = "ROUTE_TITLE";
    public static final String DIRECTION_TAG = "DIRECTION_TAG";
    public static final String DIRECTION_TITLE = "DIRECTION_TITLE";
    public static final String DIRECTION_NAME = "DIRECTION_NAME";
    public static final String STOP_TAG = "STOP_TAG";
    public static final String STOP_TITLE = "STOP_TITLE";
    public static final String STOP_ID = "STOP_ID";
    public static final String STOP_LAT = "STOP_LAT";
    public static final String STOP_LON = "STOP_LON";
    public static final String TRACKER_SIZE = "TRACKER_SIZE";

    private static final int PERMISSION_REQUEST = 700;

    /* Variable */
    private LayoutInflater layoutInflater;
    private LinearLayout layout_main, admobLayout;

    private ArrayList<ItemNextTracker> itemNextTrackers = new ArrayList<>();

    // Counter
    private int timerCounter = 1; // in Second
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            /* Task per 30 second */
            if ((timerCounter % 30) == 0) {
                for (ItemNextTracker tracker : itemNextTrackers) {
                    tracker.loadNextPrediction();
                }
            }
            else {
                for (ItemNextTracker tracker : itemNextTrackers) {
                    tracker.tick();
                }
            }

            // Check the timer
            int timerResolution = 1;
            timerCounter += timerResolution;

            if (timerCounter >= 3600) {
                timerCounter = 0;
            }

            timerHandler.postDelayed(this, timerResolution * 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        /* Setup UI Object */
        layoutInflater = getLayoutInflater();

        layout_main = (LinearLayout) findViewById(R.id.layout_main);
        assert layout_main != null;

        // Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if ((this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.permission_request_title);
                builder.setMessage(R.string.permission_request_msg);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Permission check
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.permission_required_title)
                            .setMessage(R.string.permission_required_msg)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return;
                }

                selectNextAgency();
            }
        });

        // Initialize Admob
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));

        admobLayout = (LinearLayout) getInflater().inflate(R.layout.card_admob, null);

        AdView mAdView = (AdView) admobLayout.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

        // Initialize trackers
        readTracker();
    }

    @Override
    public void onResume() {
        timerHandler.postDelayed(timerRunnable, 0);
        super.onResume();
    }

    @Override
    public void onPause() {
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            intent.putExtra(MainActivity.TRACKER_SIZE, itemNextTrackers.size());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.permission_required_title);
                    builder.setMessage(R.string.permission_required_msg);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {}
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_NEXT) {
            if (resultCode == MainActivity.RESULT_OK) {
                // Load information
                SharedPreferences sharedPref = getSharedPreferences("selector", Activity.MODE_PRIVATE);
                String title = sharedPref.getString(MainActivity.TITLE, "");
                String agency_tag = sharedPref.getString(MainActivity.AGENCY_TAG, "");
                String agency_title = sharedPref.getString(MainActivity.AGENCY_TITLE, "");
                String region_title = sharedPref.getString(MainActivity.REGION_TITLE, "");
                String route_tag = sharedPref.getString(MainActivity.ROUTE_TAG, "");
                String route_title = sharedPref.getString(MainActivity.ROUTE_TITLE, "");
                String direction_tag = sharedPref.getString(MainActivity.DIRECTION_TAG, "");
                String direction_title = sharedPref.getString(MainActivity.DIRECTION_TITLE, "");
                String direction_name = sharedPref.getString(MainActivity.DIRECTION_NAME, "");
                String stop_tag = sharedPref.getString(MainActivity.STOP_TAG, "");
                String stop_title = sharedPref.getString(MainActivity.STOP_TITLE, "");
                String stop_lat = sharedPref.getString(MainActivity.STOP_LAT, "");
                String stop_lon = sharedPref.getString(MainActivity.STOP_LON, "");

                // Add new tracker
                ItemNextTracker tracker = new ItemNextTracker(this, title,
                        agency_tag, agency_title, region_title,
                        route_tag, route_title,
                        direction_tag, direction_title, direction_name,
                        stop_tag, stop_title, stop_lat, stop_lon);

                itemNextTrackers.add(tracker);

                // Write to storage
                writeTracker();

                // Read and refresh the UI
                readTracker();
            }
        }
    }

    /* Select NextBus Agency  */
    private void selectNextAgency() {
        Intent intent = new Intent(this, NextAgencyActivity.class);
        startActivityForResult(intent, MainActivity.REQUEST_NEXT);
    }

    /* Write tracker to storage */
    public void writeTracker() {

        JSONArray json = new JSONArray();

        try {

            for (ItemNextTracker tracker : itemNextTrackers){
                json.put(tracker.getJsonObject());
                Log.e(TAG, "WRITE: " + tracker.getJsonObject().toString());
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("trackers.json", Activity.MODE_PRIVATE));
            outputStreamWriter.write(json.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Read tracker from storage */
    private void readTracker() {

        try {
            InputStream inputStream = openFileInput("trackers.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String str = stringBuilder.toString();

                JSONArray json = new JSONArray(str);

                itemNextTrackers.clear();

                for (int i = 0; i < json.length(); i++){
                    JSONObject trackerObj = json.getJSONObject(i);
                    itemNextTrackers.add(new ItemNextTracker(this, trackerObj));

                    Log.e(TAG, "READ: " + trackerObj.toString());
                }
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        refreshTrackerUI();
    }

    /* Remove a tracker  */
    public void removeTracker(ItemNextTracker tracker) {
        getLayout().removeView(tracker.getLayout());
        itemNextTrackers.remove(tracker);
        writeTracker();
    }


    /* Refresh Tracker UI in the list */
    private void refreshTrackerUI() {
        getLayout().removeAllViews();

        for (ItemNextTracker tracker : itemNextTrackers){
            getLayout().addView(tracker.getLayout());
        }

        if (itemNextTrackers.size() > 0) {
            if (admobLayout != null) {
                getLayout().addView(admobLayout, 1);
            }
        }
        else {
            LinearLayout startLayout = (LinearLayout) getInflater().inflate(R.layout.card_start, null);
            getLayout().addView(startLayout);
        }
    }

    /* Self getter */
    public MainActivity getActivity() {
        return this;
    }

    /* LayoutInflater getter */
    public LayoutInflater getInflater() {
        return layoutInflater;
    }

    /* Layout getter */
    public LinearLayout getLayout() {
        return  layout_main;
    }
}
