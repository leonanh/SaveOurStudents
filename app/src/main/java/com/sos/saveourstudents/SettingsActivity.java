package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Slider;

/**
 * Created by Xian on 5/19/2015.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Validations validations = new Validations();
    Context appContext;
    Toolbar toolbar;
    TextView distanceConfirmation;
    SharedPreferences sharedPref;
    String email;
    String curPassword;
    String enteredPW;
    String enteredPW2;
    int currdistance;
    int newdistance;
    Slider distanceSlider;
    Button emailButton;
    Button passwordButton;
    EditText newEmail;
    EditText password;
    EditText newPassword;
    EditText newPasswordReEnter;
    boolean validEmail;
    boolean validPassword;
    TextView currDistanceDisplay;

    Toast prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_settings);
        appContext = getApplicationContext();

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        distanceSlider = (Slider) findViewById(R.id.distance_slider);
        currDistanceDisplay = (TextView) findViewById(R.id.current_distance);
        distanceConfirmation = (TextView) findViewById(R.id.distance_confirmation);
        emailButton = (Button) findViewById(R.id.change_email);
        passwordButton = (Button) findViewById(R.id.change_password);

        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);

            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        currdistance = sharedPref.getInt("distance", 1);

        currDistanceDisplay.setText(currdistance+"");
        distanceSlider.setPosition(currdistance, true);
        distanceSlider.setValue(currdistance, true);
        distanceSlider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, float oldPos, float newPos, int oldValue, int newValue) {
                currDistanceDisplay.setText(newValue+"");
            }

        });



        emailButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       /* if(v == distanceSlider)
        {
            newdistance = (int)distanceSlider.getPosition();
            currDistanceDisplay.setText(newdistance);
            if(newdistance != currdistance)
            {
                distanceConfirmation.setText("Distance Changed");
                distanceConfirmation.setTextColor(R.color.green);
            }

        }*/
        if(v == emailButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Email");
            newEmail = new EditText(this);
            newEmail.setHint("New Email");
            builder.setView(newEmail);
            builder.setPositiveButton("Change Email", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    email = newEmail.getText().toString();
                    validEmail = verifyEmail(email);
                    if(validEmail) {
                        // change with server
                        prompt = Toast.makeText(appContext, "Email Changed", Toast.LENGTH_SHORT);
                        prompt.show();
                    }
                    else
                    {
                        //toast invalid email
                        prompt = Toast.makeText(appContext, "Invalid Email", Toast.LENGTH_SHORT);
                        prompt.show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go back
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if(v == passwordButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Password");
            password = new EditText(this);
            password.setHint("Current Password");
            builder.setView(password);
            newPassword = new EditText(this);
            newPassword.setHint("New Password");
            builder.setView(newPassword);
            newPasswordReEnter= new EditText(this);
            newPasswordReEnter.setHint("Re-enter New Password");
            builder.setView(newPasswordReEnter);
            builder.setPositiveButton("Change Email", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    curPassword = password.getText().toString();
                    enteredPW = newPassword.getText().toString();
                    enteredPW2 = newPasswordReEnter.getText().toString();
                    validPassword = true;
                    prompt = Toast.makeText(appContext, "Password Changed", Toast.LENGTH_SHORT);
                    prompt.show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go back
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean verifyEmail(String incomingEmail) {
        return validations.testEmailSignUp(incomingEmail);
    }
}
