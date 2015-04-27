package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFloat;

public class ProfileActivity extends Activity
        implements ViewProfileFragment.OnEditButtonListener, EditProfileFragment.OnFragmentInteractionListener {

    private Student currStudent;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: Grab currStudent from database
        currStudent = new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null);

        ViewProfileFragment viewProfileFragment = ViewProfileFragment.newInstance(currStudent);
        getFragmentManager().beginTransaction().add(R.id.profile_activity_container, viewProfileFragment)
                .commit();

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

    @Override
    public void onEditButton() {
        EditProfileFragment editProfileFragment = EditProfileFragment.newInstance(currStudent);
        getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.profile_activity_container, editProfileFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
