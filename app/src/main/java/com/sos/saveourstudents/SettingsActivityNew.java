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

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Slider;
import com.sos.saveourstudents.supportclasses.Validations;

/**
 * Created by Xian on 5/19/2015.
 */
public class SettingsActivityNew extends AppCompatActivity implements View.OnClickListener {

    Context appContext;
    Toolbar toolbar;
    SharedPreferences sharedPref;
    Validations validations = new Validations();
    String email;
    String curPassword;
    String enteredPW;
    String enteredPW2;
    int currdistance;


    Slider distanceSlider;
    Button emailButton;
    Button passwordButton;
    EditText newEmail;
    EditText password;
    EditText newPassword;
    EditText newPasswordReEnter;

    boolean validEmail;
    boolean validPassword;
    boolean correctPassword;
    TextView currDistanceDisplay;
    Toast prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        appContext = getApplicationContext();


        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        distanceSlider = (Slider) findViewById(R.id.distance_slider);
        currDistanceDisplay = (TextView) findViewById(R.id.current_distance);
        emailButton = (Button) findViewById(R.id.change_email);
        passwordButton = (Button) findViewById(R.id.change_password);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        currdistance = sharedPref.getInt("distance", 1);

        currDistanceDisplay.setText(currdistance + "");
        distanceSlider.setPosition(currdistance, true);
        distanceSlider.setValue(currdistance, true);
        distanceSlider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider slider, float oldPos, float newPos, int oldValue, int newValue) {
                currDistanceDisplay.setText(newValue + "");
                //TODO Change with server
            }

        });
        emailButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == emailButton) {
            SimpleDialog.Builder builder = new SimpleDialog.Builder() {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    newEmail = (EditText) findViewById(R.id.new_email);
                    email = newEmail.getText().toString();
                    validEmail = verifyEmail(email);
                    if (validEmail) {
                        // change with server
                        prompt = Toast.makeText(appContext, "Email Changed", Toast.LENGTH_SHORT);
                        prompt.show();
                    } else {
                        //toast invalid email
                        prompt = Toast.makeText(appContext, "Invalid Email", Toast.LENGTH_SHORT);
                        prompt.show();
                    }
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.title("Change Email");
            builder.contentView(R.layout.email_settings_dialog);
            builder.positiveAction("Change Email");
            builder.negativeAction("Cancel");
        }
        if (v == passwordButton) {
            SimpleDialog.Builder builder = new SimpleDialog.Builder() {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    password = (EditText) findViewById(R.id.current_password);
                    newPassword = (EditText) findViewById(R.id.new_password);
                    newPasswordReEnter = (EditText) findViewById(R.id.new_password_reeneter);
                    curPassword = password.getText().toString();
                    enteredPW = newPassword.getText().toString();
                    enteredPW2 = newPasswordReEnter.getText().toString();
                    correctPassword = true; // TODO check if password is correct
                    validPassword = verifyPassword(enteredPW, enteredPW2);
                    if (validPassword && correctPassword) {
                        prompt = Toast.makeText(appContext, "Password Changed", Toast.LENGTH_SHORT);
                        prompt.show();
                    } else {
                        //toast invalid email
                        prompt.show();
                    }
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.title("Change Password");
            builder.contentView(R.layout.password_settings_dialog);
            builder.positiveAction("Change Password");
            builder.negativeAction("Cancel");
        }
    }

    private boolean verifyEmail(String incomingEmail) {
        return validations.testEmailSignUp(incomingEmail);
    }

    private boolean verifyPassword(String incomingPass1, String incomingPass2) {
        int verifyPassword1 = validations.testPass(incomingPass1, incomingPass2);


        if (verifyPassword1 == Validations.INCORRECT_LENGTH_TOP) {
            prompt = Toast.makeText(appContext, "Invalid Password Length", Toast.LENGTH_SHORT);
            return false;
        }

        if (verifyPassword1 == Validations.INCORRECT_LENGTH_BOT) {
            prompt = Toast.makeText(appContext, "Invalid Password Length", Toast.LENGTH_SHORT);
            return false;
        }

        if (verifyPassword1 == Validations.REPEAT_NOT_SAME) {
            prompt = Toast.makeText(appContext, "Passwords Do Not Match", Toast.LENGTH_SHORT);
            return false;
        }

        if (verifyPassword1 == Validations.VALIDATION_PASSED) {
            return true;
        }
        return true;
    }
}


