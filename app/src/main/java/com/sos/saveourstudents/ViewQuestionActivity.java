package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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


public class ViewQuestionActivity extends AppCompatActivity {

    private final int numPages = 3;

    private Menu menu;
    private SlidingTabLayout mSlidingTabLayout;
    private FragmentPagerAdapter mViewGroupPagerAdapter;
    private ViewPager mViewPager;

    private String mQuestionId;
    public JSONObject mQuestionInfo;
    public ArrayList tags;
    private boolean isEditable;

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


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.view_group_tabPage);
        mViewPager = (ViewPager) findViewById(R.id.view_group_viewPager);

        // Setting up toolbar
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


        //Need to pass location/userImage to map frag, could be moved to service call?
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

                                JSONArray questionAndTags = result.getJSONObject("result").getJSONArray("myArrayList");

                                mQuestionInfo = questionAndTags.getJSONObject(0).getJSONObject("map");

                                tags = new ArrayList<>();
                                if(questionAndTags.length() > 1){
                                    for(int a = 1; a < questionAndTags.length(); a++){
                                        tags.add(questionAndTags.getJSONObject(a).getJSONObject("map").getString("tag"));
                                    }
                                }


                                String userImageUrl = "";
                                if(mQuestionInfo.has("image"))
                                    userImageUrl = mQuestionInfo.getString("image");


                                String latitude = mQuestionInfo.getString("latitude");
                                String longitude = mQuestionInfo.getString("longitude");

                                Location location = new Location("new");
                                location.setLongitude(Double.parseDouble(longitude));
                                location.setLatitude(Double.parseDouble(latitude));

                                SharedPreferences sharedPref = getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                String currentUserId = sharedPref.getString("user_id", "");
                                isEditable = mQuestionInfo.getString("user_id").equalsIgnoreCase(currentUserId);

                                buildFragments(location, userImageUrl, isEditable);

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





    private void buildFragments(Location location, String userImageUrl, boolean isEditable){



        onCreateOptionsMenu(menu);


        mViewGroupPagerAdapter = new ViewGroupPagerAdapter(getSupportFragmentManager(), location, userImageUrl, isEditable);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(menu != null) {
            this.menu = menu;
            if (isEditable)
                getMenuInflater().inflate(R.menu.menu_edit_question, menu);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.action_close_question) {
            showCloseGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                                ViewQuestionActivity.this.finishActivity(ViewQuestionActivity.this.RESULT_OK);

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



    class ViewGroupPagerAdapter extends FragmentPagerAdapter {


        private Fragment mFragmentViewQuestion;

        private Fragment
                mViewGroupLocationFragment,
                mViewGroupMembersFragment;

        private boolean isEditable;

        public ViewGroupPagerAdapter(FragmentManager fm, Location location, String userImageUrl, boolean isEditable) {
            super(fm);
            this.isEditable = isEditable;
            mFragmentViewQuestion = ViewQuestionFragment.newInstance(mQuestionId, isEditable);
            mViewGroupLocationFragment = ViewQuestionLocationFragment.newInstance(mQuestionId, location, userImageUrl, isEditable);
            //mViewGroupMembersFragment = EditQuestionMembersFragment.newInstance(mQuestionId, isEditable);
            mViewGroupMembersFragment = ViewQuestionMembersFragment.newInstance(mQuestionId, isEditable);

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




}
