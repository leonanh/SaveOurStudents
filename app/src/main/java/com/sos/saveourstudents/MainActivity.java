package com.sos.saveourstudents;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

public class MainActivity extends ActionBarActivity {


    ViewPager mViewPager;
    SlidingTabLayout mTabs;


    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;


    int ICONS[] = {R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    String TITLES[] = {"Home","Events","Mail","Shop","Travel"};
    int PROFILEIMAGE = R.drawable.ic_launcher;

    static boolean LOGGED_IN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!LOGGED_IN){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);

        //Our AppCompat Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.the_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SOS");



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
                return Color.WHITE;
            }
        });



        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,"Name","Email",PROFILEIMAGE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);


        Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

/*

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity
                mDrawerLayout,         // DrawerLayout object
                R.drawable.ic_navigation_drawer,  // nav drawer icon to replace 'Up' caret
                R.string.drawer_open,  // "open drawer" description
                R.string.drawer_close  // "close drawer" description
        ) {

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
*/






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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.view_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        if (id == R.id.view_question) {
            startActivity(new Intent(this, QuestionActivity.class));
            return true;
        }

        //if (mDrawerToggle.onOptionsItemSelected(item)) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}


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
        return 2;
    }
}