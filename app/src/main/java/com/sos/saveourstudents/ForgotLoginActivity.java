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
public class ForgotLoginActivity extends Activity implements View.OnClickListener {

    TextView forgetPrompt;
    Button sendEmailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        forgetPrompt = (TextView)findViewById(R.id.forgot_credentials_prompt);
        sendEmailBtn = (Button)findViewById(R.id.send_credentials_to_email_btn);

        //Apply fonts to each view
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/Moon Flower Bold.ttf");
        forgetPrompt.setTypeface(font);

        font = Typeface.createFromAsset(getAssets(),"fonts/CODE Bold.otf");
        sendEmailBtn.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
    }
}

