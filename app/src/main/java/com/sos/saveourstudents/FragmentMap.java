package com.sos.saveourstudents;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentMap extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    static final LatLng UCSD = new LatLng(32.88006, -117.234013);
    static final LatLng GEISEL = new LatLng(32.881151, -117.23744999999997);
    private GoogleMap mMap;
    private MapView mMapView;
    private Bundle mBundle;
    private Location mLastLocation;
    private Context mContext;

    private View rootView;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Question> mPostList;

    private LayoutInflater minflater;
    public FragmentMap() {
        this.mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        minflater = inflater;
        mContext = this.getActivity();
        rootView = inflater.inflate(R.layout.map_layout, container,
                false);

        createAndShowMap();

        getMapData();

        return rootView;
    }



    public void createAndShowMap(){

        buildGoogleApiClient();

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        mMap = mMapView.getMap();
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        zoomToMyPosition();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = minflater.inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);

                // Setting the longitude
                tvLng.setText("Longitude:" + latLng.longitude);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

    }


    public void getMapData()
    {
        //Singleton is asynchronous, carries over from FragmentFeed

        //TODO: Make volley call
        String url = "http://10.0.2.2:8080/com.mysql.services/rest/serviceclass/getVenues";

        /*
        String mTitle;
        String mDesc;
        Image mProfPic;
        double mTime = howMuchTimeAgo(GETSYSTEMTIME);
        double mDist = calculateDistfromLatLng(yourLatLng, theirLatLng);
        */


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        //System.out.println("Response: " + response.toString());
                        createUI();
                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //System.out.println("Error: " + error.toString());
                        displayError();
                    }
                });
        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }

    /**
     * The final call after volley returns data,
     * the volley call will save the data to a local datastructure (arraylist maybe)
     * and build the map according to index of list
     * TODO
     */
    private void createUI()
    {
        mPostList = new ArrayList<Question>();
        /*
        for (int i = 0; i < Json Array Length; i++)
        {
            if (jsonobjectarray[i].distance <= user.distance)
            {
                Question temp = new Question(whatever);
                mPostList.add(temp);
            }
        }
            */
        if(mMap != null)//Fernando test
        {
            for (int i = 0; i < mPostList.size(); i++)
            {
                     /*mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .title(mTitle)
                    .snippet());*/
            }
            Marker ucsd = mMap.addMarker(new MarkerOptions().position(UCSD)
                    .title("UCSD"));
            Marker geisel = mMap.addMarker(new MarkerOptions()
                    .position(GEISEL)
                    .title("GEISEL")
                    .snippet("GEISEL is cool"));

            mMap.setMyLocationEnabled(true);
        }

    }

    private void displayError()
    {
        /* Display an error message to the user if request fails */
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    /**
     * Magic
     */
    private void zoomToMyPosition(){

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }



    }


}