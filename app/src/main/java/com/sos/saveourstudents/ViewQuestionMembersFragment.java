package com.sos.saveourstudents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class ViewQuestionMembersFragment extends Fragment {


    ArrayList<JSONObject> mTutorList = null;
    ArrayList<JSONObject> mStudentList = null;

    private ListView mStudentsListView;
    private ListView mTutorsListView;
    private StudentsArrayAdapter mStudentsListViewArrayAdapter;
    private TutorsArrayAdapter mTutorsListViewArrayAdapter;

    private SharedPreferences sharedPref;

    private String mQuestionId;
    private Context mContext;
    private boolean mEditable;


    public static ViewQuestionMembersFragment newInstance(String questionId, boolean editable) {

        ViewQuestionMembersFragment fragment = new ViewQuestionMembersFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        args.putBoolean("editable", editable);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewQuestionMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            mQuestionId = getArguments().getString("questionId");
            mEditable = getArguments().getBoolean("editable");
        }else{
            //error
        }

        mContext = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_view_group_members, container, false);

        sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mStudentsListView = (ListView) rootView.findViewById(R.id.view_group_members_students_listView);
        mTutorsListView = (ListView) rootView.findViewById(R.id.view_group_members_tutors_listView);


        getMemberData();

        return rootView;
    }

    private void getMemberData() {


        //final String currentUserId = sharedPref.getString("user_id", "");


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewMembers?"+paramString;


        //System.out.println("view member url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("Members result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){
                                mStudentList = null;
                                mTutorList = null;


                                JSONArray memberList = result.getJSONObject("result").getJSONArray("myArrayList");
                                for(int a = 0; a < memberList.length();a++){

                                    System.out.println("adding: "+memberList.getJSONObject(a).getJSONObject("map"));
                                    if(memberList.getJSONObject(a).getJSONObject("map").getBoolean("tutor")){
                                        if(mTutorList == null){
                                            mTutorList = new ArrayList<JSONObject>();
                                            mTutorList.add(memberList.getJSONObject(a).getJSONObject("map"));
                                        }
                                        else{
                                            mTutorList.add(memberList.getJSONObject(a).getJSONObject("map"));
                                        }
                                    }else{
                                        if(mStudentList == null){
                                            mStudentList = new ArrayList<JSONObject>();
                                            mStudentList.add(memberList.getJSONObject(a).getJSONObject("map"));
                                        }
                                        else{
                                            mStudentList.add(memberList.getJSONObject(a).getJSONObject("map"));
                                        }
                                    }

                                }


                                if(mTutorList != null && mTutorList.size() > 0) {
                                    mTutorsListViewArrayAdapter = new TutorsArrayAdapter(mContext, mTutorList);
                                    mTutorsListView.setAdapter(mTutorsListViewArrayAdapter);
                                }

                                if(mStudentList != null && mStudentList.size() > 0) {
                                    mStudentsListViewArrayAdapter = new StudentsArrayAdapter(mContext, mStudentList);
                                    mStudentsListView.setAdapter(mStudentsListViewArrayAdapter);
                                }


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


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class StudentsArrayAdapter extends ArrayAdapter<JSONObject> {
        private List<JSONObject> data;

        public StudentsArrayAdapter(Context context, List<JSONObject> objects) {
            super(context, 0, objects);
            data = objects;

        }

        @Override
        public JSONObject getItem(int position) {
            return data.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final JSONObject currStudentMember = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.view_group_members_student_layout, parent, false);
            }
            try {
                String name = currStudentMember.getString("first_name")+" " +
                        currStudentMember.getString("last_name");
                ((TextView) convertView.findViewById(R.id.user_name)).setText(name);

                if(currStudentMember.has("image"))
                    getUserImage(currStudentMember.getString("image"), ((ImageView) convertView.findViewById(R.id.user_image)));


                final String userId = currStudentMember.getString("user_id");

                if (!userId.equalsIgnoreCase(sharedPref.getString("user_id", "")) && mEditable) {
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showRemoveUserDialog(userId);
                            return true;
                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(getActivity(), ProfileActivity.class)
                                .putExtra("userId", currStudentMember.getString("user_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



            return convertView;
        }
    }

    /**
     * Array adapter to be used with the Tutors ListView
     * Incorporates Thumbs Up/Down functionality
     */
    private class TutorsArrayAdapter extends ArrayAdapter<JSONObject> {
        private List<JSONObject> data;

        public TutorsArrayAdapter(Context context, List<JSONObject> objects) {
            super(context, 0, objects);
            data = objects;

        }

        @Override
        public JSONObject getItem(int position) {
            return data.get(position);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final JSONObject currStudentMember = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_group_members_tutors_layout, parent, false);
            }

            try {
                ((TextView) convertView.findViewById(R.id.tutor_firstName))
                        .setText(currStudentMember.getString("first_name"));

                ((TextView) convertView.findViewById(R.id.tutor_lastName))
                        .setText(currStudentMember.getString("last_name"));


                if(currStudentMember.has("image"))
                    getUserImage(currStudentMember.getString("image"), ((ImageView) convertView.findViewById(R.id.tutor_profile_image)));


                final String userId = currStudentMember.getString("user_id");



                if (!userId.equalsIgnoreCase(sharedPref.getString("user_id", "")) && mEditable) {
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showRemoveUserDialog(userId);

                            return true;
                        }
                    });

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(getActivity(), ProfileActivity.class)
                                .putExtra("userId", currStudentMember.getString("user_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            ImageView mThumbsUpButton = ((ImageView) convertView.findViewById(R.id.view_group_members_thumbs_up));
            ImageView mThumbsDownButton = ((ImageView) convertView.findViewById(R.id.view_group_members_thumbs_down));

            if(mEditable){
                mThumbsUpButton.setVisibility(View.VISIBLE);
                mThumbsDownButton.setVisibility(View.VISIBLE);
                setUpThumbsUpButton(mThumbsUpButton, mThumbsDownButton);
                setUpThumbsDownButton(mThumbsUpButton, mThumbsDownButton);

            }
            else{
                mThumbsUpButton.setVisibility(View.GONE);
                mThumbsDownButton.setVisibility(View.GONE);
            }



            return convertView;
        }


        private void setUpThumbsUpButton(final ImageView mThumbsUpButton,
                                         final ImageView mThumbsDownButton) {
            mThumbsUpButton.setSelected(false);
            mThumbsUpButton.setColorFilter(getResources().getColor(R.color.divider_color));
            mThumbsUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mThumbsUpButton.isSelected()) {
                        Log.v("ThumbsUp", "SettingLightBlue");
                        mThumbsUpButton.setColorFilter(getResources().getColor(R.color.primary));
                        mThumbsUpButton.setSelected(true);
                        // Case 2 - Thumbs down button is currently clicked
                        if (mThumbsDownButton.isSelected()) {
                            mThumbsDownButton.setColorFilter(getResources().getColor(R.color.divider_color));
                            mThumbsDownButton.setSelected(false);
                        }
                    }
                    // Thumbs Up Button is currently clicked
                    else {
                        Log.v("ThumbsUp", "SettingGrayUp");
                        mThumbsUpButton.setColorFilter(getResources().getColor(R.color.divider_color));
                        mThumbsUpButton.setSelected(false);
                    }
                }
            });
        }

        private void setUpThumbsDownButton(final ImageView mThumbsUpButton,
                                           final ImageView mThumbsDownButton) {
            mThumbsDownButton.setSelected(false);
            mThumbsDownButton.setColorFilter(getResources().getColor(R.color.divider_color));
            mThumbsDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thumbs Down Button is not clicked
                    if (!mThumbsDownButton.isSelected()) {
                        mThumbsDownButton.setColorFilter(getResources().getColor(R.color.red));
                        mThumbsDownButton.setSelected(true);
                        // Case 2 - Thumbs Up Button is currently clicked
                        if (mThumbsUpButton.isSelected()) {
                            mThumbsUpButton.setColorFilter(getResources().getColor(R.color.divider_color));
                            mThumbsUpButton.setSelected(false);
                        }
                    }
                    // Thumbs Down Button is currently clicked
                    else {
                        mThumbsDownButton.setColorFilter(getResources().getColor(R.color.divider_color));
                        mThumbsDownButton.setSelected(false);
                    }
                }
            });
        }

    }

    private void getUserImage(String imageUrl, final ImageView imageView){

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                } else {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
                }
            }
        });

    }



    private void showRemoveUserDialog(final String userId){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage("Would you like to remove this user from your group?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeUser(userId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void removeUser(String userId){

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", userId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/removeUser?"+paramString;


        System.out.println("removeUser url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("removeUser result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){

                                Toast.makeText(mContext, "User removed", Toast.LENGTH_SHORT);
                                getMemberData();
                            }
                            else{
                                Toast.makeText(mContext, "Error removing user", Toast.LENGTH_SHORT);
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
