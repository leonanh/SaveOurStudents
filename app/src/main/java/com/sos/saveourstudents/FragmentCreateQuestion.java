package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentCreateQuestion extends Fragment implements View.OnClickListener {

    private Context mContext;

    TextView questionText;
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

        flowLayout = (ViewGroup) rootView.findViewById(R.id.flow_container);
        questionText = (TextView) rootView.findViewById(R.id.question_text);
        questionText.setOnClickListener(this);
        //Call this method to initiate volley request
        getTagData();


        return rootView;


    }

    private void showTags(){

        View tagView = null;
            try {

                for(int a = 0; a < popularTags.length(); a++){

                    System.out.println("want to display tag: "+popularTags.getJSONObject(a));
                    tagView = inflater.inflate(R.layout.tag_item_layout, null, false);
                    tagView.findViewById(R.id.the_linear).setOnClickListener(this);
                    TextView tagText = (TextView) tagView.findViewById(R.id.tag_text);
                    tagText.setText(popularTags.getJSONObject(a).getString("tag"));
                    flowLayout.addView(tagView);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }




    }


    private void getTagData(){


        /**
         * JSON Array Example
         */
        String tag_json_arry = "json_array_req";
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getTags";


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

                            showTags();

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










/*

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

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
                                //popularTags = result.getJSONArray("result");
                            }


                            System.out.println("popularTags "+popularTags.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        //showTags();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("error: "+error);
            }
        });



        // Adding request to request queue
        Singleton.getInstance().addToRequestQueue(req, tag_json_arry);

*/

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

        } else if (v.getId() == R.id.question_text) {

            final com.rey.material.widget.EditText input = new com.rey.material.widget.EditText(mContext);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.TOP;
            lp.setMargins(10,10,10,10);
            input.setLayoutParams(lp);
            //
            //input.setLayoutParams(lp);

            new AlertDialog.Builder(mContext)
                    .setTitle("Post Question")
                    .setMessage("")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable value = input.getText();
                            System.out.println("wrote: "+value);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing.
                }
            }).show();

        }



    }


}
