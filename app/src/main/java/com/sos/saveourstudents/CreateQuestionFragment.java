package com.sos.saveourstudents;

import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rey.material.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;



public class CreateQuestionFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        Response.Listener, Response.ErrorListener,TagDialogFragment.NoticeDialogListener {



    public final int DIALOG_FRAGMENT = 1;
    private String mQuestionId;
    private ArrayList<String> tagList;
    private JSONObject mQuestionInfo;

    private SharedPreferences sharedPref;
    private Context mContext;
    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    private EditText questionEditText, topicEditText;
    private ImageView userImage;
    private ImageView sendButton, addTagsButton, locationToggle, groupToggle, tutorToggle;
    private TextView userName, requestGroupText, requestTutorText;
    private boolean showLocation = true;
    private boolean isInEditMode = false; //For response back to edit question frag
    LayoutInflater inflater;
    View rootView;


    public static CreateQuestionFragment newInstance(String questionId) {
        CreateQuestionFragment fragment = new CreateQuestionFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateQuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        this.inflater = inflater;

        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        rootView = inflater.inflate(R.layout.fragment_create_question, container,
                false);


        sendButton = (ImageView) rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        addTagsButton = (ImageView) rootView.findViewById(R.id.add_tag_button);
        addTagsButton.setOnClickListener(this);

        locationToggle = (ImageView) rootView.findViewById(R.id.location_toggle);
        locationToggle.setOnClickListener(this);

        requestGroupText = (TextView) rootView.findViewById(R.id.study_group_text);
        requestGroupText.setOnClickListener(this);
        groupToggle = (ImageView) rootView.findViewById(R.id.study_group_toggle);
        groupToggle.setOnClickListener(this);
        groupToggle.setSelected(false);

        requestTutorText = (TextView) rootView.findViewById(R.id.tutor_text);
        requestTutorText.setOnClickListener(this);
        tutorToggle = (ImageView) rootView.findViewById(R.id.tutor_toggle);
        tutorToggle.setOnClickListener(this);
        tutorToggle.setSelected(false);

        userName = (TextView) rootView.findViewById(R.id.question_name_text);
        questionEditText = (EditText) rootView.findViewById(R.id.question_edit_text);
        topicEditText = (EditText) rootView.findViewById(R.id.topic_edit_text);
        userImage = (ImageView) rootView.findViewById(R.id.question_user_image);

        buildGoogleApiClient();

        tagList = new ArrayList<String>();

        String name = sharedPref.getString("first_name", "") + " " + sharedPref.getString("last_name", "");
        userName.setText(name);
        getUserImage(sharedPref.getString("image", "image"), userImage);

        if (getArguments() != null) {
            mQuestionId = getArguments().getString("questionId");
            if(!mQuestionId.equalsIgnoreCase("")){
                getQuestionData();
                isInEditMode = true;
            }

        }


        return rootView;


    }


    private void clickGroupButton(){
        if(!groupToggle.isSelected()) {
            groupToggle.setColorFilter(getResources().getColor(R.color.primary_dark));
            requestGroupText.setTextColor(getResources().getColor(R.color.primary_dark));
        }
        else{
            groupToggle.setColorFilter(getResources().getColor(R.color.hint_text));
            requestGroupText.setTextColor(getResources().getColor(R.color.hint_text));
        }
        groupToggle.setSelected(!groupToggle.isSelected());
    }

