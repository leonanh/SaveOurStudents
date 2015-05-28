package com.sos.saveourstudents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewGroupActivity extends AppCompatActivity
        implements ViewGroupMembersFragment.OnTutorRatingListener{

    private static final int numPages = 3;
    private static FragmentViewQuestion mFragmentViewQuestion;
    private static ViewGroupLocationFragment mViewGroupLocationFragment;
    private static ViewGroupMembersFragment mViewGroupMembersFragment;
    private static SlidingTabLayout mSlidingTabLayout;
    private static FragmentPagerAdapter mViewGroupPagerAdapter;
    private static ViewPager mViewPager;

    private Question mCurrQuestion;

    private ArrayList<Student> mStudents;
    private ArrayList<Student> mTutors;

    public boolean isCurrViewerIsInGroup() {
        return mCurrViewerIsInGroup;
    }

    private boolean mCurrViewerIsInGroup;

    private static final String mQuestionIdIntentsTag = "questionId";
    private static final String mQuestionUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/" +
                    "viewQuestion?questionId=";
    private String mQuestionId;

    private static final String mUserURL =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getUserById?userId=";
    private String mUserId;

    private static final String mMembersUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewMembers?questionId=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        mCurrViewerIsInGroup = false; // Will be checked in JSON parsing

        mQuestionId = getIntent().getStringExtra(mQuestionIdIntentsTag);
        mStudents = new ArrayList<>();
        mTutors = new ArrayList<>();

        String url = mQuestionUrl + mQuestionId;

        JsonObjectRequest questionRequest = new JsonObjectRequest(Request.Method.GET,
                url, (JSONObject) null, new QuestionResponseListener(), new QuestionErrorListener());

        Singleton.getInstance().addToRequestQueue(questionRequest);

        // Setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_group_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_group, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTutorRatingInteraction(boolean rating) {
    }

    class ViewGroupPagerAdapter extends FragmentPagerAdapter {

        public ViewGroupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0) {
                return getResources().getString(R.string.view_group_question);
            }
            else if(position == 1) {
                return getResources().getString(R.string.view_group_location);
            }
            else if(position == 2) {
                return getResources().getString(R.string.view_group_members);
            }
            else {
                Log.e("ViewGroup", "ERROR: Nonexistent Position!");
                return null;
            }
        }
        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return mFragmentViewQuestion;
            }
            else if(position == 1) {
                return mViewGroupLocationFragment;
            }
            else if(position == 2) {
                return mViewGroupMembersFragment;
            }
            else {
                Log.e("ViewGroup", "ERROR: Nonexistent Position!");
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }

    private void initializeAllViews() {
        // Assigning private variables to correct ViewGroups
        mFragmentViewQuestion = new FragmentViewQuestion(mCurrQuestion);

        mViewGroupLocationFragment = ViewGroupLocationFragment.newInstance(mCurrQuestion
                .getmLocation());

        mViewGroupMembersFragment = ViewGroupMembersFragment.newInstance(
                mStudents, mTutors);


        mViewGroupPagerAdapter = new ViewGroupPagerAdapter(getSupportFragmentManager());
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.view_group_tabPage);
        mViewPager = (ViewPager) findViewById(R.id.view_group_viewPager);

        // Setting up sliding tabs feature
        mViewPager.setAdapter(mViewGroupPagerAdapter);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primary_light);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);
    }

    /**
     * Begins parsing through the Question and creating the Group Leader Student object
     */
    class QuestionResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONObject theResponse = new JSONObject(response.toString())
                        .getJSONObject("result")
                        .getJSONArray("myArrayList")
                        .getJSONObject(0)
                        .getJSONObject("map");

                mUserId = theResponse.getString("user_id");
                String getStudentUrl = mUserURL + mUserId;

                mCurrQuestion = new Question(theResponse.getBoolean("study_group"),
                        theResponse.getBoolean("tutor"), new LatLng(
                        theResponse.getDouble("latitude"), theResponse.getDouble("longitude")),
                        theResponse.getBoolean("active"), null, theResponse.getString("text"));
                mCurrQuestion.setmQuestionId(mQuestionId);

                JsonObjectRequest studentRequest = new JsonObjectRequest(Request.Method.GET,
                        getStudentUrl, (JSONObject) null,
                        new ProfileResponseListener(), new ProfileErrorListener());
                Singleton.getInstance().addToRequestQueue(studentRequest);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class QuestionErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

    class ProfileResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONObject theResponse = new JSONObject(response.toString())
                        .getJSONObject("result")
                        .getJSONArray("myArrayList")
                        .getJSONObject(0)
                        .getJSONObject("map");
                mCurrQuestion.setmGroupOwner(new Student(theResponse.getString("first_name"),
                        theResponse.getString("last_name"), theResponse.getInt("rating"),
                        theResponse.getString("school"), theResponse.getString("major"),
                        theResponse.getString("description"), null));

                String getMembersTableUrl = mMembersUrl + mQuestionId;

                JsonObjectRequest membersRequest = new JsonObjectRequest(Request.Method.GET,
                        getMembersTableUrl, (JSONObject) null,
                        new MembersTableResponseListener(), new MembersTableErrorListener());
                Singleton.getInstance().addToRequestQueue(membersRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class ProfileErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

    class MembersTableResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONArray membersArray = new JSONObject(response.toString())
                        .getJSONObject("result").getJSONArray("myArrayList");

                for (int i = 0; i < membersArray.length(); i++) {
                    JSONObject currMember = membersArray.getJSONObject(i).getJSONObject("map");
                    Student currStudent = new Student(currMember.getString("first_name"),
                            currMember.getString("last_name"), currMember.getString("user_id"));

                    if (currMember.getBoolean("tutor")) {
                        mTutors.add(currStudent);
                    } else mStudents.add(currStudent);

                    if (currStudent.getUserId() == mUserId) {
                        mCurrViewerIsInGroup = true;
                    }

                }

                initializeAllViews();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    class MembersTableErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }
}
