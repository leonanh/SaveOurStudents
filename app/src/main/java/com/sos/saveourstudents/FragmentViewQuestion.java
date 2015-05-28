package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rey.material.widget.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FragmentViewQuestion extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LayoutInflater mInflater;
    private RecycleViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View rootView;
    private LinearLayout taglist;
    private FloatingActionButton fabButton;
    private TextView userName;
    private TextView questionDate;
    private TextView questionDistance;
    private TextView questionText;
    private ImageView userImage;


    private Context mContext;

    private String mQuestionId;


    private JSONObject mQuestionInfo;
    private ArrayList tags;


    public static FragmentViewQuestion newInstance(String questionId) {
        FragmentViewQuestion fragment = new FragmentViewQuestion();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentViewQuestion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mInflater = inflater;
        mContext = this.getActivity();

        rootView = inflater.inflate(R.layout.fragment_view_question, container,
                false);




        mQuestionId = getArguments().getString("questionId");
        if(mQuestionId.equalsIgnoreCase(""))
            Toast.makeText(mContext, "QuestionId empty in viewQuestiomFrag" , Toast.LENGTH_SHORT).show();


        userName = (TextView) rootView.findViewById(R.id.question_name_text);
        questionText = (TextView) rootView.findViewById(R.id.question_text);
        questionDate = (TextView) rootView.findViewById(R.id.question_timestamp);
        questionDistance = (TextView) rootView.findViewById(R.id.question_distance);

        taglist = (LinearLayout) rootView.findViewById(R.id.tag_list_layout);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        if(((EditQuestionActivity) getActivity()).mQuestionInfo == null) {
            getQuestionData();
            getCommentsData();
        }
        else{
            this.mQuestionInfo = ((EditQuestionActivity) getActivity()).mQuestionInfo;
            this.tags = ((EditQuestionActivity) getActivity()).tags;
            try {
                showQuestionDetails(mQuestionInfo);
                showQuestionTags(tags);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //TODO show fab after question data is retrieved
        fabButton = (FloatingActionButton) rootView.findViewById(R.id.group_action);
        fabButton.setVisibility(View.GONE);


        return rootView;
    }


    private void getQuestionData() {


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewQuestion?"+paramString;


        System.out.println("url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("edit questions result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){


                                JSONArray questionAndTags = result.getJSONObject("result").getJSONArray("myArrayList");

                                mQuestionInfo = questionAndTags.getJSONObject(0).getJSONObject("map");
                                tags = new ArrayList<>();
                                if(questionAndTags.length() > 1){
                                    for(int a = 1; a < questionAndTags.length(); a++){
                                       tags.add(questionAndTags.getJSONObject(a).getJSONObject("map").getString("tag"));
                                    }

                                    showQuestionTags(tags);
                                }

                                showQuestionDetails(mQuestionInfo);

                                buildFab(mQuestionInfo.getString("user_id"));


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


    private void getCommentsData() {


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getComments?"+paramString;


        System.out.println("getComments url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("comments result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){

                                JSONArray commentList = result.getJSONObject("result").getJSONArray("myArrayList");



                                mAdapter = new RecycleViewAdapter(commentList, R.layout.question_comment_item);
                                mRecyclerView.setAdapter(mAdapter);

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


    private void showQuestionDetails(JSONObject details) throws JSONException {

        System.out.println("Details:" + details);
        String userNameText = details.getString("first_name")+ " "+details.getString("last_name");
        String topicText = details.getString("topic");
        String question = details.getString("text");
        String dateText = details.getString("date");


        if(details.has("image")) {
            String userImageUrl = details.getString("image");
            getUserImage(userImageUrl, userImage);
        }

        boolean studyGroupBool = details.getBoolean("study_group");
        boolean tutorGroupBool = details.getBoolean("tutor");


        userName.setText(userNameText);
        questionText.setText(question);
        questionDate.setText(Singleton.getInstance().doDateLogic(dateText));



    }

    private void showQuestionTags(ArrayList<String> tags){

        //System.out.println("Tags:" + tags);

        if(tags != null && tags.size() > 0){
            for(int i = 0; i < tags.size(); i++) {
                TextView text = new TextView(mContext);
                text.setText("#"+tags.get(i)+ "   ");
                text.setTextColor(getResources().getColor(R.color.primary_dark));
                taglist.addView(text);
            }

        }


    }



    private void buildFab(String userId){

        if(userId != null && !userId.equalsIgnoreCase("")){
            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String currentUserId = sharedPref.getString("user_id", "");

            if(currentUserId.equalsIgnoreCase(userId)){
                //owner
                fabButton.setIcon(getResources().getDrawable(R.drawable.ic_create_white_18dp), false);
                fabButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do edit dialog??
                    }
                });
            }else{
                fabButton.setIcon(getResources().getDrawable(R.drawable.ic_person_add_white_18dp), false);
                fabButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showJoinDialog();
                    }
                });
            }
            fabButton.setVisibility(View.VISIBLE);

        }


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





    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }







    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private JSONArray commentList;
        private int rowLayout;

        public RecycleViewAdapter(JSONArray commentList, int rowLayout) {
            this.commentList = commentList;
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
                //System.out.println("question: "+mQuestionList.getJSONObject(position).getJSONObject("map"));

                String firstName = commentList.getJSONObject(position).getJSONObject("map").getString("first_name");
                String lastName = commentList.getJSONObject(position).getJSONObject("map").getString("last_name");
                String text = commentList.getJSONObject(position).getJSONObject("map").getString("text");

                boolean group = commentList.getJSONObject(position).getJSONObject("map").getBoolean("study_group");
                boolean tutor = commentList.getJSONObject(position).getJSONObject("map").getBoolean("tutor");



                String userImageUrl = "";
                if(commentList.getJSONObject(position).getJSONObject("map").has("image")){
                    userImageUrl = commentList.getJSONObject(position).getJSONObject("map").getString("image");
                }

                /*
                if(!userImageUrl.equalsIgnoreCase("")){
                    commentList(userImageUrl, viewHolder.userImage);
                }
*/

                //System.out.println("Question " + position + ": " + mQuestionList.getJSONObject(position).getJSONObject("map"));
                viewHolder.nameText.setText(firstName + " " + lastName);

                //System.out.println("date: " + Singleton.getInstance().doDateLogic(theDate));
                viewHolder.questionText.setText(text);


                //viewHolder.dateText.setText(Singleton.getInstance().doDateLogic(theDate));




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return commentList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {


            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            private CardView cardView;


            //Declare views here, dont fill them
            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.question_text);
                nameText = (TextView) itemView.findViewById(R.id.name_text);
                dateText = (TextView) itemView.findViewById(R.id.timestamp_text);

                userImage = (ImageView) itemView.findViewById(R.id.user_image_details);


                cardView = (CardView) itemView.findViewById(R.id.card_view);
                cardView.setOnTouchListener(this);
                userImage.setOnClickListener(this);
                nameText.setOnClickListener(this);

            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("touched : "+getAdapterPosition());

                /*
                if(v == cardView && event.getAction() == MotionEvent.ACTION_UP){

                    try {
                        String questionId = commentList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("question_id");
                        Intent mIntent = new Intent(mContext, ViewGroupActivity.class);
                        mIntent.putExtra("questionId", questionId);
                        startActivity(mIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
*/
                return false;
            }

            @Override
            public void onClick(View v) {

                if(v == nameText || v == userImage){

                    try {
                        String userId = commentList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("user_id");
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



    private void showJoinDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to join as a tutor or a group member?");

        builder.setPositiveButton("Member", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the tutor button
            }
        });
        builder.setNegativeButton("Tutor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the member button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();





    }



}

