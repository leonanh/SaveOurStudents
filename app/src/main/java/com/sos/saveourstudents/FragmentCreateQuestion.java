package com.sos.saveourstudents;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentCreateQuestion extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        Response.Listener, Response.ErrorListener {

    private Context mContext;
    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    private EditText questionEditText, topicEditText;

    private ImageView sendButton, addTagsButton;
    LayoutInflater inflater;
    ViewGroup flowLayout;
    View rootView;
    JSONArray popularTags = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getActivity();
        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.fragment_create_question, container,
                false);

        sendButton = (ImageView) rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        addTagsButton = (ImageView) rootView.findViewById(R.id.add_tag_button);
        addTagsButton.setOnClickListener(this);

        questionEditText = (EditText) rootView.findViewById(R.id.question_edit_text);
        topicEditText = (EditText) rootView.findViewById(R.id.topic_edit_text);


        buildGoogleApiClient();


        //If edit, get question info from server
        //If create, set variables to null


        return rootView;


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        //System.out.println(v.getId() == R.id.the_linear);
        if(v.getId() == R.id.the_linear){

            System.out.println("v is selected: " + v.isSelected());
            v.setSelected(!v.isSelected());

        }
        else if(v == sendButton){
            System.out.println("Location: "+mCurrentLocation);
            if (!topicEditText.getText().toString().equalsIgnoreCase("")) {
                topicEditText.clearError();

                if (!questionEditText.getText().toString().equalsIgnoreCase("")) {
                    questionEditText.clearError();

                    if(mCurrentLocation != null){
                        sendQuestionToServer();
                        //postQuestion();
                    }
                    else{
                        Toast.makeText(mContext, "Cant find location...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Question edit text empty
                    questionEditText.setError("Question is empty");
                }

            } else {
                //Topic empty
                topicEditText.setError("Topic is empty");
            }


        }
        else if(v == addTagsButton){

            //TODO show tags dialog
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();

            DialogFragment newFragment = new TagDialogFragment(mContext, 1);

            newFragment.show(getActivity().getSupportFragmentManager(), "");


        }


    }



    private void sendQuestionToServer(){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String userId = sharedPref.getString("user_id", "");



/*
        String filterListFix = "";

        if(myList.size() == 0){
            //Dont use any filters. Just return all questions in range
        } else {//Use filters
            for (int a = 0; a < myList.size(); a++) {
                filterListFix = filterListFix + "&tags=" + myList.get(a);
            }
        }
        */

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createQuestion";


        String uri = Uri.parse("http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createQuestion")
                .buildUpon()
                .appendQueryParameter("userId", "39uo830mgaqfbmctt6j9tkt8ab")
                .appendQueryParameter("latitude", mCurrentLocation.getLatitude()+"")
                .appendQueryParameter("longitude", mCurrentLocation.getLongitude()+"")
                .appendQueryParameter("text", questionEditText.getText().toString())
                .appendQueryParameter("tags", "UCSD")
                .appendQueryParameter("tutor", "0")
                .appendQueryParameter("studygroup", "1")
                .appendQueryParameter("topic", topicEditText.getText().toString())

                .build().toString();

        System.out.println("URI: "+uri);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("result "+result);
                            if(!result.getString("success").equalsIgnoreCase("1")){
                                //Error getting data
                                return;
                            }
                            else{
                                //TODO Show alert for success
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("Error: " + error.toString());
            }

        });



        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


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


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener( this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        System.out.println("Connected to GoogleApi: "+mCurrentLocation);
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
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {

    }
}
