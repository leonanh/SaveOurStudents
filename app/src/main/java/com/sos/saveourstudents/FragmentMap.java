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
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import com.android.volley.VolleyLog;
//import com.android.volley.toolbox.JsonArrayRequest;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentMap extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnInfoWindowClickListener, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    static final LatLng UCSD = new LatLng(32.88006, -117.234013);
    static final LatLng GEISEL = new LatLng(32.881151, -117.23744999999997);
    private GoogleMap mMap;
    private MapView mMapView;
    private Bundle mBundle;
    private Context mContext;

    private View rootView;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout detailsLayout;

    private JSONArray mQuestionList;
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

        detailsLayout = (RelativeLayout) rootView.findViewById(R.id.lower_layout);
        detailsLayout.setVisibility(View.GONE);
        detailsLayout.setOnClickListener(this);
        createAndShowMap();

        return rootView;
    }



    public void createAndShowMap(){

        buildGoogleApiClient();

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        mMap = mMapView.getMap();
        mMap.setOnMapClickListener(this);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //zoomToMyPosition();

        /*
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {


                // Getting view from the layout file info_window_layout
                View v = minflater.inflate(R.layout.info_window_layout, null, false);

                // Getting the position from the marker
                //LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                //TextView tv_lat = (TextView) v.findViewById(R.id.user_name);

                // Getting reference to the TextView to set longitude
                //TextView tv_lng = (TextView) v.findViewById(R.id.tv_lng);

                // Setting the latitude
                //tv_lat.setText("Latitude:" +latLng.latitude);

                // Setting the longitude
                //tv_lng.setText("Longitude:" + latLng.longitude);

                //TextView tvTitle = (TextView) v.findViewById(R.id.title);
                //tvTitle.setText(marker.getTitle());
                // TextView tvSnippet = ((TextView) v.findViewById(R.id.snippet));
                //tvSnippet.setText(arg0.getSnippet());

                // Returning the view containing InfoWindow contents
                return v;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                return null;
            }
        });
*/
        //mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

    }

    public void getMapData() {

        mMap.clear();

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

        List<String> myList = new ArrayList<String>();
        myList.addAll(filterList);

        String filterListFix = ""; //TODO not good

        if(myList.size() == 0){
            //Dont use any filters. Just return all questions in range
        } else {//Use filters
            for (int a = 0; a < myList.size(); a++) {
                filterListFix = filterListFix + "&tags=" + myList.get(a);
            }
        }

        double latitude = 32.88006;
        double longitude = -117.2340133;


        if(mCurrentLocation != null){
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }
        else{
            System.out.println("Could not get location, using UCSD as default");
        }


        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions"+
                "?latitude="+latitude+
                "&longitude="+longitude+
                filterListFix+
                "&limit="+50; //TODO from settings


        System.out.println("URL: " + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //System.out.println("response: "+response.toString()); //DEBUG


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


                            //System.out.println("mQuestionList "+mQuestionList);
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


            for (int i = 0; i < mQuestionList.length(); i++){

                try {
                    System.out.println("overlays: "+mQuestionList.getJSONObject(i).getJSONObject("map"));

                    double latitude = Double.parseDouble(mQuestionList.getJSONObject(i).getJSONObject("map").getString("latitude"));
                    double longitude = Double.parseDouble(mQuestionList.getJSONObject(i).getJSONObject("map").getString("longitude"));




                    View marker = minflater.inflate(R.layout.info_window_layout, null, false);

                    //TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
                    //numTxt.setText("27");

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            //.title(mQuestionList.getJSONObject(i).getJSONObject("map").getString("first_name"))
                            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, marker))));
                                    //.icon(BitmapDescriptorFactory.fromResource(R.layout.info_window_layout))

                            //.snippet(mQuestionList.getJSONObject(i).getJSONObject("map").getString("topic")));






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
        mMap.setMyLocationEnabled(true);
        System.out.println("Connected to GoogleApi: " + mCurrentLocation);
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
        System.out.println("Location: "+location);
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
        }
        else {
            //System.out.println("Map not visible");
            if((MainActivity) getActivity() != null)
                ((MainActivity) getActivity()).showFab();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        detailsLayout.setVisibility(View.GONE);
        ((MainActivity) getActivity()).showFab();
    }

    public void onInfoWindowClick(Marker marker) {
        ((MainActivity) getActivity()).hideFab();
        detailsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        System.out.println("Marker: "+marker.getId());
        //TODO change dialog info
        ((MainActivity) getActivity()).hideFab();
        detailsLayout.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent mIntent = new Intent(mContext, QuestionActivity.class);
        mIntent.putExtra("type", 0);
        startActivity(mIntent);
    }
}