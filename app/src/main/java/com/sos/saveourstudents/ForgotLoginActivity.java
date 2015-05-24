package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    Button sendEmailBtn;
    com.rey.material.widget.EditText emailentry;
    //TextView errormsg;
    Toast emailsent;
    Toast emaildne;
    Context appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        //forgetPrompt = (TextView)findViewById(R.id.forgot_credentials_prompt);
        sendEmailBtn = (Button)findViewById(R.id.send_credentials_to_email_btn);
        emailentry = (com.rey.material.widget.EditText)findViewById(R.id.forgot_login_email_textfield);
        //errormsg = (TextView)findViewById(R.id.forgot_login_error_text);



        sendEmailBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == sendEmailBtn) {
            if(emailentry.getText().toString().isEmpty()) {
                Log.d("debug","errmsg empty");
                //errormsg.setText(R.string.emailEmpty);
                return;
            }

            //TODO: Add database validation
            doRetrieveInfo(emailentry.getText().toString());

            /*
            //emailsent = Toast.makeText(appContext, "Email sent!", Toast.LENGTH_SHORT);
            //emailsent.show();

            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();*/
        }
    }

    private void doRetrieveInfo(final String email)
    {
        //find the valid url
        //http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/forgotPassword?" +
                "email=" + email;


        System.out.println("Url: "+url);
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if(response.getString("expectResults").equalsIgnoreCase("0")) {
                        emaildne = Toast.makeText(appContext, R.string.invalidEmail, Toast.LENGTH_SHORT);
                        emaildne.show();
                        //System.out.println("debug");
                    } else if (response.getString("success").equalsIgnoreCase("1")) {
                        //email exists
                        emailsent = Toast.makeText(appContext, R.string.emailSent, Toast.LENGTH_SHORT);
                        emailsent.show();
                        Intent loginActivity = new Intent(ForgotLoginActivity.this, LoginActivity.class);
                        startActivity(loginActivity);
                        finish();

                        return;
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

        Singleton.getInstance().addToRequestQueue(jsonObjReq);

    }

}