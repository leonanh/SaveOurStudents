package com.sos.saveourstudents;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sos.saveourstudents.supportclasses.NavDrawerAdapter;
import com.sos.saveourstudents.supportclasses.RecyclerItemClickListener;
import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

public class MainActivity extends ActionBarActivity {


    ViewPager mViewPager;
    SlidingTabLayout mTabs;


    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout mDrawer;                                 // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    //ButtonFloat fab;

    int ICONS[] = {R.drawable.ic_settings_black_24dp,R.drawable.ic_exit_to_app_black_24dp, R.drawable.ic_help_black_24dp};
    String TITLES[] = {"Profile","Logout","Help"};
    int PROFILEIMAGE = R.drawable.defaultprofile;


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

        if(!sharedPref.contains("first_name")){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //Our AppCompat Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        setSupportActionBar(toolbar);


        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();


        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(this.getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) this.findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);

        mTabs.setViewPager(mViewPager);

        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                //eturn Color.WHITE;
                return MainActivity.this.getResources().getColor(R.color.primary_dark);
            }
        });



        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know we wont change the size of the list
        mAdapter = new NavDrawerAdapter(TITLES, ICONS, "Name","Email", PROFILEIMAGE);


        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    //Nav drawer listener
                    @Override public void onItemClick(View view, int position) {
                        System.out.println("clicked " + position);

                        if(position == 1){
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        }
                        else if(position == 2){
                            Intent mainActivity = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(mainActivity);
                            finish();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ///return true;
        }
        else if (id == R.id.action_filter) {

            DisplayMetrics metrics = new DisplayMetrics();
            /*
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
*/

            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            DialogFragment newFragment = new TagDialogFragment(this, metrics, 0);

            newFragment.setCancelable(true);

            newFragment.show(getSupportFragmentManager(), "");


        }


        if (id == R.id.view_question) {

            Intent mIntent = new Intent(this, QuestionActivity.class);
            mIntent.putExtra("type", 0);
            startActivity(mIntent);
            return true;
        }

        if (id == R.id.edit_question) {

            Intent mIntent = new Intent(this, QuestionActivity.class);
            mIntent.putExtra("type", 1);
            startActivity(mIntent);

            return true;
        }

        if (id == R.id.add_member) {

            Intent mIntent = new Intent(this, MemberJoinActivity.class);
            startActivity(mIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}







/**
 * Handles all work that pertains to the 2 main fragments
 */
class MyPagerAdapter extends FragmentPagerAdapter {

    FragmentFeed feed = new FragmentFeed();
    FragmentMap map = new FragmentMap();
    String[] tabNames = {"Feed", "Map"};

    public MyPagerAdapter(FragmentManager fm) {
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
        else
            return map;
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }
}
