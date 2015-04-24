package com.sos.saveourstudents.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.sos.saveourstudents.R;

public class ProfileActivity extends Activity {
    private ImageView profileImage;
    private TextView profileFullname;
    private TextView profileRating;
    private TextView profileAboutme;
    private TextView profileSchool;
    private TextView profileMajor;
    private TextView profileDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileFullname = (TextView) findViewById(R.id.profile_fullname);
        profileRating = (TextView) findViewById(R.id.profile_rating);
        profileAboutme = (TextView) findViewById(R.id.profile_aboutme);
        profileSchool = (TextView) findViewById(R.id.profile_school);
        profileMajor = (TextView) findViewById(R.id.profile_major);
        profileDescription = (TextView) findViewById(R.id.profile_description);

        profileFullname.setText("TODO: GRAB NAME FROM DATABASE");
        profileRating.setText(R.string.profile_rating + "TODO: GRAB RATING FROM DATABASE");
        profileAboutme.setText(R.string.profile_aboutme);
        profileSchool.setText(R.string.profile_school + "TODO: GRAB SCHOOL FROM DATABASE");
        profileMajor.setText(R.string.profile_major + "TODO: GRAB MAJOR FROM DATABASE");
        profileDescription.setText(R.string.profile_description + "TODO: GRAB DESCRIPTION FROM DATABASE");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
