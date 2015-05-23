package com.sos.saveourstudents;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Xian on 5/19/2015.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button doneButton;
    boolean emailChanged = false;
    boolean passwordChanged = false;
    boolean distanceChanged = false;
    Toast confirmation;
    Context appContext;
    Toolbar toolbar;
    EditText emailField;
    EditText passwordField;
    TextView emailConfirmation;
    TextView passwordConfirmation;
    TextView distanceConfirmation;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_settings);
        appContext = getApplicationContext();

        emailField = (EditText) findViewById(R.id.change_email);
        passwordField = (EditText) findViewById(R.id.change_password);
        emailConfirmation = (TextView) findViewById(R.id.email_confirmation);
        passwordConfirmation = (TextView) findViewById(R.id.password_confirmation);
        distanceConfirmation = (TextView) findViewById(R.id.distance_confirmation);

        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /*TODO*/
        /* Set confirmation text to green and set text to X changed if valid change
         * Otherwise set confirmation to red and set text to Invalid X
         * Remove hardcoded text in xml (used to see location of textview)
         *
         * Get input checks to see if email/pw/distance was changed
         */
        doneButton = (Button) findViewById(R.id.settings_save_button);
        doneButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v == doneButton) {
            if (emailChanged) {
                //Update email database
                confirmation = Toast.makeText(appContext, "Setting changed", Toast.LENGTH_SHORT);
                confirmation.show();
            }
            if (passwordChanged) {
                //Update password database
                confirmation = Toast.makeText(appContext, "Setting changed", Toast.LENGTH_SHORT);
                confirmation.show();
            }
            if (distanceChanged) {
                //Update distance database
                confirmation = Toast.makeText(appContext, "Setting changed", Toast.LENGTH_SHORT);
                confirmation.show();
            }
            finish();
        }
    }
}
