package com.sos.saveourstudents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rey.material.widget.EditText;
import com.sos.saveourstudents.supportclasses.Validations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by HTPC on 4/26/2015.
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    Validations validations = new Validations();
    Button signUpBtn;
    EditText passInput1, passInput2, emailInput, firstNameInput, lastNameInput;
    Toast prompt;
    Context appContext;


    //GCM
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "862374215545"; //Unique identifier for our project
    GoogleCloudMessaging gcm;
    String regid = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(this);
        }

        checkDeviceId();

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
        String emailInput1;
        String passwordInput1;
        String passwordInput2;
        String firstName;
        String lastName;

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
            if (passCheck && email && firstCheck && lastCheck && regid != null) {

                //==================================================================================


                //http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createUser
                String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createUser?" +
                        "firstName=" + firstName  +
                        "&lastName=" + lastName  +
                        "&password=" + Validations.get_SHA_1_SecurePassword(passwordInput1) +
                        "&image=" + "" +
                        "&email=" + emailInput1 +
                        "&deviceId=" + regid;

                System.out.println("ur = " + url);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                        url,(JSONObject)null, new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    JSONObject theResponse = new JSONObject(response.toString());

                                    if(response.getString("success").equalsIgnoreCase("1")){
                                        // Sign Up successful
                                        //Intent loginActivity = new Intent(SignupActivity.this, LoginActivity.class);
                                        //startActivity(loginActivity);
                                        finish();

                                        return;
                                    } else {
                                        //Failed to Sign Up b/c email is taken
                                        emailInput.setError("Email is taken");
                                        //response.getJSONObject("result").getJSONArray("myArrayList");
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


                Singleton.getInstance().addToRequestQueue(jsObjRequest);
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
            passInput1.setError("Not_the_same_password");
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
        //boolean emailIsValid = validations.testEmailSignUp(incomingEmail);


        if (!Validations.isValidEmail(incomingEmail)) {
            emailInput.setError("bad email");
            return false;
        }else{
            emailInput.clearError();
            return true;
        }


    }



    private boolean verifyFirstLast(String incomingFirstLast) {
        boolean firstLastIsValid = validations.testFirstLast(incomingFirstLast);

        if (firstLastIsValid == false) {
            return false;
        }
        return true;

    }



    private void checkDeviceId(){

        //GCM
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                System.out.println("Device registration ID: " + regid);
            }
        } else {
            Log.i("SOS", "No valid Google Play Services APK found.");
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("SOS", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        //final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String registrationId = sharedPref.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("SOS", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = sharedPref.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("SOS", "App version changed.");
            return "";
        }
        return registrationId;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private void registerInBackground() {

        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                System.out.println("Params1: " + params);
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SignupActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    System.out.println("Device registered, registration ID=" + regid);
                    // sendRegistrationIdToBackend();

                    //storeRegistrationId(LoginActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


            protected void onPostExecute(String msg) {
            }

        }.execute(null, null, null);

    }








}
