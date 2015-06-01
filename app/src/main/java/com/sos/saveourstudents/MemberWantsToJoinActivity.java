package com.sos.saveourstudents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Xian on 5/16/2015.
 */
public class MemberWantsToJoinActivity extends AppCompatActivity implements View.OnClickListener {

    private String mQuestionId;
    private String mUserId;
    private String mType;
    FloatingActionButton acceptButton;
    FloatingActionButton declineButton;
    RelativeLayout userProfile;

    private String mIsGroupUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/inGroup?userId=";
    private String mRemoveMemberUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/removeUser?userId=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_join_layout);

        if (getIntent().getExtras() != null) {

            System.out.println("Extras: " + getIntent().getExtras());
            System.out.println("userId: " + getIntent().getExtras().getString("userId"));
            System.out.println("type: " + getIntent().getExtras().getString("type"));

            mUserId = getIntent().getExtras().getString("userId");
            mQuestionId = getIntent().getExtras().getString("questionId");
            mType = getIntent().getExtras().getString("type");
        }


        getUserInfo();


        userProfile = (RelativeLayout) findViewById(R.id.joining_member_info);
        acceptButton = (FloatingActionButton) findViewById(R.id.accept_fab);
        declineButton = (FloatingActionButton) findViewById(R.id.decline_fab);

        userProfile.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v == acceptButton) {
            removeUserFromCurrentGroup();
        } else if (v == declineButton) {
            finish();
        } else if (v == userProfile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

    }


    private void getUserImage(String imageUrl, final ImageView imageView) {

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        // If you are using normal ImageView
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
                }
            }
        });

    }


    private void getUserInfo() {

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", mUserId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getUserById?" + paramString;


        System.out.println("getUserById url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("getUserById result " + result);
                            if (result.getString("success").equalsIgnoreCase("1")) {

                                JSONObject userObject = result.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map");


                                String name = userObject.getString("first_name") + " " + userObject.getString("last_name");

                                TextView joiningMemberName = (TextView) findViewById(R.id.member_name);
                                joiningMemberName.setText(userObject.getString("first_name"));

                                TextView memberInfoName = (TextView) findViewById(R.id.joining_member_name);
                                memberInfoName.setText(name);

                                TextView memberInfoMajor = (TextView) findViewById(R.id.joining_member_major);
                                memberInfoMajor.setText(userObject.getString("major"));


                                if (userObject.has("image")) {
                                    ImageView userImage = (ImageView) findViewById(R.id.joining_member_image);
                                    getUserImage(userObject.getString("image"), userImage);
                                }


                            } else {
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

    private void addMembertoGroup() {

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));
        params.add(new BasicNameValuePair("userId", mUserId));
        params.add(new BasicNameValuePair("tutor", (mType.equalsIgnoreCase("2") ? 1 : 0) + "")); //((mType.equalsIgnoreCase("2") ? 1 : 0)+"")

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/acceptUser?" + paramString;


        System.out.println("adduser url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("adduser result " + result);
                            if (result.getString("success").equalsIgnoreCase("1")) {
                                finish();
                            } else {
                                //Error...
                                Toast.makeText(MemberWantsToJoinActivity.this, "Error Accepting user", Toast.LENGTH_SHORT).show();
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

    private void removeUserFromCurrentGroup() {
        String isJoinerInGroup = mIsGroupUrl + mUserId;
        JsonObjectRequest inGroupRequest = new JsonObjectRequest(Request.Method.GET, isJoinerInGroup,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = new JSONObject(response.toString());

                    if(result.getInt("expectResults") != 0) {
                        String removeMemberFromGroup = mRemoveMemberUrl + mUserId;
                        JsonObjectRequest removeMemberRequest =
                                new JsonObjectRequest(Request.Method.GET, removeMemberFromGroup,
                                        (JSONObject) null, new RemoveUserFromGroupResponseListener(),
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                        Singleton.getInstance().addToRequestQueue(removeMemberRequest);

                    }
                    else {
                        addMembertoGroup();
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
        Singleton.getInstance().addToRequestQueue(inGroupRequest);

    }

    private class RemoveUserFromGroupResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            addMembertoGroup();
        }
    }
}
