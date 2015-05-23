package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HTPC on 4/26/2015.
 */
public class ForgotLoginActivity extends Activity implements View.OnClickListener {

    TextView forgetPrompt;
    Button sendEmailBtn;
    EditText emailentry;
    TextView errormsg;
    Toast emailsent;
    Context appContext;

    //private String jsonResponse;
    Toast prompt; //feedback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        forgetPrompt = (TextView)findViewById(R.id.forgot_credentials_prompt);
        sendEmailBtn = (Button)findViewById(R.id.send_credentials_to_email_btn);
        emailentry = (EditText)findViewById(R.id.forgot_login_email_textfield);
        errormsg = (TextView)findViewById(R.id.forgot_login_error_text);
        appContext = getApplicationContext();

        //Apply fonts to each view
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/Moon Flower Bold.ttf");
        forgetPrompt.setTypeface(font);

        font = Typeface.createFromAsset(getAssets(),"fonts/CODE Bold.otf");
        sendEmailBtn.setTypeface(font);

        sendEmailBtn.setOnClickListener(this);

        prompt = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
        String emailInput;
    }

    @Override
    public void onClick(View v) {
        if(v == sendEmailBtn) {
            if(emailentry.getText().toString().isEmpty()){
                Log.d("debug","errmsg empty");
                errormsg.setText(R.string.emailEmpty);
                return;
            }

            //TODO: Add database validation
            doRetrieveInfo();

            emailsent = Toast.makeText(appContext, "Email sent!", Toast.LENGTH_SHORT);
            emailsent.show();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
    }

    private void doRetrieveInfo(final String email)
    {
        //find the valid url
        String url = "http://53.200.33.91:8080/com.mysql.services/rest/serviceclass/forgotPassword?" +
                "&email" + emailInput;

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    // response will be json object
                    String email = response.getString("email");

                    if (response.getString("success").equalsIgnoreCase("1")) {
                        //email exists
                        emailsent = Toast.makeText(appContext, "Email sent!", Toast.LENGTH_SHORT);
                        emailsent.show();
                        Intent loginActivity = new Intent(this, LoginActivity.class);
                        startActivity(loginActivity);
                        finish();

                    }else {
                        prompt = Toast.makeText(appContext, "Email DNE!", Toast.LENGTH_SHORT);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {

            System.out.println("Error: " + error.toString());
        }

        });


    }
}