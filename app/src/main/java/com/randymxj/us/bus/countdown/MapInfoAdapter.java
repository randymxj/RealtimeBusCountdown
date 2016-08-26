package com.randymxj.us.bus.countdown;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Xiaojing on 7/30/2016.
 */
public class MapInfoAdapter implements InfoWindowAdapter {

    /* Variable */
    private NextStopActivity main;

    public MapInfoAdapter(NextStopActivity m) {
        main = m;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        Context context = getMain();

        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(context);
        title.setTextColor(ContextCompat.getColor(getMain(), R.color.textColorTitle));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(context);
        snippet.setTextColor(ContextCompat.getColor(getMain(), R.color.textColorContent));
        snippet.setText(marker.getSnippet());
        snippet.setGravity(Gravity.CENTER);

        info.addView(title);
        info.addView(snippet);

        return info;
    }

    /* Main getter */
    private NextStopActivity getMain() {
        return main;
    }

}
