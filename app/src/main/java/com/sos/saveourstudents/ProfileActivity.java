package com.sos.saveourstudents;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends AppCompatActivity
        implements ViewProfileFragment.OnEditButtonListener,
        EditProfileFragment.OnDoneButtonListener {

    private Student currStudent;
    private ViewProfileFragment viewProfileFragment;
    private EditProfileFragment editProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: Grab currStudent from database
        currStudent = new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
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

        viewProfileFragment = ViewProfileFragment.newInstance(currStudent);
        getFragmentManager().beginTransaction()
                .add(R.id.profile_activity_container, viewProfileFragment)
                .addToBackStack(null)
                .commit();

    }

    /**
     * Overrides onBackPressed()
     * EditProfileFragment should go back to ViewProfileFragment
     */
    @Override
    public void onBackPressed() {
        if (viewProfileFragment != null && editProfileFragment != null) {
            if (findViewById(R.id.profile_editProfile_overall).hasFocus()) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(editProfileFragment)
                        .attach(viewProfileFragment)
                        .commit();
                editProfileFragment = null;
            } else {
                findViewById(R.id.profile_editProfile_overall).requestFocus();
            }
        } else super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        editProfileFragment = EditProfileFragment.newInstance(currStudent);
        getFragmentManager().beginTransaction()
                .detach(viewProfileFragment)
                .add(R.id.profile_activity_container, editProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDoneButton() {
        if (editProfileFragment != null && viewProfileFragment != null) {
            editProfileFragment.updateStudent();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(editProfileFragment)
                    .attach(viewProfileFragment)
                    .commit();
            editProfileFragment = null;
        } else {
            Log.e("ProfileActivity", "NullPointerException for a fragment");
        }
    }
}
