package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.drive.realtime.internal.event.ValueChangedDetails;
import com.rey.material.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HTPC on 4/26/2015.
 */
public class SignupActivity extends Activity implements View.OnClickListener {
    Validations validations = new Validations();

    Button signUpBtn;
    EditText passInput1, passInput2, emailInput, firstNameInput, lastNameInput;
    Toast prompt;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        passInput1 = (EditText) findViewById(R.id.signup_password_textfield);
        passInput2 = (EditText) findViewById(R.id.signup_password_confirm_textfield);
        emailInput = (EditText) findViewById(R.id.signup_email_textfield);
        firstNameInput = (EditText) findViewById(R.id.signup_first_name);
        lastNameInput = (EditText) findViewById(R.id.signup_last_name);
        signUpBtn = (Button) findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(this);

        appContext = getApplicationContext();   //Instantiate toast.
        prompt = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View v) {
        String usernameInput;
        String emailInput1;
        String passwordInput1;
        String passwordInput2;
        String firstName;
        String lastName;
        //TODO JSON NOT WORKING!!!
        if (v == signUpBtn) {

            emailInput1 = emailInput.getText().toString();
            passwordInput1 = passInput1.getText().toString();
            passwordInput2 = passInput2.getText().toString();
            firstName = firstNameInput.getText().toString();
            lastName = lastNameInput.getText().toString();

            boolean email = verifyEmail(emailInput1);
            boolean passCheck = verifyPassword(passwordInput1,passwordInput2);
            boolean firstCheck = verifyFirstLast(firstName);
            boolean lastCheck = verifyFirstLast(lastName);
            //Perform database signup
            if (passCheck && email && firstCheck && lastCheck) {

                //==================================================================================
                String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                        Secure.ANDROID_ID);

                //http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getTags
                String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createUser?" +
                        "firstName=" + firstName  +
                        "&lastName=" + lastName  +
                        "&password=" + Singleton.get_SHA_1_SecurePassword(passwordInput1) +
                        "&email" + emailInput1 +
                        "&deviceId=" + android_id;


                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                        (JSONObject) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getString("success").equalsIgnoreCase("1")){
                                Intent mainActivity = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }else{
                                prompt = Toast.makeText(appContext, "Invalid login!", Toast.LENGTH_SHORT);//TODO: Use R String
                                prompt.show();
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
                //==================================================================================
            }
        }
    }

    private boolean verifyPassword(String incomingPass1, String incomingPass2) {
        int verifyPassword1 = validations.testPass(incomingPass1, incomingPass2);


        if (verifyPassword1 == Validations.INCORRECT_LENGTH_TOP) {
            passInput1.setError("incorrect length");
            return false;
        }

        if ( verifyPassword1 == Validations.INCORRECT_LENGTH_BOT) {
            passInput2.setError("incorrect length");
            return false;
        }

        if (verifyPassword1 == Validations.REPEAT_NOT_SAME){
            passInput1.setError("Not the same password");
            passInput2.setError("Not the same password");
            return false;
        }

        if (verifyPassword1 == Validations.VALIDATION_PASSED) {
            passInput1.clearError();
            passInput2.clearError();
            return true;
        }

        return true;
    }

    private boolean verifyEmail(String incomingEmail) {
        boolean emailIsValid = validations.testEmailSignUp(incomingEmail);


        if (emailIsValid == false) {
            emailInput.setError("bad email");
            return false;
        }
        emailInput.clearError();
        return true;

    }



    private boolean verifyFirstLast(String incomingFirstLast) {
        boolean firstLastIsValid = validations.testEmailSignUp(incomingFirstLast);

        if (firstLastIsValid == false) {
            return false;
        }
        return true;

    }
}
