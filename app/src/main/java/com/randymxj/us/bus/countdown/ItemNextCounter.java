package com.randymxj.us.bus.countdown;

import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class for Counter Item
 */
public class ItemNextCounter {

    /* Final */
    private static final String TAG = "@@@ ItemNextCounter";

    /* Variable */
    MainActivity main;
    LinearLayout layout;

    TextView tv_counter;

    private long initTimeStamp;
    private int seconds;
    private String vehicle;

    public ItemNextCounter(MainActivity main, String seconds, String vehicle) {
        this.main = main;
        this.seconds = Integer.valueOf(seconds);
        this.vehicle = vehicle;

        initTimeStamp = System.currentTimeMillis()/1000;

        // Setup UI object
        layout = (LinearLayout) main.getInflater().inflate(R.layout.view_counter, null);
        tv_counter = (TextView) layout.findViewById(R.id.tv_item_counter);
    }

    /* Update UI */
    public void updateUI() {

        // Calculate second delta
        long currentTimeStamp = System.currentTimeMillis()/1000;
        int delta = (int) (currentTimeStamp - initTimeStamp);

        int calcSeconds = seconds - delta;

        String str;

        if (calcSeconds >= 0) {
            int hr = calcSeconds / 3600;
            int min = (calcSeconds % 3600) / 60;
            int sec = calcSeconds % 60;

            String hr_str = String.valueOf(hr);
            String min_str = String.valueOf(min);
            String sec_str = String.valueOf(sec);

            str = hr_str + " hrs " + min_str + " mins " + sec_str + " secs";
        }
        else {
            str = "Arrived";
        }

        tv_counter.setText(str);
    }

    /* Getter*/

    /* Layout getter */
    public LinearLayout getLayout() {
        return layout;
    }

}
