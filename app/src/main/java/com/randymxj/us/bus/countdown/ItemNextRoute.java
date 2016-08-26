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
 * Class for route item
 */
public class ItemNextRoute {

    /* Final */
    private static final String TAG = "@@@ ItemNextRoute";

    /* Variable */
    private NextRouteActivity main;

    private String route_tag, route_title;

    private LinearLayout layout;
    private ImageView iv_avatar;
    private TextView tv_title, tv_content;

    /* Constructor */
    public ItemNextRoute(NextRouteActivity main, String route_tag, String route_title) {

        // Variable
        this.main = main;

        this.route_tag = route_tag;
        this.route_title = route_title;

        // View
        layout = (LinearLayout) main.getInflater().inflate(R.layout.card_route, null);
        iv_avatar = (ImageView) layout.findViewById(R.id.iv_item_title);
        tv_title = (TextView) layout.findViewById(R.id.tv_item_title);
        tv_content = (TextView) layout.findViewById(R.id.tv_item_content);

        getMain().getLayout().addView(layout);

        // Data
        tv_title.setText(route_title);
        tv_content.setText(route_tag);

        // Listener
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectDirection();
            }
        });
    }

    /* Select direction */
    private void selectDirection() {
        // Load selector data
        SharedPreferences sharedPref = getMain().getSharedPreferences("selector", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(MainActivity.ROUTE_TAG, route_tag);
        editor.putString(MainActivity.ROUTE_TITLE, route_title);

        editor.commit();

        Intent intent = new Intent(getMain(), NextDirectionActivity.class);
        getMain().startActivityForResult(intent, MainActivity.REQUEST_NEXT);
    }

    /* Main Activity getter */
    private NextRouteActivity getMain() {
        return main;
    }
}
