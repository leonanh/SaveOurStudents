package com.sos.saveourstudents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deamon on 4/21/15.
 */
public class FragmentFeed extends Fragment {

    private final String TAG = "SOS Tag";
    private RecycleViewAdapter mAdapter;

    static CardManager mCardManagerInstance;
    RecyclerView mRecyclerView;

    Context mContext;

    static List<Question> mQuestionList;

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



        //TODO Move into server call (volley)
        mQuestionList = new ArrayList<Question>();
        for(int a = 0;a<15;a++){
            Question temp = new Question("Question "+a);
            mQuestionList.add(temp);
        }



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewAdapter(CardManager.getInstance().getCounters(), R.layout.feed_item_layout, mContext);
        mRecyclerView.setAdapter(mAdapter);





        //VOLLEYExamples

        //String url ="http://54.200.33.91:8080/hello/";

        String url = "http://10.0.2.2:8080/com.mysql.services/rest/serviceclass/getVenues";



        TextView mTxtDisplay;
        ImageView mImageView;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, (JSONObject)null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Response: " + response.toString());
                        //mTxtDisplay.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("Error: " + error.toString());
                    }
                });

        // Access the RequestQueue through your singleton class.
        //Singleton.getInstance().addToRequestQueue(jsObjRequest);



        /**
         * JSON Array Example
         */
        // Tag used to cancel the request
        String tag_json_arry = "json_array_req";
        String url1 = "http://api.androidhive.info/volley/person_array.json";

        JsonArrayRequest req = new JsonArrayRequest(url1,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        //Singleton.getInstance().addToRequestQueue(req, tag_json_arry);

        /**
         * Image Request Example
         */
        String urlimage = "http://i01.i.aliimg.com/img/pb/487/830/416/416830487_639.jpg";
        //ImageLoader imageLoader = Singleton.getInstance().getImageLoader();

        // If you are using normal ImageView
        /*
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

    public static class CardManager {

        public static CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

        public List<Question> getCounters() {
            if (mQuestionList == null) {
                mQuestionList = new ArrayList<Question>();

            }
            return mQuestionList;
        }
    }

    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private List<Question> questions;
        private int rowLayout;

        public RecycleViewAdapter(List<Question> questions, int rowLayout, Context context) {
            this.questions = questions;
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            //viewHolder.questionText.setText(mCardManagerInstance.getCounters().get(i).title+"");
            //viewHolder.venueType.setText(mInstance.getCounters().get(i)+"");

			/*
			Counter counter = counters.get(i);
			viewHolder.counterName.setText(counter.name);
			viewHolder.counterIncrement.setText(counter.increment+"");
			viewHolder.counterTotal.setText(counter.total+"");

			int color = CounterManager.getInstance().getCounters().get(i).color;

			viewHolder.rippleView.setRippleColor(color);
			viewHolder.counterName.setTextColor(color);
			viewHolder.counterIncrement.setTextColor(color);
			viewHolder.counterTotal.setTextColor(color);

			viewHolder.upArrow.setColorFilter(color);
			viewHolder.downArrow.setColorFilter(color);
			*/

        }

        @Override
        public int getItemCount() {
            return questions == null ? 0 : questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            public TextView distanceText;
            private RippleView rippleView;


            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.question_text);
                nameText = (TextView) itemView.findViewById(R.id.question_text);
                dateText = (TextView) itemView.findViewById(R.id.question_text);
                distanceText = (TextView) itemView.findViewById(R.id.question_text);
                //userImage = (ImageView) itemView.findViewById(R.id.question_text);
                rippleView = (RippleView) itemView.findViewById(R.id.more);
                rippleView.setOnTouchListener(this);

				/*
				counterName = (TextView) itemView.findViewById(R.id.counter_name);
				counterIncrement = (TextView) itemView.findViewById(R.id.counter_increment);
				counterTotal = (TextView) itemView.findViewById(R.id.counter_total);
				cardView = (CardView) itemView.findViewById(R.id.cardview);


				upArrow = (ImageView) itemView.findViewById(R.id.up_image_button);
				downArrow = (ImageView) itemView.findViewById(R.id.down_image_button);

				upArrow.setOnClickListener(this);
				downArrow.setOnClickListener(this);


				*/
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(v ==  rippleView && event.getAction() == MotionEvent.ACTION_DOWN){
                    System.out.println("Clicked Question");

                }



                return false;
            }

        }

    }
}