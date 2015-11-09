package com.fluxinatedhotmail.butler.common.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.logger.Log;
import com.fluxinatedhotmail.butler.common.logger.LogWrapper;

/**
 * Created by Fluxi on 11/6/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /** Set up targets to receive log data */
    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Ready");
    }

    public void initializeViews(ViewGroup v){}
    protected void FragmentTransaction(int containerViewId, Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_slide_in
                        , R.anim.animation_slide_out
                        , R.anim.animation_slide_out2
                        , R.anim.animation_slide_in2)
                .replace(containerViewId, fragment)
                .addToBackStack(null)
                .commit();
    }
}
