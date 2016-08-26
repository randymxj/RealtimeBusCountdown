package com.randymxj.us.bus.countdown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class for Direction Item
 */
public class ItemNextDirection {

    /* Final */
    private static final String TAG = "@@@ ItemNextRoute";

    /* Variable */
    private NextDirectionActivity main;

    private String direction_tag, direction_title, direction_name;

    private LinearLayout layout;
    private ImageView iv_avatar;
    private TextView tv_title, tv_content;

    /* Constructor */
    public ItemNextDirection(NextDirectionActivity main, String direction_tag, String direction_title, String direction_name) {
        // Variable
        this.main = main;

        this.direction_tag = direction_tag;
        this.direction_title = direction_title;
        this.direction_name = direction_name;

        // View
        layout = (LinearLayout) main.getInflater().inflate(R.layout.card_route, null);
        iv_avatar = (ImageView) layout.findViewById(R.id.iv_item_title);
        tv_title = (TextView) layout.findViewById(R.id.tv_item_title);
        tv_content = (TextView) layout.findViewById(R.id.tv_item_content);

        getMain().getLayout().addView(layout);

        // Data
        tv_title.setText(direction_title);
        tv_content.setText(direction_name);

        // Listener
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectStop();
            }
        });
    }

    /* Select stop */
    private void selectStop() {
        // Load selector data
        SharedPreferences sharedPref = getMain().getSharedPreferences("selector", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(MainActivity.DIRECTION_TAG, direction_tag);
        editor.putString(MainActivity.DIRECTION_TITLE, direction_title);
        editor.putString(MainActivity.DIRECTION_NAME, direction_name);

        editor.commit();

        Intent intent = new Intent(getMain(), NextStopActivity.class);
        getMain().startActivityForResult(intent, MainActivity.REQUEST_NEXT);
    }

    /* Main Activity getter */
    private NextDirectionActivity getMain() {
        return main;
    }
}