    private void clickTutorButton(){
        if(!tutorToggle.isSelected()) {
            tutorToggle.setColorFilter(getResources().getColor(R.color.primary_dark));
            requestTutorText.setTextColor(getResources().getColor(R.color.primary_dark));
        }
        else{
            tutorToggle.setColorFilter(getResources().getColor(R.color.hint_text));
            requestTutorText.setTextColor(getResources().getColor(R.color.hint_text));
        }
        tutorToggle.setSelected(!tutorToggle.isSelected());

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

        if(v == sendButton){
            //System.out.println("Location: " + mCurrentLocation);

            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(questionEditText.getWindowToken(), 0);



            boolean error = false;
            if(topicEditText.getText().toString().equalsIgnoreCase("")) {
                topicEditText.setError("Topic is empty");
                error = true;
            }
            else{topicEditText.clearError();}
            if(questionEditText.getText().toString().equalsIgnoreCase("")) {
                questionEditText.setError("Question is empty");
                error = true;
            }
            else{questionEditText.clearError();}
            if(mCurrentLocation == null && showLocation){
                Toast.makeText(mContext, "Cant find location", Toast.LENGTH_SHORT).show();
                error = true;
            }

            if(!error){
                sendQuestionToServer();
            }


        }
        else if(v == addTagsButton){

            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();

            DialogFragment newFragment = new TagDialogFragment(mContext, DIALOG_FRAGMENT);
            newFragment.setTargetFragment(CreateQuestionFragment.this, DIALOG_FRAGMENT);
            Bundle listbundle = new Bundle();
            listbundle.putStringArrayList("list", tagList);

            newFragment.setArguments(listbundle);
            newFragment.show(getActivity().getSupportFragmentManager(), "");


        }
        else if(v == locationToggle){
            if(showLocation){
                locationToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_off_grey600_36dp));
            }
            else{
                locationToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_grey600_36dp));
            }

            showLocation = !showLocation;
        }
        else if(v == groupToggle || v == requestGroupText){
            clickGroupButton();

        }
        else if(v == tutorToggle || v == requestTutorText){
            clickTutorButton();


        }
    }


    private void getQuestionData() {


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewQuestion?"+paramString;


        //System.out.println("url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("edit questions result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){


                                JSONArray questionAndTags = result.getJSONObject("result").getJSONArray("myArrayList");

                                mQuestionInfo = questionAndTags.getJSONObject(0).getJSONObject("map");
                                tagList = new ArrayList<>();
                                if(questionAndTags.length() > 1){
                                    for(int a = 1; a < questionAndTags.length(); a++){
                                        tagList.add(questionAndTags.getJSONObject(a).getJSONObject("map").getString("tag"));
                                    }
                                }

                                showQuestionDetails();

                            }
                            else{
                                //Error...
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error with connection or url: " + error.toString());
            }

        });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }



    private void sendQuestionToServer(){

        double latitude = 0.0;
        double longitude = 0.0;

        if (mCurrentLocation == null) {
            if (getLastKnownLocation() != null) {
                //System.out.println("Using last known location, which is not current. So its probably wrong.");
                mCurrentLocation = getLastKnownLocation();
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();
            } else {
                if (showLocation && !isInEditMode) {
                    //System.out.println("Error getting current or last known location. Unable to send this post");
                    Toast.makeText(mContext, "Unable to send this post", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                }

            }
        }
        else{
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", sharedPref.getString("user_id", "")));

        if(!isInEditMode){
            params.add(new BasicNameValuePair("latitude", latitude+""));
            params.add(new BasicNameValuePair("longitude", longitude + ""));
        }

        params.add(new BasicNameValuePair("text", questionEditText.getText().toString()));

        for (int a = 0; a < tagList.size(); a++) {
            params.add(new BasicNameValuePair("tags", tagList.get(a)));
        }
        params.add(new BasicNameValuePair("tutor", (tutorToggle.isSelected() ? 1 : 0)+""));
        params.add(new BasicNameValuePair("studyGroup", (groupToggle.isSelected() ? 1 : 0)+""));
        params.add(new BasicNameValuePair("topic", topicEditText.getText().toString()));
        params.add(new BasicNameValuePair("visibleLocation", (showLocation ? 1 : 0)+""));


        String paramString = URLEncodedUtils.format(params, "utf-8").replace("+", "%20");

        String url;
        if(isInEditMode)
            url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/editQuestion?"+paramString;
        else
            url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createQuestion?"+paramString;


        System.out.println("create/edit url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("result "+result);
                            if(!result.getString("success").equalsIgnoreCase("1")){

                                //Error...
                                if(result.getString("result").startsWith("Duplicate")){
                                    Toast.makeText(mContext, "You already have an active post", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    if(isInEditMode)
                                        Toast.makeText(mContext, "Error editing question", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(mContext, "Error creating question", Toast.LENGTH_SHORT).show();

                                }

                            }
                            else{
                                if(isInEditMode)
                                    sendNotification("Successfully Edited Question");
                                else
                                    sendNotification("Successfully Created Question");

                                getActivity().finishActivity(getActivity().RESULT_OK);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });


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


    private Location getLastKnownLocation() {

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            return location;
        }

        return null;
    }


    @Override
    public void passTagList(DialogFragment dialog, Set<String> activeFilters) {
        System.out.println("activeFilters: "+activeFilters.toString());
        if(activeFilters.size() > 0 ){
            tagList.clear();
            tagList.addAll(activeFilters);
            addTagsButton.setColorFilter(getResources().getColor(R.color.primary_dark));
        }
        else{
            tagList.clear();
            addTagsButton.setColorFilter(getResources().getColor(R.color.hint_text));
        }

    }


    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, ViewGroupActivity.class), 0);

        //Add question ID to intent

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("SaveOurStudents")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setTicker(msg)
                        .setContentText(msg);

        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(12345, mBuilder.build());
    }




    private void showQuestionDetails(){


        System.out.println("ShowQuestionDetails: " +mQuestionInfo);

        try {



            boolean studyBool = mQuestionInfo.getBoolean("study_group");
            boolean tutorBool = mQuestionInfo.getBoolean("tutor");

            if(tutorBool)
                clickTutorButton();

            if(studyBool)
                clickGroupButton();


            questionEditText.setText(mQuestionInfo.getString("text"));
            topicEditText.setText(mQuestionInfo.getString("topic"));


            if(tagList.size() > 0){
                addTagsButton.setColorFilter(getResources().getColor(R.color.primary_dark));
            }



            //TODO need visibility bool


        } catch (JSONException e) {
            e.printStackTrace();
        }








    }






}