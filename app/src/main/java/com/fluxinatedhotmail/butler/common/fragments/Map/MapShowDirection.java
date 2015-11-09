package com.fluxinatedhotmail.butler.common.fragments.Map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fluxinatedhotmail.butler.MyApplication;
import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.activities.MapActivity;
import com.fluxinatedhotmail.butler.common.fragments.BaseFragment;
import com.fluxinatedhotmail.butler.common.models.GoogleDirections;
import com.fluxinatedhotmail.butler.common.utils.maputils.LocationProvider;
import com.fluxinatedhotmail.butler.common.utils.maputils.LocationProvider.LocationCallback;
import com.fluxinatedhotmail.butler.enums.Tags;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.http.GenericUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fluxi on 11/6/2015.
 */
public class MapShowDirection extends BaseFragment implements LocationCallback
{

    private GoogleMap googleMap;
    private LocationProvider mLocationProvider;
    private TextView mOrigin,mDestination;
    private static final String PLACES_DIRECTIONS = "http://maps.googleapis.com/maps/api/directions/json";
    private final Handler mHandler = new Handler();
    private Marker selectedMarker,liveTrackingMarker;
    private List<ArrayList<Marker>> markerList = new ArrayList<>();
    private ArrayList<Marker> markers;
    private Animator animator;
    private Location mCurrentLocation;
    private boolean isFirstTime = true;


