package com.sos.saveourstudents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentFeed extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private SharedPreferences sharedPref;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    Location lastKnownLocation;
    private GoogleApiClient mGoogleApiClient;

    private final String TAG = "SOS Tag";
    private RecycleViewAdapter mAdapter;

    static CardManager mCardManagerInstance;
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Context mContext;

    private JSONArray mQuestionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.feed_layout, container,
                false);


        mContext = this.getActivity().getBaseContext();

        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(mContext);
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuestionData();
            }
        });

        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        buildGoogleApiClient();


        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        lastKnownLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));





        /**
         * Image Request Example
         */
/*
        String urlimage = "http://i01.i.aliimg.com/img/pb/487/830/416/416830487_639.jpg";
        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        // If you are using normal ImageView
        imageLoader.get(urlimage, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    //TODO imageview.setImageBitmap(response.getBitmap());
                }
            }
        });
*/




        return rootView;
    }

    public void getQuestionData() {


        getLocationUpdate();

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
        params.add(new BasicNameValuePair("latitude", latitude+""));
        params.add(new BasicNameValuePair("longitude", longitude + ""));
        for (int a = 0; a < myList.size(); a++) {
            params.add(new BasicNameValuePair("tags", myList.get(a)));
        }
        params.add(new BasicNameValuePair("limit", sharedPref.getInt("distance", 10)+""));


        String paramString = URLEncodedUtils.format(params, "utf-8").replace("+", "%20");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions?"+paramString;


        //System.out.println("URL: "+url);
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
                                //Error getting data, show dialog?
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
                            mSwipeRefreshLayout.setRefreshing(false);
                            showQuestions();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("Error: " + error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);


    }


    private void showQuestions(){

        mAdapter = new RecycleViewAdapter(R.layout.feed_item_layout);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        getQuestionData();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public static class CardManager {

        public static CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

    }

    public void getLocationUpdate(){
        createLocationRequest();
        startLocationUpdates();
    }



    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
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



    protected synchronized void buildGoogleApiClient() {
        mSwipeRefreshLayout.setRefreshing(true);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();


    }






    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private int rowLayout;

        public RecycleViewAdapter(int rowLayout) {
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            //viewHolder.questionText.setText(mCardManagerInstance.getCounters().get(i).title+"");
            //viewHolder.venueType.setText(mInstance.getCounters().get(i)+"");
            try {
                System.out.println("question: "+mQuestionList.getJSONObject(position).getJSONObject("map"));

                String firstName = mQuestionList.getJSONObject(position).getJSONObject("map").getString("first_name");
                String lastName = mQuestionList.getJSONObject(position).getJSONObject("map").getString("last_name");
                String theDate = mQuestionList.getJSONObject(position).getJSONObject("map").getString("date");
                String topic = mQuestionList.getJSONObject(position).getJSONObject("map").getString("topic");
                String text = mQuestionList.getJSONObject(position).getJSONObject("map").getString("text");

                boolean group = mQuestionList.getJSONObject(position).getJSONObject("map").getBoolean("study_group");
                boolean tutor = mQuestionList.getJSONObject(position).getJSONObject("map").getBoolean("tutor");

                double latitude = Double.parseDouble(mQuestionList.getJSONObject(position).getJSONObject("map").getString("latitude"));
                double longitude = Double.parseDouble(mQuestionList.getJSONObject(position).getJSONObject("map").getString("longitude"));

                if(group){
                    viewHolder.groupIcon.setColorFilter(getResources().getColor(R.color.primary_dark));
                }
                if(tutor){
                    viewHolder.tutorIcon.setColorFilter(getResources().getColor(R.color.primary_dark));
                }

                //System.out.println("Question " + position + ": " + mQuestionList.getJSONObject(position).getJSONObject("map"));
                viewHolder.nameText.setText(firstName + " " + lastName);

                //System.out.println("date: " + Singleton.getInstance().doDateLogic(theDate));
                viewHolder.questionText.setText(text);


                viewHolder.dateText.setText(Singleton.getInstance().doDateLogic(theDate));
                viewHolder.topicText.setText(topic);


                String distanceType = sharedPref.getString("distanceType", "MI");



                if(mCurrentLocation != null){
                    viewHolder.distanceText.setText(
                            Singleton.getInstance().doDistanceLogic(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                                    latitude, longitude, distanceType)+""+distanceType);
                }
                else if(lastKnownLocation != null){
                    System.out.println("mCurrentLocation is null, trying lastknown");
                    viewHolder.distanceText.setText(
                            Singleton.getInstance().doDistanceLogic(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                                    latitude, longitude, distanceType) + ""+distanceType);
                }
                else{
                    viewHolder.distanceText.setVisibility(View.INVISIBLE);
                    System.out.println("mCurrentLocation and lastknown is null");
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return mQuestionList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {

            public ImageView groupIcon;
            public ImageView tutorIcon;
            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            public TextView topicText;
            public TextView distanceText;
            private CardView cardView;


            //Declare views here, dont fill them
            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.question_text);
                nameText = (TextView) itemView.findViewById(R.id.name_text);
                dateText = (TextView) itemView.findViewById(R.id.timestamp_text);
                topicText = (TextView) itemView.findViewById(R.id.topic_text);
                distanceText = (TextView) itemView.findViewById(R.id.distance_text);
                userImage = (ImageView) itemView.findViewById(R.id.user_image_details);

                groupIcon = (ImageView) itemView.findViewById(R.id.group_icon);
                tutorIcon = (ImageView) itemView.findViewById(R.id.tutor_icon);
                cardView = (CardView) itemView.findViewById(R.id.card_view);
                cardView.setOnTouchListener(this);
                userImage.setOnClickListener(this);
                nameText.setOnClickListener(this);

            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("touched : "+getAdapterPosition());

                if(v == cardView && event.getAction() == MotionEvent.ACTION_UP){

                    try {
                        String questionId = mQuestionList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("question_id");
                        String userId = mQuestionList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("user_id");
                        Intent mIntent = new Intent(mContext, ViewGroupActivity.class);
                        mIntent.putExtra("questionId", questionId);
                        mIntent.putExtra("userId", userId);
                        startActivity(mIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return false;
            }

            @Override
            public void onClick(View v) {

                if(v == nameText || v == userImage){

                    try {
                        String userId = mQuestionList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("user_id");
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
}