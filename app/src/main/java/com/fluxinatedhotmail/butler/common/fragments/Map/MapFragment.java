package com.fluxinatedhotmail.butler.common.fragments.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.activities.MapActivity;
import com.fluxinatedhotmail.butler.common.adapters.PlaceAutocompleteAdapter;
import com.fluxinatedhotmail.butler.common.fragments.BaseFragment;
import com.fluxinatedhotmail.butler.common.fragments.FragmentChangeCallbacks;
import com.fluxinatedhotmail.butler.common.logger.Log;
import com.fluxinatedhotmail.butler.enums.FragmentTags;
import com.fluxinatedhotmail.butler.enums.TAGS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Fluxi on 11/7/2015.
 */
public class MapFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,View.OnClickListener
{

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteViewDestination, mAutocompleteViewOrigin;

    //TODO: need to change LatLngBounds
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private Bundle mCoordinatesBundle;
    private FragmentChangeCallbacks mFragmentChange;



    public static MapFragment newInstance(Object obj)
    {

        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        args.putString(FRAGMENT_KEY,fragment.getClass().getName());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mFragmentChange = (MapActivity)getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.animating_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_destination,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
       //TODO
        mCoordinatesBundle = new Bundle();

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly. enableAutomanage only works if activity is extended with fragmentactivity
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                //.enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mAdapter = new PlaceAutocompleteAdapter(getActivity().getApplicationContext(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteViewOrigin = (AutoCompleteTextView)getView().findViewById(R.id.autocomplete_places_origin);
        mAutocompleteViewOrigin.setAdapter(mAdapter);
        mAutocompleteViewOrigin.setOnItemClickListener(new myAutoCompleteItemClickListener(TAGS.ORIGIN));

        mAutocompleteViewDestination = (AutoCompleteTextView)getView().findViewById(R.id.autocomplete_places_destination);
        mAutocompleteViewDestination.setAdapter(mAdapter);
        mAutocompleteViewDestination.setOnItemClickListener(new myAutoCompleteItemClickListener(TAGS.DESTINATION));
        ((Button) getView().findViewById(R.id.button_map)).setOnClickListener(this);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity().getApplicationContext(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_map:

                mFragmentChange.OnFragmentChange(FragmentTags.MAP,mCoordinatesBundle);
                break;
        }
    }

    private class myAutoCompleteItemClickListener implements AdapterView.OnItemClickListener
    {

        private TAGS tag;

        public myAutoCompleteItemClickListener(TAGS tag)
        {
            this.tag = tag;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>()
            {
                @Override
                public void onResult(PlaceBuffer places)
                {
                    if (!places.getStatus().isSuccess())
                    {
                        Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                        places.release();
                        return;
                    }
                    // Get the Place object from the buffer.
                    final Place place = places.get(0);
                    /*final CharSequence thirdPartyAttribution = places.getAttributions();
                    if (thirdPartyAttribution == null) {
                        mPlaceDetailsAttribution.setVisibility(View.GONE);
                    } else {
                        mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                        mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
                    }*/
                    Log.i(TAG, "Place details received: " + place.getName());
                    mCoordinatesBundle.putDouble(tag.name() + getResources().getString(R.string.latitude), place.getLatLng().latitude);
                    mCoordinatesBundle.putDouble(tag.name() + getResources().getString(R.string.longitude), place.getLatLng().longitude);
                    mCoordinatesBundle.putString(tag.name() + getResources().getString(R.string.place_name), place.getName().toString());
                    mCoordinatesBundle.putString(tag.name() + getResources().getString(R.string.place_address), place.getAddress().toString());
                    places.release();
                }
            });

            Toast.makeText(getActivity().getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    }


    @Override
    protected void initializeViews(ViewGroup vg)
    {
        //TODO
    }
}
