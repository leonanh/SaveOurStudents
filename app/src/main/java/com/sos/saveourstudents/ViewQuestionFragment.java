package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.rey.material.widget.EditText;
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


public class ViewQuestionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, LocationListener {

    private final int EDIT_QUESTION = 2345;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private SharedPreferences sharedPref;

    private RecycleViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View rootView;
    private LinearLayout taglist;
    private FloatingActionButton fab;
    private ImageView sendButton;
    private EditText commentEditText;
    private TextView userName;
    private TextView questionDate;
    private TextView questionTopic;
    private TextView questionDistance;
    private TextView questionText;
    private ImageView userImage;
    private ImageView tutorIcon;
    private ImageView groupIcon;

    private boolean mStudyGroupBool;
    private boolean mTutorBool;


    private boolean mEditable;
    public Context mContext;
    private String mQuestionId;

    private JSONObject mQuestionInfo;
    private ArrayList tags;


    public static ViewQuestionFragment newInstance(String questionId, boolean isEditable) {
        ViewQuestionFragment fragment = new ViewQuestionFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        args.putBoolean("isEditable", isEditable);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.fragment_view_question, container, false);

        if (getArguments() != null) {
            mQuestionId = getArguments().getString("questionId");
            mEditable = getArguments().getBoolean("isEditable");
        }else{
            Toast.makeText(mContext, "QuestionId empty in viewQuestiomFrag" , Toast.LENGTH_SHORT).show();
        }

        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        buildGoogleApiClient();

        userImage = (ImageView) rootView.findViewById(R.id.question_image);



        userName = (TextView) rootView.findViewById(R.id.question_name_text);
        questionText = (TextView) rootView.findViewById(R.id.question_text);
        questionDate = (TextView) rootView.findViewById(R.id.question_timestamp);
        questionDistance = (TextView) rootView.findViewById(R.id.question_distance);
        questionTopic = (TextView) rootView.findViewById(R.id.question_topic_text);
        tutorIcon = (ImageView) rootView.findViewById(R.id.tutor_icon);
        groupIcon = (ImageView) rootView.findViewById(R.id.group_icon);

