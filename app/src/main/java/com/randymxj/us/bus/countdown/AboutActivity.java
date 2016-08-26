package com.randymxj.us.bus.countdown;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutActivity extends AppCompatActivity {

    /* Final */
    private static final String TAG = "@@@ AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /* Setup UI Object */
        LayoutInflater layoutInflater = getLayoutInflater();

        LinearLayout layout_about = (LinearLayout) findViewById(R.id.layout_about);
        assert layout_about != null;

        // Version
        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        String version = "Version: ";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (tv_version != null) {
            tv_version.setText(version);
        }

        // Usage
        TextView tv_usage = (TextView) findViewById(R.id.tv_usage);
        String usage = "Tracker Usage: ";

        /* Load Intent Extra */
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int size = extras.getInt(MainActivity.TRACKER_SIZE);
            usage += String.valueOf(size);
            if (tv_usage != null) {
                tv_usage.setText(usage);
            }
        }

    }
}
