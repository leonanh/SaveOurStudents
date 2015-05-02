package com.sos.saveourstudents;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

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


    public FragmentMap() {
        this.mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getActivity();
        rootView = inflater.inflate(R.layout.map_layout, container,
                false);


        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);


        mMap = mMapView.getMap();


        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded();

        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if(mMap != null)//Fernando test
        {
            Marker ucsd = mMap.addMarker(new MarkerOptions().position(UCSD)
                    .title("UCSD"));
            Marker geisel = mMap.addMarker(new MarkerOptions()
                            .position(GEISEL)
                            .title("GEISEL")
                            .snippet("GEISEL is cool"));

            mMap.setMyLocationEnabled(true);
        }


        buildGoogleApiClient();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            TextView mLatitudeText = (TextView) rootView.findViewById(R.id.lat_text);
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            TextView mLongitudeText = (TextView) rootView.findViewById(R.id.long_text);
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        //setUpMapIfNeeded();


        //followMeLocationSource = new FollowMeLocationSource();


        /*
        if (mMap!=null){
            Marker hamburg = mMap.addMarker(new MarkerOptions().position(UCSD)
                    .title("UCSD"));
            Marker kiel = mMap.addMarker(new MarkerOptions()
                    .position(GEISEL)
                    .title("GEISEL")
                    .snippet("GEISEL is cool")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UCSD, 15));

            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
*/


        return rootView;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName("users.account.name@gmail.com")
                .build();*/
        mGoogleApiClient.connect();
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapView) rootView.findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //mMap.setMyLocationEnabled(true);
        //followMeLocationSource.getBestAvailableProvider();

        // Get a reference to the map/GoogleMap object
        //setUpMapIfNeeded();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        //mMap.setMyLocationEnabled(false);
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

    // Marker info bubble

    // Add window
    /*static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    Marker melbourne = mMap.addMarker(new MarkerOptions()
            .position(MELBOURNE)
            .title("Melbourne")
            .snippet("Population: 4,137,400"));

    // Show window

    static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    melbourne = mMap.addMarker(new MarkerOptions()
            .position(MELBOURNE)
            .title("Melbourne"));
    melbourne.showInfoWindow();*/


}