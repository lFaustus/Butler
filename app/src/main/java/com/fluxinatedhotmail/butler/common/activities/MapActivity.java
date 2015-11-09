package com.fluxinatedhotmail.butler.common.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fluxinatedhotmail.butler.FragmentActivityChangeCallbacks;
import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.fragments.Map.MapFragment;
import com.fluxinatedhotmail.butler.common.fragments.Map.MapShowDirection;
import com.fluxinatedhotmail.butler.enums.Tags;

public class MapActivity extends BaseActivity implements FragmentActivityChangeCallbacks
{
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_destination);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null)
        {
            //overridePendingTransition(R.anim.animation_enter_set, R.anim.animation_leave_set);
            overridePendingTransition(R.anim.animation_slide_in, R.anim.animation_slide_out);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootview_fragment, MapFragment.newInstance(null))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnFragmentActivityChange(Tags.FragmentActivityTags fragment, Object... extra)
    {
        switch(fragment)
        {
            case MAP_FRAGMENT:
                FragmentTransaction(R.id.rootview_fragment, MapShowDirection.newInstance((Bundle)extra[0]));
                break;

            case HOME_FRAGMENT:
                break;

            case NEWS_FRAGMENT:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0)
            overridePendingTransition(R.anim.animation_slide_out2,R.anim.animation_slide_in2);
    }
}