    public static MapShowDirection newInstance(@Nullable Bundle bundle)
    {
        Bundle args = new Bundle();
        if(bundle == null)
            args = bundle;

        MapShowDirection fragment = new MapShowDirection();
        args.putString(FRAGMENT_KEY, fragment.getClass().getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((MapActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((MapActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MapActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_maps,container,false);
        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setUpMapIfNeeded();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mOrigin = (TextView) getView().findViewById(R.id.maps_origin);
        mDestination = (TextView) getView().findViewById(R.id.maps_destination);
    }


    @Override
    protected void initializeViews(ViewGroup vg)
    {
        //TODO: need to experiment as I encountered problem when initialization of views happens in here
        for(int i =0; i < vg.getChildCount() ; i++)
        {

        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mMenu = getMenuInflater();
        menu.clear();
        mMenu.inflate(R.menu.animating_menu,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_bar_start_animation)
        {
            animator = new Animator();
            animator.startAnimation(true);
        }
        else if (item.getItemId() == R.id.action_bar_show_route)
        {
            PolylineOptions options;
            Iterator<ArrayList<Marker>> m = markerList.iterator();
            while(m.hasNext())
            {
                options = new PolylineOptions().geodesic(true).color(Color.BLUE).width(5);
                ArrayList<Marker> mMarkerList = m.next();
                Iterator<Marker> mMarker = mMarkerList.iterator();
                while(mMarker.hasNext())
                {
                    options.add(mMarker.next().getPosition());
                }
                googleMap.addPolyline(options);
            }
        }
        else if(item.getItemId() == android.R.id.home)
        {
            ((MapActivity)getActivity()).onBackPressed();
        }
        return true;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.animating_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mLocationProvider = new LocationProvider(getActivity().getApplicationContext(), this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mLocationProvider.disconnect();
        if(animator!=null)
            animator.stopAnimation();
    }

    @Override
    public void handleNewLocation(Location location)
    {
        clearMarker();


        this.mCurrentLocation = location;
        LatLng currentPos = new LatLng(location.getLatitude(),location.getLongitude());
        if(isFirstTime)
        {
            Bundle mCoordinates = getArguments();

            LatLng origin = new LatLng(mCoordinates.getDouble(Tags.MapTags.ORIGIN.name() + getResources().getString(R.string.latitude), mCurrentLocation.getLatitude())
                    , mCoordinates.getDouble(Tags.MapTags.ORIGIN.name() + getResources().getString(R.string.longitude), mCurrentLocation.getLongitude()));

            LatLng destination = new LatLng(mCoordinates.getDouble(Tags.MapTags.DESTINATION.name() + getResources().getString(R.string.latitude), mCurrentLocation.getLatitude())
                    , mCoordinates.getDouble(Tags.MapTags.DESTINATION.name() + getResources().getString(R.string.longitude), mCurrentLocation.getLongitude()));
            setPlacesDirections(origin, destination);
            currentPos = origin;
            isFirstTime = false;
        }
        else
        {

            if(liveTrackingMarker == null)
                liveTrackingMarker = googleMap.addMarker(new MarkerOptions().position(currentPos));
            else
                liveTrackingMarker.setPosition(currentPos);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
    }



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    public void setPlacesDirections(LatLng origin,LatLng destination) {

        GenericUrl genericUrl = new GenericUrl(PLACES_DIRECTIONS);
        genericUrl.put("origin",origin.latitude+","+origin.longitude);
        genericUrl.put("destination",destination.latitude+","+destination.longitude);
        genericUrl.put("sensor",false);
        genericUrl.put("mode", "driving");
        genericUrl.put("alternatives", false);
        JsonObjectRequest mJSONOBjectRequest = new JsonObjectRequest(Request.Method.GET, genericUrl.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("URL", genericUrl.toString());
                try {

                    JSONArray mArray = response.getJSONArray("routes");
                    ArrayList<GoogleDirections> mDirectionsList = new ArrayList<>();
                    GoogleDirections mDirection;
                    Log.e("route size",mArray.length()+""  );
                    final int size = mArray.length();
                    for(int i = 0; i < size ; i++)
                    {
                        //JSONArray mLegsArray = mArray.getJSONArray(i);
                        JSONObject mObject = mArray.getJSONObject(i);
                        JSONArray mLegsArray = mObject.getJSONArray("legs");
                        Log.e("legs array size",mLegsArray.length()+"");
                        final int size2 = mLegsArray.length();
                        JSONObject mObject2 = mLegsArray.getJSONObject(0);
                        //mArrival = mObject2.getJSONObject("arrival_time"),
                        //mDeparture = mObject2.getJSONObject("departure_time");
                        JSONArray mStepsArray = mObject2.getJSONArray("steps");
                        JSONObject mDistanceObject = mObject2.getJSONObject("distance"),
                                mDurationObject = mObject2.getJSONObject("duration");

                        mDirection = new GoogleDirections();
                        String end_address = mObject2.getString("end_address"),
                                start_address = mObject2.getString("start_address");

                        mDirection.setDistance(mDistanceObject.getString("text"));
                        mDirection.setDuration(mDurationObject.getString("text"));
                        mOrigin.setText(start_address);
                        mDestination.setText(end_address);
                        // Long departure = mDeparture.getLong("value"),
                        //     arrival = mArrival.getLong("value");
                        // mDirection.setDeparture_time(departure);
                        // mDirection.setArrivalTime(arrival);

                        JSONObject mOverviewPolylineObject = mObject.getJSONObject("overview_polyline");
                        mDirection.setOverviewPolyline(mOverviewPolylineObject.getString("points"));
                        mDirection.setEndAddress(end_address);
                        mDirection.setStartAddress(start_address);
                        for(int l = 0; l < mStepsArray.length(); l++)
                        {

                            JSONObject mObject3 = mStepsArray.getJSONObject(i);
                            Log.e("mobject3 size",mObject3.length()+"");
                            // JSONObject mDistanceObject = mObject3.getJSONObject("distance");
                            // JSONObject mDistanceObject = mObject3.getJSONObject("distance"),
                            //       mDurationObject = mObject3.getJSONObject("duration"),
                            JSONObject     mEndLocationObject = mObject3.getJSONObject("end_location"),
                                    mStartLocationObject = mObject3.getJSONObject("start_location");
                            //mPolylinePointsObject = mObject3.getJSONObject("polyline");

                            //  mDirection.setDistance(mDistanceObject.getString("text"));
                            // mDirection.setDuration(mDurationObject.getString("text"));
                            mDirection.setEndLocation(mEndLocationObject.getDouble("lat"), mEndLocationObject.getDouble("lng"));
                            mDirection.setStartLocation(mStartLocationObject.getDouble("lat"), mStartLocationObject.getDouble("lng"));
                            //mDirection.setOverviewPolyline(mPolylinePointsObject.getString("points"));

                            mDirection.setTravelMode(mObject3.getString("travel_mode"));
                        }
                        /*for(int k = 0 ; k <size2 ; k++)
                        {

                            JSONObject mObject2 = mLegsArray.getJSONObject(i);
                           // Log.e("mobject2",mObject2.toString()+"");
                            //Log.e("mobject2 size",mObject2.length()+"");

                            JSONArray mStepsArray = mObject2.getJSONArray("steps");
                             mDirection = new Directions();
                            String end_address = mObject2.getString("end_address"),
                                    start_address = mObject2.getString("start_address");
                            mDirection.setEndAddress(end_address);
                            mDirection.setStartAddress(start_address);
                            for(int l = 0; l < mStepsArray.length(); l++)
                            {

                                JSONObject mObject3 = mStepsArray.getJSONObject(i);
                                Log.e("mobject3 size",mObject3.length()+"");
                               // JSONObject mDistanceObject = mObject3.getJSONObject("distance");
                                JSONObject mDistanceObject = mObject3.getJSONObject("distance"),
                                           mDurationObject = mObject3.getJSONObject("duration"),
                                           mEndLocationObject = mObject3.getJSONObject("end_location"),
                                           mStartLocationObject = mObject3.getJSONObject("start_location"),
                                           mPolylinePointsObject = mObject3.getJSONObject("polyline");

                                mDirection.setDistance(mDistanceObject.getString("text"));
                                mDirection.setDuration(mDurationObject.getString("text"));
                                mDirection.setEndLocation(mEndLocationObject.getDouble("lat"), mEndLocationObject.getDouble("lng"));
                                mDirection.setStartLocation(mStartLocationObject.getDouble("lat"), mStartLocationObject.getDouble("lng"));
                                mDirection.setOverviewPolyline(mPolylinePointsObject.getString("points"));
                                mDirection.setTravelMode(mObject3.getString("travel_mode"));
                            }
                           // mDirections = mDirection;
                        }*/
                        // mDirections = mDirection;
                        mDirectionsList.add(mDirection);
                    }
                    Log.e("direction list size",mDirectionsList.size()+"");
                    markerList = decodePoly(mDirectionsList);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.Volley().getRequestQueue().add(mJSONOBjectRequest);
        //System.out.println(mDirections.toString());
    }

    private List<ArrayList<Marker>> decodePoly(List<GoogleDirections> mDirections) {
        List<ArrayList<Marker>> mMarkerList = new ArrayList<>();
        ArrayList<Marker> poly = new ArrayList<Marker>();
        Iterator<GoogleDirections> directions = mDirections.iterator();
        while(directions.hasNext()) {
            GoogleDirections direct = directions.next();
            int index = 0, len = direct.getOverview_polyline().length();
            int lat = 0, lng = 0;
            int counter = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = direct.getOverview_polyline().charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = direct.getOverview_polyline().charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                //googleMap.addMarker(new MarkerOptions().position(p).visible(false));
                //poly.add(p);
                Marker marker = googleMap.addMarker(new MarkerOptions().position(p));
                if (counter == 0) {
                    marker.setTitle("I'm Here");
                    marker.showInfoWindow();
                } else
                    marker.setVisible(false);

                /*if(!directions.hasNext())
                {
                    marker.setVisible(true);
                    marker.setTitle("Destination Distance: " + direct.getDistance());
                    marker.setSnippet("Estimated Time Of Arrival: " + direct.getDuration());
                    marker.showInfoWindow();
                }*/
                poly.add(marker);
                counter++;
            }
            mMarkerList.add(poly);
        }
        poly.get(poly.size()-1).setVisible(true);
        poly.get(poly.size() - 1).setTitle("Destination Distance: " + mDirections.get(0).getDistance());
        poly.get(poly.size() - 1).setSnippet("Estimated Time Of Arrival: " + mDirections.get(0).getDuration());
        poly.get(poly.size() - 1).showInfoWindow();
       /* Marker m = poly.get(0);
        m.setTitle("I am here");
        m = poly.get(poly.size()-1);
        m.setTitle("Destination " + markers.size());
        m.setSnippet("Distance - " + distance);*/

        return mMarkerList;
    }


    public class Animator implements Runnable {

        private static final int ANIMATE_SPEEED = 1500;
        private static final int ANIMATE_SPEEED_TURN = 1000;
        private static final int BEARING_OFFSET = 20;

        private final Interpolator interpolator = new LinearInterpolator();

        int currentIndex = 0;

        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stop() {
            trackingMarker.remove();
            mHandler.removeCallbacks(animator);
            animator = null;

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = markers.get(0).getPosition();
            LatLng secondPos = markers.get(1).getPosition();

            setupCameraPositionForMovement(markerPos, secondPos);

        }

        private void setupCameraPositionForMovement(LatLng markerPos,
                                                    LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos,secondPos);

            trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(trackingMarker.getPosition())
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(tilt)
                            .zoom(googleMap.getCameraPosition().zoom >= 16 ? googleMap.getCameraPosition().zoom : 16)
                            .build();
            //navigateToPoint(trackingMarker.getPosition(),0,cameraPosition.bearing,15,true);
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();


        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(markers.get(0).getPosition());
            return googleMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {

            List<LatLng> points = polyLine.getPoints();
            /*LatLng p = new LatLng((((double) latLng.latitude / 1E5)),
                    (((double) latLng.longitude / 1E5)));*/
            points.add(latLng);
            polyLine.setPoints(points);
            polyLine.setGeodesic(true);
        }



        public void stopAnimation() {
            animator.stop();
        }

        public void startAnimation(boolean showPolyLine) {
            Iterator<ArrayList<Marker>> markerlist = markerList.iterator();
            while(markerlist.hasNext())
            {
                markers = markerlist.next();
                if (markers.size() >= 2)
                {
                    animator.initialize(showPolyLine);
                }
            }
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);

            double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
            double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
            LatLng newPosition = new LatLng(lat, lng);

            trackingMarker.setPosition(newPosition);


            if (showPolyline)
                updatePolyLine(newPosition);

            // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
            //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
            //navigateToPoint(newPosition,false);
            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                //if (currentIndex<markers.size()-1) {
                if (currentIndex<markers.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    LatLng begin = getBeginLatLng();
                    LatLng end = getEndLatLng();

                    float bearingL = bearingBetweenLatLngs(begin, end);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(end) // changed this...
                                    .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(googleMap.getCameraPosition().zoom)
                                    .build();

                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(animator, 16);

                } else {

                    currentIndex++;
                    highLightMarker(currentIndex);
                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(trackingMarker.getPosition()) // changed this...
                                            //.bearing(bearingL  + BEARING_OFFSET)
                                            //.tilt(tilt)
                                    .zoom(googleMap.getCameraPosition().zoom)
                                    .build();
                    navigateToPoint(newPosition,false,cameraPosition);
                    stopAnimation();
                }

            }
        }




        private LatLng getEndLatLng() {
            return markers.get(currentIndex+1).getPosition();
        }

        private LatLng getBeginLatLng() {
            return markers.get(currentIndex).getPosition();
        }

        private void adjustCameraPosition() {
            //System.out.println("tilt = " + tilt);
            //System.out.println("upward = " + upward);
            //System.out.println("zoom = " + zoom);
            if (upward) {

                if (tilt<90) {
                    tilt ++;
                    zoom-=0.01f;
                } else {
                    upward=false;
                }

            } else {
                if (tilt>0) {
                    tilt --;
                    zoom+=0.01f;
                } else {
                    upward=true;
                }
            }
        }
    };


    private void resetMarkers() {
        /*Iterator<ArrayList<Marker>> markers = this.markerList.iterator();
        while(markers.hasNext())
        {
            ArrayList<Marker> mMarker = markers.next();
            //listiterator so that we can modify the content
            ListIterator<Marker> tempMarker = mMarker.listIterator();
            while(tempMarker.hasNext())
            {
                tempMarker.next().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
        }*/

        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    /**
     * Adds a marker to the map.
     */
    //TODO: to be modified since the list contains list of markers for possible routes
    public void addMarkerToMap(LatLng latLng) {
        Marker marker;

        if(markers.size() == 0) {
            marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title("I am here")
                    .snippet("I am here"));
        }
        else{
            Location location = convertLatLngToLocation(markers.get(markers.size()-1).getPosition());
            Location destination = convertLatLngToLocation(latLng);

            marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title("Destination " + markers.size())
                    .snippet("Distance  " + location.distanceTo(destination)/1000 +"KM"));
        }
        markers.add(marker);

    }


    private void clearMarker()
    {
        googleMap.clear();
        markerList.clear();
        //markers.clear();
    }

    /**
     * Remove the currently selected marker.
     */
    public void removeSelectedMarker() {
        /*Iterator<ArrayList<Marker>> markerList = this.markerList.iterator();
        while(markerList.hasNext())
        {
            ArrayList<Marker> mMarker = markerList.next();
            Iterator<Marker> tempMarker = mMarker.iterator();
            while(tempMarker.hasNext())
            {
                if(tempMarker.equals(this.selectedMarker))
                {
                    tempMarker.remove();
                    this.selectedMarker.remove();
                    break;
                }
            }
        }*/
        this.markers.remove(this.selectedMarker);
        this.selectedMarker.remove();
    }


    /**
     * Allows us to navigate to a certain point.
     */
    public void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom,boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);

    }

    public void navigateToPoint(LatLng latLng, boolean animate,@Nullable CameraPosition mposition) {
        CameraPosition position;
        if(mposition == null)
            position = new CameraPosition.Builder().target(latLng).build();
        else
            position = mposition;
        changeCameraPosition(position, animate);
    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, 15);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            //googleMap.animateCamera(cameraUpdate,1500,null);
            googleMap.animateCamera(cameraUpdate,1500,null);
        } else {
            // cameraUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, 15);
            googleMap.moveCamera(cameraUpdate);
        }

    }


    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {

        highLightMarker(markers.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {

		/*
		for (Marker foundMarker : this.markers) {
			if (!foundMarker.equals(marker)) {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				foundMarker.showInfoWindow();
			}
		}
		*/
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //marker.showInfoWindow();

        //Utils.bounceMarker(googleMap, marker);

        this.selectedMarker = marker;
        navigateToPoint(selectedMarker.getPosition(),true,null);
    }

}
