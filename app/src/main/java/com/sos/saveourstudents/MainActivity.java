package com.sos.saveourstudents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sos.saveourstudents.supportclasses.NavDrawerAdapter;
import com.sos.saveourstudents.supportclasses.RecyclerItemClickListener;
import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean fabShowing = false;

    ViewPager mViewPager;
    SlidingTabLayout mTabs;

    ViewPagerAdapter viewPagerAdapter;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    DrawerLayout mDrawer;                                 // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    com.rey.material.widget.FloatingActionButton fab;

    int ICONS[] = {R.drawable.ic_person_black_24dp,R.drawable.ic_exit_to_app_black_24dp, R.drawable.ic_settings_black_24dp};
    String TITLES[] = {"Profile","Logout","Settings"};


    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(this);
        }

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if(!sharedPref.contains("first_name")){//Your not logged in. Go to login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //Our AppCompat Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        setSupportActionBar(toolbar);

        fab = (com.rey.material.widget.FloatingActionButton) findViewById(R.id.fab_image);

        hideFab();
        buildFab();



        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabs = (SlidingTabLayout) this.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        mTabs.setViewPager(mViewPager);

        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return MainActivity.this.getResources().getColor(R.color.primary_light);
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        //mRecyclerView.setHasFixedSize(true);                            // Letting the system know we wont change the size of the list

        String name = sharedPref.getString("first_name", "") + " "+ sharedPref.getString("last_name", "");
        mAdapter = new NavDrawerAdapter(TITLES, ICONS, name, sharedPref.getString("email", "email"), sharedPref.getString("image", "image"));//PROFILEIMAGE


        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    //Nav drawer listener
                    @Override public void onItemClick(View view, int position) {
                        System.out.println("clicked " + position);

                        if(position == 1){
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("userId", sharedPref.getString("user_id", ""));
                            startActivity(intent);

                        }
                        else if(position == 2){
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if(position == 3){
                            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                        }

                    }
                })
        );

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }



        }; // Drawer Toggle Object Made
        mDrawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mDrawer != null)
            mDrawer.closeDrawers();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {
            TagDialogFragment newFragment = new TagDialogFragment(this, 0);
            newFragment.show(getSupportFragmentManager(), "");

        }

        else if (item.getItemId() == R.id.add_member) {
            Intent mIntent = new Intent(this, MemberJoinActivity.class);
            startActivity(mIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void hideFab(){
        if(fabShowing){
            TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 300);
            anim.setDuration(200);
            anim.setFillAfter(true);
            fab.startAnimation(anim);
            fabShowing = false;
        }
    }

    public void showFab(){
        if(!fabShowing){
            TranslateAnimation anim = new TranslateAnimation( 0, 0 , 300, 0 );
            anim.setDuration(200);
            anim.setFillAfter( true );
            fab.startAnimation(anim);
            fabShowing = true;
        }


    }

    public void updateFragments(){
        updateMapFragment();
        updateFeedFragment();
    }
    private void updateMapFragment(){
        ((FragmentMap) viewPagerAdapter.getItem(1)).getLocationUpdate();
    }
    private void updateFeedFragment(){
        ((FragmentFeed) viewPagerAdapter.getItem(0)).getQuestionData();
    }


    @Override
    public void onClick(View v) {
        //System.out.println("clicked tabs: ");
    }


    private void buildFab(){
        getQuestionActiveStatus();
    }



    private void getQuestionActiveStatus(){

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", sharedPref.getString("user_id", "")));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/hasQuestion?"+paramString;


        System.out.println("url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            final JSONObject result = new JSONObject(response.toString());
                            //System.out.println("has questions result "+result);

                            if(!result.getString("success").equalsIgnoreCase("1")){
                                //Error...

                            }
                            else{
                                final String questionId = result.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("question_id");
                                //System.out.println("QuestionID = " +questionId);
                                //Edit Question
                                if(result.getString("expectResults").equalsIgnoreCase("1")){
                                    fab.setIcon(getResources().getDrawable(R.drawable.ic_create_white_18dp), false);
                                    fab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mIntent = new Intent(MainActivity.this, EditQuestionActivity.class);
                                            mIntent.putExtra("questionId", questionId);
                                            startActivity(mIntent);
                                        }
                                    });
                                }
                                //Add question
                                else{
                                    fab.setIcon(getResources().getDrawable(R.drawable.ic_add_white_24dp), false);
                                    fab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mIntent = new Intent(MainActivity.this, CreateQuestionActivity.class); //TODO rename, dialogize
                                            mIntent.putExtra("questionId", "");
                                            startActivity(mIntent);
                                        }
                                    });
                                }
                                showFab();


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






/**
 * Handles all work that pertains to the 2 main fragments
 */
class ViewPagerAdapter extends FragmentPagerAdapter {

    FragmentFeed feed = new FragmentFeed();
    FragmentMap map = new FragmentMap();
    String[] tabNames = {"Feed", "Map"};


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return feed;
        }
        else{
            return map;
        }


    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

}

