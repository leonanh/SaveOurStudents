package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class ViewQuestionMembersFragment extends Fragment {


    ArrayList<JSONObject> mTutorList = null;
    ArrayList<JSONObject> mStudentList = null;

    private ListView mStudentsListView;
    private ListView mTutorsListView;
    private StudentsArrayAdapter mStudentsListViewArrayAdapter;
    private TutorsArrayAdapter mTutorsListViewArrayAdapter;


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

        mStudentsListView = (ListView) rootView.findViewById(R.id.view_group_members_students_listView);
        mTutorsListView = (ListView) rootView.findViewById(R.id.view_group_members_tutors_listView);


        getMemberData();

        return rootView;
    }

    private void getMemberData() {

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String currentUserId = sharedPref.getString("user_id", "");


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
        HashMap<JSONObject, Integer> mIdMap;

        public StudentsArrayAdapter(Context context, List<JSONObject> objects) {
            super(context, 0, objects);
            mIdMap = new HashMap<>();

            for (int i = 0; i < objects.size(); i++) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject currStudentMember = getItem(position);
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

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    /**
     * Array adapter to be used with the Tutors ListView
     * Incorporates Thumbs Up/Down functionality
     */
    private class TutorsArrayAdapter extends ArrayAdapter<JSONObject> {
        private HashMap<JSONObject, Integer> mIdMap;
        private final int mThumbsUpButtonGreyId = R.drawable.ic_thumb_up_grey_500_18dp;
        private final int mThumbsUpButtonBlueId = R.drawable.ic_thumb_up_light_blue_500_18dp;
        private final int mThumbsDownButtonGreyId = R.drawable.ic_thumb_down_grey_500_18dp;
        private final int mThumbsDownButtonRedId = R.drawable.ic_thumb_down_red_400_18dp;

        public TutorsArrayAdapter(Context context, List<JSONObject> objects) {
            super(context, 0, objects);
            mIdMap = new HashMap<>();

            for (int i = 0; i < objects.size(); i++) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            return mIdMap.get(getItem(position));
        }

        /**
         * Sets up the view for the ListView
         * @param position The position of the current item in the ListView
         * @param convertView The view of the current item in the ListView
         * @param parent The ListView
         * @return convertView after setup
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject currStudentMember = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.view_group_members_tutors_layout, parent, false);
            }

            try {
                ((TextView) convertView.findViewById(R.id.tutor_firstName))
                        .setText(currStudentMember.getString("first_name"));

                ((TextView) convertView.findViewById(R.id.tutor_lastName))
                        .setText(currStudentMember.getString("last_name"));


                if(currStudentMember.has("image"))
                    getUserImage(currStudentMember.getString("image"), ((ImageView) convertView.findViewById(R.id.tutor_profile_image)));

            } catch (JSONException e) {
                e.printStackTrace();
            }


            ImageButton mThumbsUpButton =
                    ((ImageButton) convertView.findViewById(R.id.view_group_members_thumbs_up));
            ImageButton mThumbsDownButton =
                    ((ImageButton) convertView.findViewById(R.id.view_group_members_thumbs_down));
            setUpThumbsUpButton(mThumbsUpButton, mThumbsDownButton);
            setUpThumbsDownButton(mThumbsUpButton, mThumbsDownButton);

            return convertView;
        }

        // TODO: Set up Thumbs Up/Down functionality for database rating updates
        private void setUpThumbsUpButton(final ImageButton mThumbsUpButton,
                                         final ImageButton mThumbsDownButton) {
            mThumbsUpButton.setTag(false);
            mThumbsUpButton.setImageResource(mThumbsUpButtonGreyId);
            mThumbsUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("ThumbsUp", "onClick");
                    // Thumbs Up Button is not clicked
                    if ((Boolean) mThumbsUpButton.getTag() == false) {
                        Log.v("ThumbsUp", "SettingLightBlue");
                        mThumbsUpButton
                                .setImageResource(mThumbsUpButtonBlueId);
                        mThumbsUpButton.setTag(true);
                        // Case 2 - Thumbs down button is currently clicked
                        if ((Boolean) mThumbsDownButton.getTag() == true) {
                            mThumbsDownButton
                                    .setImageResource(mThumbsDownButtonGreyId);
                            mThumbsDownButton.setTag(false);
                        }
                    }
                    // Thumbs Up Button is currently clicked
                    else {
                        Log.v("ThumbsUp", "SettingGrayUp");
                        mThumbsUpButton
                                .setImageResource(mThumbsUpButtonGreyId);
                        mThumbsUpButton.setTag(false);
                    }
                }
            });
        }

        private void setUpThumbsDownButton(final ImageButton mThumbsUpButton,
                                           final ImageButton mThumbsDownButton) {
            mThumbsDownButton.setTag(false);
            mThumbsDownButton.setImageResource(mThumbsDownButtonGreyId);
            mThumbsDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thumbs Down Button is not clicked
                    if ((Boolean) mThumbsDownButton.getTag() == false) {
                        mThumbsDownButton
                                .setImageResource(mThumbsDownButtonRedId);
                        mThumbsDownButton.setTag(true);
                        // Case 2 - Thumbs Up Button is currently clicked
                        if ((Boolean) mThumbsUpButton.getTag() == true) {
                            mThumbsUpButton
                                    .setImageResource(mThumbsUpButtonGreyId);
                            mThumbsUpButton.setTag(false);
                        }
                    }
                    // Thumbs Down Button is currently clicked
                    else {
                        mThumbsDownButton
                                .setImageResource(mThumbsDownButtonGreyId);
                        mThumbsDownButton.setTag(false);
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


}