        taglist = (LinearLayout) rootView.findViewById(R.id.tag_list_layout);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton) rootView.findViewById(R.id.group_action);
        fab.setVisibility(View.INVISIBLE);

        sendButton = (ImageView) rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        commentEditText = (EditText) rootView.findViewById(R.id.comment_edittext);

        getCommentsData();

        if(((ViewQuestionActivity) getActivity()).mQuestionInfo == null) {
            getQuestionData();
        }
        else{
            mQuestionInfo = ((ViewQuestionActivity) getActivity()).mQuestionInfo;
            tags = ((ViewQuestionActivity) getActivity()).tags;
            try {
                showQuestionDetails(mQuestionInfo);
                showQuestionTags(tags);
                buildFab();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String ownerId = mQuestionInfo.getString("user_id");
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra("userId", ownerId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return rootView;
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
                                tags = new ArrayList<>();
                                if(questionAndTags.length() > 1){
                                    for(int a = 1; a < questionAndTags.length(); a++){
                                       tags.add(questionAndTags.getJSONObject(a).getJSONObject("map").getString("tag"));
                                    }

                                    showQuestionTags(tags);
                                }

                                userImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String ownerId = mQuestionInfo.getString("user_id");
                                            Intent intent = new Intent(mContext, ProfileActivity.class);
                                            intent.putExtra("userId", ownerId);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                showQuestionDetails(mQuestionInfo);

                                buildFab();


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
                showConnectionIssueDialog();
            }

        });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void getCommentsData() {

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getComments?"+paramString;


        //System.out.println("getComments url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("comments result "+result);
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
                showConnectionIssueDialog();
            }

        });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);



    }


    private void showQuestionDetails(JSONObject details) throws JSONException {

        //System.out.println("Details:" + details);
        String firstName = details.getString("first_name");

        if(firstName.length() > 14) {
            firstName = firstName.substring(0, 13).concat("...");
        }
        String userNameText = firstName + " " +details.getString("last_name");
        String topicText = details.getString("topic");
        String question = details.getString("text");
        String dateText = details.getString("date");
        double latitude = details.getDouble("latitude");
        double longitude = details.getDouble("longitude");


        if(details.has("image")) {
            String userImageUrl = details.getString("image");
            getUserImage(userImageUrl, userImage);
        }

        mStudyGroupBool = details.getBoolean("study_group");
        mTutorBool = details.getBoolean("tutor");


        userName.setText(userNameText);
        questionText.setText(question);
        questionTopic.setText(topicText);
        questionDate.setText(Singleton.getInstance().doDateLogic(dateText));

        if(mCurrentLocation != null)
            questionDistance.setText(Singleton.getInstance().doDistanceLogic(latitude, longitude, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), "MI")+"MI");
        else
            questionDistance.setVisibility(View.INVISIBLE);

        if(mTutorBool)
        tutorIcon.setColorFilter(getResources().getColor(R.color.primary));

        if(mStudyGroupBool)
        groupIcon.setColorFilter(getResources().getColor(R.color.primary));

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


    public void buildFab() {

            String currentUserId = sharedPref.getString("user_id", "");
            String questionOwner = "";
        try {
            questionOwner = mQuestionInfo.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(currentUserId.equalsIgnoreCase(questionOwner)){
                //owner
                fab.setIcon(getResources().getDrawable(R.drawable.ic_create_white_24dp), false);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(mContext, CreateQuestionActivity.class);
                        mIntent.putExtra("questionId", mQuestionId);
                        startActivityForResult(mIntent, EDIT_QUESTION);
                    }
                });
                fab.setVisibility(View.VISIBLE);
            }else{
                getGroupActiveStatus();
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
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        showConnectionIssueDialog();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showConnectionIssueDialog();
    }

    @Override
    public void onClick(View v) {
        if(v == sendButton){
            if(!commentEditText.getText().toString().equalsIgnoreCase("")) {
                commentEditText.clearError();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                addComment();
            }
            else{
                commentEditText.setError("");
            }


        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

    }

    private void showConnectionIssueDialog(){
        Toast.makeText(mContext, "Connection error, try again", Toast.LENGTH_SHORT).show();
        if( ((ViewQuestionActivity) getActivity()) != null)
            ((ViewQuestionActivity) getActivity()).mSnackBar.show();
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

            try {
                System.out.println("question: "+commentList.getJSONObject(position).getJSONObject("map"));

                String firstName = commentList.getJSONObject(position).getJSONObject("map").getString("first_name");
                String lastName = commentList.getJSONObject(position).getJSONObject("map").getString("last_name");
                String text = commentList.getJSONObject(position).getJSONObject("map").getString("comment");
                String date = commentList.getJSONObject(position).getJSONObject("map").getString("posted");


                if(commentList.getJSONObject(position).getJSONObject("map").has("image") &&
                        !commentList.getJSONObject(position).getJSONObject("map").getString("image").equalsIgnoreCase("")){
                    String userImageUrl = commentList.getJSONObject(position).getJSONObject("map").getString("image");
                    getUserImage(userImageUrl, viewHolder.userImage);
                }


                viewHolder.nameText.setText(firstName + " " + lastName);

                viewHolder.questionText.setText(text);
                viewHolder.dateText.setText(Singleton.getInstance().doDateLogic(date));



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return commentList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ImageView userImage;
            public TextView questionText;
            public TextView nameText;
            public TextView dateText;
            private CardView cardView;


            //Declare views here, dont fill them
            public ViewHolder(View itemView) {
                super(itemView);
                questionText = (TextView) itemView.findViewById(R.id.comment_text);
                nameText = (TextView) itemView.findViewById(R.id.comment_name_text);
                dateText = (TextView) itemView.findViewById(R.id.comment_timestamp);
                userImage = (ImageView) itemView.findViewById(R.id.comment_user_image);


                cardView = (CardView) itemView.findViewById(R.id.card_view);
                userImage.setOnClickListener(this);
                nameText.setOnClickListener(this);

            }


            @Override
            public void onClick(View v) {

                if((v == nameText || v == userImage)){

                    try {
                        String userId = commentList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("user_id");
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Missing UserId", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }

    }

    private void showRemoveYourselfDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to remove yourself from this group?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeYourselfFromGroup(sharedPref.getString("user_id", ""));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showJoinDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to join as a tutor or a group member?");

        if(mStudyGroupBool && mTutorBool) {
            builder.setPositiveButton("Member", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    sendAskToJoinGroup(0);
                }
            });
            builder.setNegativeButton("Tutor", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    sendAskToJoinGroup(1);
                }
            });
        } else if (mStudyGroupBool && !mTutorBool) {
            builder.setPositiveButton("Member", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    sendAskToJoinGroup(0);
                }
            });
        } else if (!mStudyGroupBool && mTutorBool) {
            builder.setPositiveButton("Tutor", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    sendAskToJoinGroup(1);
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addComment() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));
        params.add(new BasicNameValuePair("userId", sharedPref.getString("user_id", "")));
        params.add(new BasicNameValuePair("comment", commentEditText.getText().toString()));

        String paramString = URLEncodedUtils.format(params, "utf-8")
                .replaceAll("%27", "%27%27");

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/addComment?"+paramString;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("comments result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){
                                commentEditText.setText("");
                                getCommentsData();
                            }
                            else{
                                Toast.makeText(mContext, "Error posting comment", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
                System.out.println("Error with connection or url: " + error.toString());
            }

        });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void sendAskToJoinGroup(int type) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));
        params.add(new BasicNameValuePair("userId", sharedPref.getString("user_id", "")));
        params.add(new BasicNameValuePair("tutor", type + ""));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/askToJoinGroup?"+paramString;

        //System.out.println("sending group url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("sending group result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){
                                Toast.makeText(mContext, "Requested to join the group!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mContext, "Error Sending Group Request!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
                System.out.println("Error with connection or url: " + error.toString());
            }

        });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }



    private void getGroupActiveStatus(){

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", sharedPref.getString("user_id", "")));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/inGroup?"+paramString;

        System.out.println("getGroupActiveStatus url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());




                            System.out.println("getGroupActiveStatus result "+result);

                            if(result.getString("success").equalsIgnoreCase("1")){
                                if(result.getString("expectResults").equalsIgnoreCase("1")) {
                                    String questionId = result.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("question_id");

                                    if (questionId.equalsIgnoreCase(mQuestionId)) {
                                        //show remove yourself button

                                        fab.setIcon(getResources().getDrawable(R.drawable.ic_remove_white_24dp), false);
                                        fab.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //Do edit dialog??
                                                showRemoveYourselfDialog();
                                            }
                                        });
                                        fab.setVisibility(View.VISIBLE);
                                    } else {
                                        //Hide button, your in a group, but not this one.
                                    }
                                }
                                else{//Not in a group. Show want to add button
                                    fab.setIcon(getResources().getDrawable(R.drawable.ic_person_add_white_18dp), false);
                                    fab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showJoinDialog();
                                        }
                                    });
                                    fab.setVisibility(View.VISIBLE);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error with connection or url: " + error.toString());
                showConnectionIssueDialog();
            }

        });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


        private void removeYourselfFromGroup(final String userId) {

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("userId", userId));

            String paramString = URLEncodedUtils.format(params, "utf-8");
            String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/removeUser?" + paramString;


            System.out.println("removeUser url: " + url);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                    (JSONObject) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject result = new JSONObject(response.toString());
                                System.out.println("removeUser result " + result);
                                if (result.getString("success").equalsIgnoreCase("1")) {
                                    Toast.makeText(mContext, "User removed", Toast.LENGTH_SHORT).show();
                                    buildFab();
                                    //getActivity().setResult(getActivity().RESULT_OK);
                                    //getActivity().finish();
                                } else {
                                    Toast.makeText(mContext, "Error removing user", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error with connection or url: " + error.toString());
                    showConnectionIssueDialog();
                }

            });

            Singleton.getInstance().addToRequestQueue(jsObjRequest);

        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EDIT_QUESTION) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                ((ViewQuestionActivity) getActivity()).getQuestionData();
                mQuestionInfo = ((ViewQuestionActivity) getActivity()).mQuestionInfo;
                tags = ((ViewQuestionActivity) getActivity()).tags;
                try {
                    showQuestionDetails(mQuestionInfo);
                    showQuestionTags(tags);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

