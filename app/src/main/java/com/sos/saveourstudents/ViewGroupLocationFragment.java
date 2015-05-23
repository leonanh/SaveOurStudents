package com.sos.saveourstudents;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewGroupLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewGroupLocationFragment extends android.support.v4.app.Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_COORDINATES = "paramCoordinates";
    private LatLng mGroupCoordinates;
    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private View rootView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewGroupLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewGroupLocationFragment newInstance(LatLng param1) {
        ViewGroupLocationFragment fragment = new ViewGroupLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COORDINATES, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewGroupLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupCoordinates = getArguments().getParcelable(ARG_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        rootView =  inflater.inflate(R.layout.fragment_view_group_location, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.view_group_location_map);
        initializeMap();
        return rootView;
    }

    private void initializeMap(){
        mMapView.onCreate(new Bundle());
        mMap = mMapView.getMap();


        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildGoogleApiClient();
        mMapView.onResume();
        mMap.addMarker(new MarkerOptions()
                .position(mGroupCoordinates));
        mMap.setMyLocationEnabled(true);
        zoomToMyPosition();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Zooms to the question's location
     */
    private void zoomToMyPosition(){

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    mGroupCoordinates, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mGroupCoordinates)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

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

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }
}
