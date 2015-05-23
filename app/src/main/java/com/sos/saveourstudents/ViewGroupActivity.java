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

import com.google.android.gms.maps.model.LatLng;
import com.sos.saveourstudents.supportclasses.SlidingTabLayout;

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

    private boolean mTutorsAllowed;
    private boolean mStudentsAllowed;

    private ArrayList<Student> mStudents;
    private ArrayList<Student> mTutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        mStudents = new ArrayList<>();
        mTutors = new ArrayList<>();

        // TODO: Populate mStudents and mTutors with students from database
        mStudents.add(new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null));
        mTutors.add(new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null));

        // Assigning private variables to correct ViewGroups
        // TODO: Move initializations to separate method when all views are situated
        mFragmentViewQuestion = new FragmentViewQuestion();

        // TODO: Grab actual location from database
        mViewGroupLocationFragment = ViewGroupLocationFragment.newInstance(
                new LatLng(32.881151, -117.23744999999997));

        mViewGroupMembersFragment = ViewGroupMembersFragment.newInstance(
                mStudents, mTutors);


        mViewGroupPagerAdapter = new ViewGroupPagerAdapter(getSupportFragmentManager());
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.view_group_tabPage);
        mViewPager = (ViewPager) findViewById(R.id.view_group_viewPager);

        // TODO: Grab boolean values from database for group settings
        mTutorsAllowed = true;
        mStudentsAllowed = true;

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
                return mFragmentViewQuestion; // TODO: Change to our own implementation
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
