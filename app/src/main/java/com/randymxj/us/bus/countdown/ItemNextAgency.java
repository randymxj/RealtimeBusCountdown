package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by xiaojma on 7/28/2016.
 */
public class ItemNextAgency {

    /* Final */
    private static final String TAG = "@@@ ItemNextAgency";

    /* Variable */
    private NextAgencyActivity main;

    private String agency_tag, agency_title, region_title;

    private LinearLayout layout;
    private ImageView iv_avatar;
    private TextView tv_title, tv_content;

    /* Constructor */
    public ItemNextAgency(NextAgencyActivity main, String agency_tag, String agency_title, String region_title) {
        // Variable
        this.main = main;

        this.agency_tag = agency_tag;
        this.agency_title = agency_title;
        this.region_title = region_title;

        // View
        layout = (LinearLayout) main.getInflater().inflate(R.layout.card_agency, null);
        iv_avatar = (ImageView) layout.findViewById(R.id.iv_item_title);
        tv_title = (TextView) layout.findViewById(R.id.tv_item_title);
        tv_content = (TextView) layout.findViewById(R.id.tv_item_content);

        getMain().getLayout().addView(layout);

        // Data
        tv_title.setText(agency_title);
        tv_content.setText(region_title);

        // Listener
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectRoute();
            }
        });
    }

    /* Select route */
    private void selectRoute() {
        // Load selector data
        SharedPreferences sharedPref = getMain().getSharedPreferences("selector", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(MainActivity.TITLE, agency_title);
        editor.putString(MainActivity.AGENCY_TAG, agency_tag);
        editor.putString(MainActivity.AGENCY_TITLE, agency_title);
        editor.putString(MainActivity.REGION_TITLE, region_title);

        editor.commit();

        Intent intent = new Intent(getMain(), NextRouteActivity.class);
        getMain().startActivityForResult(intent, MainActivity.REQUEST_NEXT);
    }

    /* Getter */
    public LinearLayout getLayout() {
        return layout;
    }

    public String getRegion_title() {
        return region_title;
    }

    /* Main Activity getter */
    private NextAgencyActivity getMain() {
        return main;
    }
}
