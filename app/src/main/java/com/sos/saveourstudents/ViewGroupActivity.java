package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
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
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ViewGroupActivity extends AppCompatActivity
        implements ViewGroupMembersFragment.OnTutorRatingListener,
        ViewGroupMembersFragment.OnMemberRemoveListener {

    private Menu mMenu;

    private static final int numPages = 3;
    private static ViewGroupQuestionFragment mFragmentViewQuestion;
    private static ViewGroupLocationFragment mViewGroupLocationFragment;
    private static ViewGroupMembersFragment mViewGroupMembersFragment;
    private static SlidingTabLayout mSlidingTabLayout;
    private static FragmentPagerAdapter mViewGroupPagerAdapter;
    private static ViewPager mViewPager;

    private Question mCurrQuestion;

    private List<Student> mStudents;
    private List<Student> mTutors;

    private static final String mUserIdIntentsTag = "userId";

    private String mViewerUserId;
    private boolean mCurrViewerIsInGroup;

    private static final String mQuestionIdIntentsTag = "questionId";
    private static final String mQuestionUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/" +
                    "viewQuestion?questionId=";
    private String mQuestionId;

    private static final String mUserURL =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getUserById?userId=";

    // Start of Deagan's adjustments
    public JSONObject mQuestionInfo;
    public ArrayList tags;
    private boolean isEditable;

    // End of Deagan's adjustments

    private String mUserId;
    private static final String mMembersUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewMembers?questionId=";

    private static final String mRatingUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/rateTutor?userId=";
    private static final String mRatingParameter =
            "&like=";

    private static final String mRemoveMemberUrl =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/removeUser?userId=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("questionId")){
                mQuestionId = getIntent().getExtras().getString("questionId");
            }
            else{
                System.out.println("Could not find QuestionId, finishing editQuestion");
                finish();
            }
        }else{
            System.out.println("Could not find QuestionId, finishing editQuestion");
            finish();
        }

        mCurrViewerIsInGroup = false; // Will be checked in JSON parsing
        mViewerUserId = getIntent().getStringExtra(mUserIdIntentsTag);

        mQuestionId = getIntent().getStringExtra(mQuestionIdIntentsTag);
        mStudents = new ArrayList<>();
        mTutors = new ArrayList<>();

        String url = mQuestionUrl + mQuestionId;

        JsonObjectRequest questionRequest = new JsonObjectRequest(Request.Method.GET,
                url, (JSONObject) null, new QuestionResponseListener(), new QuestionErrorListener());

        Singleton.getInstance().addToRequestQueue(questionRequest);

        // Setting up toolbar - independent of JSON requests
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_group_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        getQuestionData();


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




                                String userImageUrl = "";
                                if(mQuestionInfo.has("image"))
                                    userImageUrl = mQuestionInfo.getString("image");


                                String latitude = mQuestionInfo.getString("latitude");
                                String longitude = mQuestionInfo.getString("longitude");

                                Location location = new Location("new");
                                location.setLongitude(Double.parseDouble(longitude));
                                location.setLatitude(Double.parseDouble(latitude));



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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mMenu == null) {
            mMenu = menu;
        }

        if(isEditable) {
            getMenuInflater().inflate(R.menu.menu_edit_question, menu);
            return true;
        }
        else return false;
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
        if (id == R.id.action_close_question) {
            showCloseGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the Student's rating in the database
     * @param rating If true, increment, else decrement
     */
    @Override
    public void onTutorRatingInteraction(boolean rating, String currUserId) {
        String rateTutorRequest = mRatingUrl + currUserId;

        if (rating) {
            rateTutorRequest = rateTutorRequest + mRatingParameter + "true";
        } else {
            rateTutorRequest = rateTutorRequest + mRatingParameter + "false";
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                rateTutorRequest, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Singleton.getInstance().addToRequestQueue(request);
    }

    /**
     * Method to be called when listener reports a request to remove a member from the dialog
     * @param memberUserId The user ID of the member to be removed
     * @param tutor A boolean representing whether the member is a tutor or not
     */
    @Override
    public void onMemberRemoveInteraction(String memberUserId, boolean tutor) {
        if(!tutor) {
           for(int i = 0; i < mStudents.size(); i++) {
               if(mStudents.get(i).getUserId().equals(memberUserId)) {
                   mStudents.remove(i);
                   String removeMemberUrl = mRemoveMemberUrl + memberUserId;
                   JsonObjectRequest removeMemberRequest = new JsonObjectRequest(Request.Method.GET,
                           removeMemberUrl, (JSONObject) null, new Response.Listener<JSONObject>() {
                       @Override
                       public void onResponse(JSONObject response) {
                           mViewGroupMembersFragment.notifyStudentsDataSetChanged();
                       }
                   }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {

                       }
                   });
                   Singleton.getInstance().addToRequestQueue(removeMemberRequest);
                   return;
               }
           }
        } else {
            for(int i = 0; i < mTutors.size(); i++) {
                if(mTutors.get(i).getUserId().equals(memberUserId)) {
                    mTutors.remove(i);
                    String removeMemberUrl = mRemoveMemberUrl + memberUserId;
                    JsonObjectRequest removeTutorRequest = new JsonObjectRequest(Request.Method.GET,
                            removeMemberUrl, (JSONObject) null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mViewGroupMembersFragment.notifyTutorsDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    Singleton.getInstance().addToRequestQueue(removeTutorRequest);
                    return;
                }
            }
        }
    }

    /**
     * Pager adapter for setting up the Sliding Tabs feature
     * Dictates titles and positions
     */
    class ViewGroupPagerAdapter extends FragmentPagerAdapter {

        public ViewGroupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.view_group_question);
            } else if (position == 1) {
                return getResources().getString(R.string.view_group_location);
            } else if (position == 2) {
                return getResources().getString(R.string.view_group_members);
            } else {
                Log.e("ViewGroup", "ERROR: Nonexistent Position!");
                return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mFragmentViewQuestion;
            } else if (position == 1) {
                return mViewGroupLocationFragment;
            } else if (position == 2) {
                return mViewGroupMembersFragment;
            } else {
                Log.e("ViewGroup", "ERROR: Nonexistent Position!");
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }

    /**
     * Initializes all views, to be used after Volley calls have finished
     */
    private void initializeAllViews() {
        // Assigning private variables to correct ViewGroups
        mFragmentViewQuestion = ViewGroupQuestionFragment.newInstance(mQuestionId, isEditable);

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
     * Checks to see if the current viewer of the activity is in the group
     * @return True if the viewer is in the group, false otherwise
     */
    public boolean isCurrViewerIsInGroup() {
        return mCurrViewerIsInGroup;
    }

    private void showCloseGroupDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this question?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteQuestion();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dont do shit...
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



    private void deleteQuestion() {

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/removeGroup?"+paramString;


        System.out.println("url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("remove group result: "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){

                                ViewGroupActivity.this.finishActivity(ViewGroupActivity.this.RESULT_OK);

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

    /**
     * Gets the current viewer's user ID
     * @return Current viewer's user ID
     */
    public String getmViewerUserId() {
        return mViewerUserId;
    }

    /**
     * Gets the current owner of the group's user ID
     * @return Group owner's user ID
     */
    public String getmUserId() {
        return mUserId;
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
                isEditable = mUserId.equals(mViewerUserId);
                String getStudentUrl = mUserURL + mUserId;

                onCreateOptionsMenu(mMenu);
                mCurrQuestion = new Question(theResponse.getBoolean("study_group"),
                        theResponse.getBoolean("tutor"), new LatLng(
                        theResponse.getDouble("latitude"), theResponse.getDouble("longitude")),
                        theResponse.getBoolean("active"), null, theResponse.getString("text"));
                mCurrQuestion.setmQuestionId(mQuestionId);

                JSONArray questionAndTags = new JSONObject(response.toString())
                        .getJSONObject("result").getJSONArray("myArrayList");

                mQuestionInfo = questionAndTags.getJSONObject(0).getJSONObject("map");

                tags = new ArrayList<>();
                if(questionAndTags.length() > 1){
                    for(int a = 1; a < questionAndTags.length(); a++){
                        tags.add(questionAndTags.getJSONObject(a).getJSONObject("map").getString("tag"));
                    }
                }

                JsonObjectRequest studentRequest = new JsonObjectRequest(Request.Method.GET,
                        getStudentUrl, (JSONObject) null,
                        new ProfileResponseListener(), new ProfileErrorListener());
                Singleton.getInstance().addToRequestQueue(studentRequest);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Error listener for the Question query
     */
    class QuestionErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

    /**
     * Retrieves the current owner of the question to populate FragmentViewQuestion
     */
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

    /**
     * Error listener for the owner profile JSON request
     */
    class ProfileErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

    /**
     * Response listener for populating the mStudents and mTutors ArrayLists
     * Views are initialized once this is complete
     */
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

                    if (currStudent.getUserId().equals(mViewerUserId)) {
                        mCurrViewerIsInGroup = true;
                    }

                }

                initializeAllViews();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Error listener for the members lists' JSON Request
     */
    class MembersTableErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }
}
