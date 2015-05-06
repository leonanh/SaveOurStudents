package com.sos.saveourstudents;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deamon on 5/6/15.
 */
public class FragmentViewQuestion extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    RecycleViewAdapter mAdapter;
    RecyclerView mRecyclerView;

    private GoogleMap mMap;
    private MapView mMapView;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    static List mQuestionList;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getActivity();

        rootView = inflater.inflate(R.layout.fragment_view_question, container,
                false);

        //Call this method to initiate volley request
        getQuestionData();



        return rootView;


    }


    /**
     * Helper method to initiate Volley call and refresh UI
     */
    private void getQuestionData() {


        //TODO turn into server call (volley)
        mQuestionList = new ArrayList<Question>();
        for(int a = 0;a < 3;a++){
            Question temp = new Question("Question "+a);
            mQuestionList.add(temp);
        }



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewAdapter(CardManager.getCounters(), R.layout.feed_item_layout, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);



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
     * Handle card views
     */
    public static class CardManager {

        CardManager mCardManagerInstance = null;

        public CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

        public static List<Question> getCounters() {
            if (mQuestionList == null) {
                mQuestionList = new ArrayList<Question>();
            }
            return mQuestionList;
        }
    }


    /**
     * Handle heavy lifting for Recyclerview
     */

    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private static final int TYPE_MAP = 0;
        private static final int TYPE_ITEM = 1;
        private List<Question> questions;
        private int rowLayout;


        public class ViewHolder extends RecyclerView.ViewHolder {
            int Holderid;

            TextView textView;
            ImageView imageView;
            ImageView profile;
            TextView userName;
            TextView email;



            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                System.out.println("ViewType: "+viewType);
                //0 index should be a map
                if(viewType == TYPE_ITEM) {
                    //textView = (TextView) itemView.findViewById(R.id.textview);
                    //imageView = (ImageView) itemView.findViewById(R.id.imageview);
                    Holderid = 1;
                }
                else{
                    mMapView = (MapView) itemView.findViewById(R.id.map);
                    initializeMap();


                    Holderid = 0;
                }
            }


        }


        public RecycleViewAdapter(List<Question> questions, int rowLayout, Context context) {
            this.questions = questions;
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 1) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_layout,parent,false);
                ViewHolder vhItem = new ViewHolder(v, viewType);
                return vhItem;

            } else if (viewType == 0) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_map_item,parent,false);
                ViewHolder vhHeader = new ViewHolder(v, viewType);
                return vhHeader;

            }
            return null;

        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if(viewHolder.Holderid == 1) {
                //viewHolder.textView.setText(mNavTitles[position - 1]);
                //viewHolder.imageView.setImageResource(mIcons[position -1]);
            }
            else{
                //viewHolder.profile.setImageResource(profile);
                //viewHolder.userName.setText(questions.get(position-1).title);
                //viewHolder.email.setText(email);
            }

        }

        @Override
        public int getItemCount() {
            return questions == null ? 0 : questions.size();
        }

        // With the following method we check what type of view is being passed
        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return TYPE_MAP;

            return TYPE_ITEM;
        }


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
        mMap.getUiSettings().setScrollGesturesEnabled(false);

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





