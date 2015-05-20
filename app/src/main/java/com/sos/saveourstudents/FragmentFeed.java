package com.sos.saveourstudents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentFeed extends Fragment {

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




        getQuestionData();










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

    private void getQuestionData() {



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


        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions"+
                "?latitude="+32.88006+ //TODO Actual Location
                "&longitude="+-117.2340133+ //TODO Actual Location
                filterListFix+
                "&limit="+50;


        System.out.println("URL: "+url);
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


    public static class CardManager {

        public static CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

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

                String firstName = mQuestionList.getJSONObject(position).getJSONObject("map").getString("first_name");
                String lastName = mQuestionList.getJSONObject(position).getJSONObject("map").getString("last_name");
                String theDate = mQuestionList.getJSONObject(position).getJSONObject("map").getString("date");
                String topic = mQuestionList.getJSONObject(position).getJSONObject("map").getString("topic");
                String text = mQuestionList.getJSONObject(position).getJSONObject("map").getString("text");

                System.out.println("Question " + position + ": " + mQuestionList.getJSONObject(position).getJSONObject("map"));
                viewHolder.nameText.setText(firstName + " " + lastName);

                //System.out.println("date: " + Singleton.getInstance().doDateLogic(theDate));
                viewHolder.questionText.setText(text);

                viewHolder.dateText.setText(Singleton.getInstance().doDateLogic(theDate));
                viewHolder.topicText.setText(topic);




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return mQuestionList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {

            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            public TextView topicText;
            public TextView distanceText;
            private MaterialRippleLayout rippleView;


            //Declare views here, dont fill them
            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.question_text);
                nameText = (TextView) itemView.findViewById(R.id.name_text);
                dateText = (TextView) itemView.findViewById(R.id.timestamp_text);
                topicText = (TextView) itemView.findViewById(R.id.topic_text);
                //distanceText = (TextView) itemView.findViewById(R.id.question_text);
                userImage = (ImageView) itemView.findViewById(R.id.user_image);
                rippleView = (MaterialRippleLayout) itemView.findViewById(R.id.ripple);
                rippleView.setOnTouchListener(this);
                userImage.setOnClickListener(this);
                nameText.setOnClickListener(this);

            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(v == rippleView && event.getAction() == MotionEvent.ACTION_UP){
                    Intent mIntent = new Intent(mContext, QuestionActivity.class);
                    mIntent.putExtra("type", 0);
                    startActivity(mIntent);
                }



                return false;
            }

            @Override
            public void onClick(View v) {
                //System.out.println("Clicked: "+v);
                if(v == nameText || v == userImage){
                    startActivity(new Intent(mContext, ProfileActivity.class));

                }
            }
        }

    }
}