package com.sos.saveourstudents.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sos.saveourstudents.R;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentMap extends Fragment {

    static final LatLng UCSD = new LatLng(32.8810, 117.2380);
    static final LatLng GEISEL = new LatLng(32.8812, 117.2375);
    private GoogleMap mMap;
    private MapView mMapView;
    private Bundle mBundle;
    private Location mLastLocation;
    private Context mContext;

    private View rootView;


    public FragmentMap() {
        this.mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.map_layout, container,
                false);


        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);


        mMap = mMapView.getMap();


        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded();

        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.setMyLocationEnabled(true);

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






}