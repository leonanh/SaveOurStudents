package com.sos.saveourstudents;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by HTPC on 4/26/2015.
 */
public class SignupActivity extends Activity implements View.OnClickListener {

    TextView usernamePrompt, passwordPrompt, confirmPasswordPrompt;
    Button confirmSignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernamePrompt = (TextView)findViewById(R.id.create_username_textview_prompt);
        passwordPrompt = (TextView)findViewById(R.id.create_password_textview_prompt);
        confirmPasswordPrompt = (TextView)findViewById(R.id.confirm_password_textview_prompt);
        confirmSignupBtn = (Button)findViewById(R.id.signup_confirm_signup_btn);

        //Apply fonts to each view
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/Moon Flower Bold.ttf");
        usernamePrompt.setTypeface(font);
        passwordPrompt.setTypeface(font);
        confirmPasswordPrompt.setTypeface(font);

        font = Typeface.createFromAsset(getAssets(),"fonts/CODE Bold.otf");
        confirmSignupBtn.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
    }
}
