package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity {
    private ImageView profileImage;
    private TextView profileFirstName;
    private TextView profileLastName;
    private TextView profileRating;
    private TextView profileAboutme;
    private TextView profileSchool;
    private TextView profileMajor;
    private TextView profileDescription;
    private ImageButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileFirstName = (TextView) findViewById(R.id.profile_firstName);
        profileLastName = (TextView) findViewById(R.id.profile_lastName);
        profileRating = (TextView) findViewById(R.id.profile_rating);
        profileAboutme = (TextView) findViewById(R.id.profile_aboutme);
        profileSchool = (TextView) findViewById(R.id.profile_school);
        profileMajor = (TextView) findViewById(R.id.profile_major);
        profileDescription = (TextView) findViewById(R.id.profile_description);

        editButton = (ImageButton) this.findViewById(R.id.edit_profile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
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
