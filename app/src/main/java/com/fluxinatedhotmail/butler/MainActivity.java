package com.fluxinatedhotmail.butler;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fluxinatedhotmail.butler.common.activities.BaseActivity;
import com.fluxinatedhotmail.butler.common.activities.MapActivity;
import com.fluxinatedhotmail.butler.common.fragments.Transportation.TransportationService;
import com.fluxinatedhotmail.butler.navigation.NavigationDrawerCallbacks;
import com.fluxinatedhotmail.butler.navigation.NavigationDrawerFragment;


public class MainActivity extends BaseActivity
        implements NavigationDrawerCallbacks
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private String mTitle = "Item 1";
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData("Jomar Pascasio", "jomar.pascasio@yahoo.com", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));

        mSavedInstanceState = savedInstanceState;
        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, TransportationService.newInstance(null))
                                        .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        switch(position)
        {
            case 0:
                mTitle = "Item 1";

                FragmentTransaction(R.id.container,TransportationService.newInstance(null));
                break;
            case 1:
                mTitle = "Item 2";
                startActivity(new Intent(this, MapActivity.class));
                break;
            case 2:
                mTitle = "Item 3";
                break;
            case 3:
                mTitle = "Item 4";
                break;

        }
    }


    @Override
    public void onBackPressed()
    {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            restoreActionBar();
            return true;
        }
        else
        {
            getSupportActionBar().setTitle(R.string.app_name);
            return false;
        }

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

        return super.onOptionsItemSelected(item);
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
