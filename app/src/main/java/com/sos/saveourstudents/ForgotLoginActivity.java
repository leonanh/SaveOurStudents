package com.sos.saveourstudents;

import android.app.Activity;
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

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by HTPC on 4/26/2015.
 */
public class ForgotLoginActivity extends Activity implements View.OnClickListener {

    Button sendEmailBtn;
    com.rey.material.widget.EditText emailentry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login);

        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(this);
        }

        sendEmailBtn = (Button)findViewById(R.id.send_credentials_to_email_btn);
        emailentry = (com.rey.material.widget.EditText) findViewById(R.id.forgot_login_email_textfield);
        sendEmailBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == sendEmailBtn) {
            if(emailentry.getText().toString().isEmpty()) {
                Log.d("debug","errmsg empty");
                emailentry.setError("");
            }else{

                emailentry.clearError();
                doRetrieveInfo(emailentry.getText().toString());
            }

        }
    }

    private void doRetrieveInfo(final String email)
    {
        //find the valid url
        //http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getQuestions
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/forgotPassword?";


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        String paramString = URLEncodedUtils.format(params, "utf-8");

        url = url + paramString;

        System.out.println("Url: "+url);
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if(response.getString("expectResults").equalsIgnoreCase("0")) {
                        Toast.makeText(ForgotLoginActivity.this, R.string.invalidEmail, Toast.LENGTH_SHORT).show();
                        //System.out.println("debug");
                    } else if (response.getString("success").equalsIgnoreCase("1")) {
                        //email exists
                        Toast.makeText(ForgotLoginActivity.this, R.string.emailSent, Toast.LENGTH_SHORT).show();
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