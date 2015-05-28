package com.sos.saveourstudents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class EditQuestionLocationFragment extends android.support.v4.app.Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {


    private static final String QUESTION_LOCATION = "paramCoordinates";
    private static final String USER_IMAGE = "userImage";

    private LayoutInflater mInflater;

    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private View rootView;

    private Location mCurrentLocation;
    private Location newLocation;
    private String userImageUrl;
    private boolean mEditable;

    public static EditQuestionLocationFragment newInstance(Location location, String userImageUrl, boolean isEditable) {
        EditQuestionLocationFragment fragment = new EditQuestionLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(QUESTION_LOCATION, location);
        args.putString(USER_IMAGE, userImageUrl);
        args.putBoolean("isEditable", isEditable);
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuestionLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentLocation = getArguments().getParcelable(QUESTION_LOCATION);
            userImageUrl = getArguments().getString(USER_IMAGE);
            mEditable = getArguments().getBoolean("isEditable");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mInflater = inflater;
        rootView =  inflater.inflate(R.layout.fragment_view_group_location, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.view_group_location_map);
        initializeMap();



        return rootView;
    }

    private void initializeMap(){
        mMapView.onCreate(new Bundle());
        mMap = mMapView.getMap();

        if(mEditable)
            mMap.setOnMarkerDragListener(this);

        mMap.setMyLocationEnabled(true);

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildGoogleApiClient();
        mMapView.onResume();


        showCustomMarker();


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
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //System.out.println("Connected to GoogleApi: " + mCurrentLocation);
        //getLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {

        //System.out.println("Location: "+location);
        //Stop updates after we get a location....
        //showCustomMarker();
        stopLocationUpdates();

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void getLocationUpdate(){
        createLocationRequest();
        startLocationUpdates();
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

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //System.out.println("On marker drag end");
        newLocation = new Location("new");
        newLocation.setLatitude(marker.getPosition().latitude);
        newLocation.setLongitude(marker.getPosition().longitude);
        showChangeLocationDialog();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
        else {

        }
    }


    private void showChangeLocationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage("Would you like to set this location?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCurrentLocation = newLocation;
                mMap.clear();
                showCustomMarker();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mMap.clear();
                showCustomMarker();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void showCustomMarker(){

        View markerLayout = mInflater.inflate(R.layout.custom_map_marker, null, false);
        ImageView userImage = (ImageView) markerLayout.findViewById(R.id.user_image_details);
        if(userImageUrl != null && !userImageUrl.equalsIgnoreCase("")){
            setMarkerImage(userImageUrl, userImage, new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

        } else {
            MarkerOptions markerOptions = new MarkerOptions()

                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, markerLayout)));


            if(mEditable)
                markerOptions.draggable(true);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(markerOptions);




        }



    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void setMarkerImage(String imageUrl, final ImageView imageView, final LatLng location){

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());

                } else {
                    // Default image...
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, (View) imageView.getParent())));
                if(mEditable)
                    markerOptions.draggable(true);
                mMap.addMarker(markerOptions);


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.latitude, location.longitude))
                        .zoom(16)
                        .bearing(0)
                        .tilt(40)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

    }





}
