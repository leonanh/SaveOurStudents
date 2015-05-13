package com.sos.saveourstudents;

import android.content.Context;
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

import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        //ImageLoader imageLoader = Singleton.getInstance().getImageLoader();

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

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            System.out.println("response: "+response.toString()); //DEBUG

                            JSONObject result = new JSONObject(response.toString());
                            if(!result.getString("success").equalsIgnoreCase("1")){
                                //Error getting data
                                return;
                            }
                            if(result.getString("expectResults").equalsIgnoreCase("0")){
                                //No results to show
                                return;
                            }
                            else{

                                mQuestionList = result.getJSONArray("result");
                            }


                            //System.out.println("popularTags "+popularTags.toString());
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



        /*
        //TODO Move into server call (volley)
        mQuestionList = new ArrayList<Question>();
        for(int a = 0;a<15;a++){
            Question temp = new Question("Question "+a);
            mQuestionList.add(temp);
        }*/

    }


    private void showQuestions(){




        mAdapter = new RecycleViewAdapter(R.layout.feed_item_layout_new);
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
                viewHolder.nameText.setText(mQuestionList.getJSONObject(position).getString("user_id"));

                viewHolder.questionText.setText(mQuestionList.getJSONObject(position).getString("text"));






            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            return mQuestionList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            public TextView distanceText;
            private RippleView rippleView;


            //Declare views here, dont fill them
            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.question_text);
                nameText = (TextView) itemView.findViewById(R.id.name_text);
                dateText = (TextView) itemView.findViewById(R.id.timestamp_text);
                //distanceText = (TextView) itemView.findViewById(R.id.question_text);
                //userImage = (ImageView) itemView.findViewById(R.id.question_text);
                rippleView = (RippleView) itemView.findViewById(R.id.more);
                rippleView.setOnTouchListener(this);


            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(v ==  rippleView && event.getAction() == MotionEvent.ACTION_UP){
                    System.out.println("Clicked Question");

                }



                return false;
            }

        }

    }
}