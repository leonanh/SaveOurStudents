package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.SnackBar;
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
    protected SnackBar mSnackBar;
    private FabBroadcastReciever fabBroadcastReciever;
    private SlidingTabLayout mSlidingTabLayout;
    private FragmentPagerAdapter mViewGroupPagerAdapter;
    private ViewPager mViewPager;

    private String mQuestionId;
    public JSONObject mQuestionInfo;
    public ArrayList tags;
    private boolean isEditable;
    private boolean mIsActive;
    private boolean mIsInGroup;

    private boolean menuIsBuilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);


        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(this);
        }

        menuIsBuilt = false;
        fabBroadcastReciever = new FabBroadcastReciever();


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

        mSnackBar = (SnackBar)findViewById(R.id.main_sn);
        mSnackBar.text("Connection timed out")
                .applyStyle(R.style.SnackBarSingleLine)
                .actionText("RETRY")
                .duration(0)
                .actionClickListener(new SnackBar.OnActionClickListener() {
                    @Override
                    public void onActionClick(SnackBar snackBar, int i) {
                        getQuestionData();
                    }
                });

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


        getQuestionData();

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter();
        iff.addAction("com.sos.saveourstudents.CUSTOM_INTENT");
        registerReceiver(fabBroadcastReciever, iff);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(fabBroadcastReciever);

    }

    public void getQuestionData() {


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
                            //System.out.println("ViewQuestionActivity result "+result);
                            if(result.getInt("expectResults") == 0) {
                                Toast.makeText(ViewQuestionActivity.this, "This question doesn't exist anymore!", Toast.LENGTH_SHORT)
                                        .show();
                                setResult(RESULT_OK);
                                finish();
                            }
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


                                //boolean isVisible = mQuestionInfo.getBoolean("visible_location");
                                mIsActive = mQuestionInfo.getBoolean("active");



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
                mSnackBar.show();
            }

        });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }





    private void buildFragments(Location location, String userImageUrl, boolean isEditable){


        invalidateOptionsMenu();

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
            if (isEditable && mIsActive) {
                System.out.println("building active menu: "+mIsActive);
                getMenuInflater().inflate(R.menu.menu_edit_public_question, menu);
            }
            else if(isEditable && !mIsActive){
                System.out.println("building inactive menu: "+mIsActive);
                getMenuInflater().inflate(R.menu.menu_edit_private_question, menu);
            }

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //System.out.println("clicked menu item:" + item);

        if (item.getItemId() == R.id.action_close_question) {
            showCloseGroupDialog();
            return true;
        }
        else if (item.getItemId() == R.id.action_private_question) {
            //System.out.println("clicked private");
            toggleGroupActive();
            //toggleQuestionActive(false);
            return true;
        }
        else if (item.getItemId() == R.id.action_public_question) {
            //System.out.println("clicked public");
            toggleGroupActive();
            //toggleQuestionActive(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


        //System.out.println("url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("remove group result: "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){


                                setResult(RESULT_OK);
                                finish();

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
                mSnackBar.show();
            }

        });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void toggleGroupActive() {

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "";
        if(mIsActive)
            url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/closeGroup?" + paramString;
        else
            url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/openGroup?" + paramString;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject result = new JSONObject(response.toString());
                            //System.out.println("activeedit result "+result);
                            if (result.getString("success").equalsIgnoreCase("1")) {
                                mIsActive = !mIsActive;
                                invalidateOptionsMenu();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                mSnackBar.show();
            }

        });

        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }




    class ViewGroupPagerAdapter extends FragmentPagerAdapter {

        private Fragment
                mFragmentViewQuestion,
                mViewGroupLocationFragment,
                mViewGroupMembersFragment;

        public ViewGroupPagerAdapter(FragmentManager fm, Location location, String userImageUrl, boolean isEditable) {
            super(fm);
            mFragmentViewQuestion = ViewQuestionFragment.newInstance(mQuestionId, isEditable);
            mViewGroupLocationFragment = ViewQuestionLocationFragment.newInstance(mQuestionId, location, userImageUrl, isEditable, false);//TODO
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

    public class FabBroadcastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            System.out.println("Recieved custom broadcast in question ACtivity");
            if(((ViewQuestionFragment) mViewGroupPagerAdapter.getItem(0)).mContext != null) {
                ((ViewQuestionFragment) mViewGroupPagerAdapter.getItem(0)).buildFab();
            }
            if(((ViewQuestionMembersFragment) mViewGroupPagerAdapter.getItem(2)).mContext != null) {
                ((ViewQuestionMembersFragment) mViewGroupPagerAdapter.getItem(2)).retrieveListOfRatedTutors();
            }


        }
    }


}
