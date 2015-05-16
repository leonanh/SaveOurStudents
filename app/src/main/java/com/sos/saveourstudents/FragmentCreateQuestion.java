package com.sos.saveourstudents;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentCreateQuestion extends Fragment implements View.OnClickListener {

    private Context mContext;

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



        //If edit, get question info from server
        //If create, set variables to null

        return rootView;


    }



    private void getQuestionData(){



        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestion";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

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
                                popularTags = result.getJSONArray("result");
                            }


                            System.out.println("popularTags "+popularTags.toString());

                            //showTags();

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

            if (!topicEditText.getText().toString().equalsIgnoreCase("")) {
                topicEditText.clearError();

                if (!questionEditText.getText().toString().equalsIgnoreCase("")) {
                    questionEditText.clearError();

                    //TODO sendQuestionToServer();
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

        }


    }



    private void sendQuestionToServer(){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String userId = sharedPref.getString("user_id", "");


         /*

         editor.putString("first_name", response.getJSONArray("result").getJSONObject(0).getString("first_name"));
                        editor.putString("last_name", response.getJSONArray("result").getJSONObject(0).getString("last_name"));
                        editor.putString("email", response.getJSONArray("result").getJSONObject(0).getString("email"));
                        editor.putString("image", response.getJSONArray("result").getJSONObject(0).getString("image"));
                        editor.putString("user_id", response.getJSONArray("result").getJSONObject(0).getString("user_id"));


          */
        System.out.println("Im'a send this question to server");

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createQuestion?"+
                "userId="+userId+
                "&latitude="+
                "&longitude="+
                "&text="+
                "&tags="+
                "&tutor="+
                "&studygroup=";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject)null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

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
                                popularTags = result.getJSONArray("result");
                            }


                            System.out.println("popularTags "+popularTags.toString());

                            //showTags();

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




}
