package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



public class FragmentMap extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;

    private ImageView userImageDetails;
    private ImageView groupIcon;
    private ImageView tutorIcon;
    private TextView userNameDetails;
    private TextView topicDetails;
    private TextView questionDetails;
    private TextView timestampDetails;
    private TextView distanceDetails;

    private String clickedQuestionId;
    private String clickedUserId;

    private View rootView;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout detailsLayout;

    private JSONArray mQuestionList;
    private LayoutInflater minflater;

    SharedPreferences sharedPref;

    public FragmentMap() {
        this.mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        minflater = inflater;
        mContext = this.getActivity();
        rootView = inflater.inflate(R.layout.fragment_map_layout, container,
                false);

        detailsLayout = (RelativeLayout) rootView.findViewById(R.id.lower_layout);
        detailsLayout.setVisibility(View.GONE);
        detailsLayout.setOnClickListener(this);

        groupIcon = (ImageView) rootView.findViewById(R.id.group_icon);
        tutorIcon = (ImageView) rootView.findViewById(R.id.tutor_icon);

        userImageDetails = (ImageView) rootView.findViewById(R.id.user_image_details);
        userImageDetails.setOnClickListener(this);
        userNameDetails = (TextView) rootView.findViewById(R.id.name_text);
        topicDetails = (TextView) rootView.findViewById(R.id.topic_text);
        questionDetails = (TextView) rootView.findViewById(R.id.question_text);
        timestampDetails = (TextView) rootView.findViewById(R.id.timestamp_text);
        distanceDetails  = (TextView) rootView.findViewById(R.id.distance_text);


        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        createAndShowMap();

        return rootView;
    }

    public void createAndShowMap(){

        buildGoogleApiClient();

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(new Bundle());
        mMap = mMapView.getMap();


        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMapData() {




        Set<String> filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

        List<String> myList = new ArrayList<String>();
        myList.addAll(filterList);


        double latitude = 32.88006;
        double longitude = -117.2340133;

        if(mCurrentLocation != null){
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }
        else{
            System.out.println("Could not get location, using UCSD as default");
        }


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("latitude", latitude + ""));
        params.add(new BasicNameValuePair("longitude", longitude + ""));
        for (int a = 0; a < myList.size(); a++) {
            params.add(new BasicNameValuePair("tags", myList.get(a)));
        }
        params.add(new BasicNameValuePair("limit", sharedPref.getInt("distance", 10) + ""));


        String paramString = URLEncodedUtils.format(params, "utf-8").replace("+", "%20");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions?"+paramString;


        //System.out.println("URL: " + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject theResponse = new JSONObject(response.toString());


                            if(!theResponse.getString("success").equalsIgnoreCase("1")){
                                //Error getting data
                                return;
                            }
                            if(theResponse.getString("expectResults").equalsIgnoreCase("0")){
                                //No results to show (empty array returned)
                                mQuestionList = theResponse.getJSONObject("result").getJSONArray("myArrayList");
                            }
                            else{
                                mQuestionList = theResponse.getJSONObject("result").getJSONArray("myArrayList");
                            }

                            showOverlays();


                        } catch (JSONException e) {
                            e.printStackTrace();//TODO Error parsing overlays, show error
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                                                //TODO Error parsing overlays, show error
                System.out.println("Error: " + error.toString());

            }
        });

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }


    private void showOverlays(){

        if(mMap != null){


            mMap.clear();

            for (int i = 0; i < mQuestionList.length(); i++){

                try {

                    //System.out.println("question: "+mQuestionList.getJSONObject(i).getJSONObject("map"));
                    double latitude = Double.parseDouble(mQuestionList.getJSONObject(i).getJSONObject("map").getString("latitude"));
                    double longitude = Double.parseDouble(mQuestionList.getJSONObject(i).getJSONObject("map").getString("longitude"));

                    String userImageUrl = "";
                    if(mQuestionList.getJSONObject(i).getJSONObject("map").has("image")){
                        userImageUrl = mQuestionList.getJSONObject(i).getJSONObject("map").getString("image");
                    }


                    View marker = minflater.inflate(R.layout.custom_map_marker, null, false);
                    ImageView userImage = (ImageView) marker.findViewById(R.id.user_image_details);



                    if(userImageUrl != null && !userImageUrl.equalsIgnoreCase("")){
                        setMarkerImage(userImageUrl, i, userImage, new LatLng(latitude, longitude));
                    } else {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .snippet(i+"")
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, marker))));

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

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
        if(mMap != null && mMapView != null)
            mMapView.onResume();
        super.onResume();

    }

    @Override
    public void onPause() {
        stopLocationUpdates();
        mMapView.onPause();
        ((MainActivity) getActivity()).showFab();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        mMapView.onDestroy();
        super.onDestroy();
    }


    private void zoomToMyPosition(){

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));


        if(mCurrentLocation != null){

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else if (location != null)
        {
            mCurrentLocation = location;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //System.out.println("Connected to GoogleApi: " + mCurrentLocation);
        getLocationUpdate();

    }

    public void getLocationUpdate(){
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //System.out.println("Location: "+location);
        //Stop updates after we get a location....
        getMapData();
        stopLocationUpdates();
        zoomToMyPosition();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //System.out.println("Map is visible");

            //Update map?
        }
        else {
            //System.out.println("Map not visible");
            if((MainActivity) getActivity() != null) {
                ((MainActivity) getActivity()).showFab();
                detailsLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        detailsLayout.setVisibility(View.GONE);
        ((MainActivity) getActivity()).showFab();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {



        try {
            int position = Integer.parseInt(marker.getSnippet());

            String userImageUrl = "";
            if(mQuestionList.getJSONObject(position).getJSONObject("map").has("image")){
                userImageUrl = mQuestionList.getJSONObject(position).getJSONObject("map").getString("image");
            }

            if(!userImageUrl.equalsIgnoreCase("")){
                getUserImage(userImageUrl, userImageDetails);
            }else{
                userImageDetails.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
            }



            String name = mQuestionList.getJSONObject(position).getJSONObject("map").getString("first_name")+" "
                    +mQuestionList.getJSONObject(position).getJSONObject("map").getString("last_name");
            userNameDetails.setText(name);
            topicDetails.setText(mQuestionList.getJSONObject(position).getJSONObject("map").getString("topic"));
            questionDetails.setText(mQuestionList.getJSONObject(position).getJSONObject("map").getString("text"));
            timestampDetails.setText(Singleton.getInstance().doDateLogic(mQuestionList.getJSONObject(position).getJSONObject("map").getString("date")));
            clickedQuestionId = mQuestionList.getJSONObject(position).getJSONObject("map").getString("question_id");
            clickedUserId = mQuestionList.getJSONObject(position).getJSONObject("map").getString("user_id");


            //Tutor/Group Icons
            boolean group = mQuestionList.getJSONObject(position).getJSONObject("map").getBoolean("study_group");
            boolean tutor = mQuestionList.getJSONObject(position).getJSONObject("map").getBoolean("tutor");

            if(group){
                groupIcon.setColorFilter(getResources().getColor(R.color.primary_dark));
            }
            else{
                groupIcon.setColorFilter(getResources().getColor(R.color.hint_text_on_background));
            }
            if(tutor){
                tutorIcon.setColorFilter(getResources().getColor(R.color.primary_dark));
            }
            else{
                tutorIcon.setColorFilter(getResources().getColor(R.color.hint_text_on_background));
            }


            //Distance data
            double latitude = Double.parseDouble(mQuestionList.getJSONObject(position).getJSONObject("map").getString("latitude"));
            double longitude = Double.parseDouble(mQuestionList.getJSONObject(position).getJSONObject("map").getString("longitude"));


            String distanceType = sharedPref.getString("distanceType", "MI");

            if(mCurrentLocation != null){
                distanceDetails.setText(
                        Singleton.getInstance().doDistanceLogic(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                                latitude, longitude, distanceType)+""+distanceType);
            }
            else{
                distanceDetails.setVisibility(View.INVISIBLE);
                System.out.println("mCurrentLocation and lastknown is null");
            }




            showMapToolbar(marker.getPosition());
            ((MainActivity) getActivity()).hideFab();
            detailsLayout.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }



        return false;
    }

    @Override
    public void onClick(View v) {

        if(v == userImageDetails){
            Intent mIntent = new Intent(mContext, ProfileActivity.class);
            mIntent.putExtra("userId", clickedUserId);
            startActivity(mIntent);

        }
        else{
            Intent mIntent = new Intent(mContext, EditQuestionActivity.class);
            mIntent.putExtra("questionId", clickedQuestionId);
            startActivity(mIntent);

        }

    }

    private void setMarkerImage(String imageUrl, final int position, final ImageView imageView, final LatLng location){

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();

        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {

                    imageView.setImageBitmap(response.getBitmap());
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .snippet(position+"")
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, (View) imageView.getParent()))));

                } else {
                    // Default image...
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .snippet(position+"")
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, (View) imageView.getParent()))));
                }
            }
        });

    }

    private void getUserImage(String imageUrl, final ImageView imageView){

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();

        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {

                    imageView.setImageBitmap(response.getBitmap());

                } else {
                    // Default image...
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
                }
            }
        });

    }

    //TODO directions get covered by fab/details. Future fix custom toolbar
    private void showMapToolbar(LatLng location){

        /*
        // Directions
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?saddr=51.5, 0.125&daddr=51.5, 0.15"));
        startActivity(intent);

        // Default google map
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?q=loc:51.5, 0.125"));
        startActivity(intent);
        */
    }



}