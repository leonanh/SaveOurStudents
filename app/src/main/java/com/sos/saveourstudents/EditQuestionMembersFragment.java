package com.sos.saveourstudents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class EditQuestionMembersFragment extends Fragment {


    private String mQuestionId;
    private Context mContext;



    public static EditQuestionMembersFragment newInstance(String questionId) {
        EditQuestionMembersFragment fragment = new EditQuestionMembersFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuestionMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionId = getArguments().getString("questionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_view_group_members, container, false);

        mQuestionId = getArguments().getString("questionId");
        if(mQuestionId.equalsIgnoreCase(""))
            Toast.makeText(mContext, "QuestionId empty in viewQuestiomFrag", Toast.LENGTH_SHORT).show();

        getMemberData();


        return rootView;
    }

    private void getMemberData() {


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getMembers?"+paramString;


        System.out.println("url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("Members result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){








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

}
