package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class LoginActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    //G+
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 300;
    private Button loginButton;


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private ImageView googleSignin;


    //FB
    CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    LoginManager loginManager;
    ImageView fbLogin;


    //GCM
    //public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "862374215545"; //TODO
    GoogleCloudMessaging gcm;
    String regid;


    TextView prompt, forgotLoginBtn, signupBtn;
    EditText usernameField, passwordField;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        setContentView(R.layout.activity_login);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(this);
        }



        prompt = (TextView) findViewById(R.id.login_prompt);
        forgotLoginBtn = (TextView) findViewById(R.id.forgot_login_btn);
        forgotLoginBtn.setOnClickListener(this);
        signupBtn = (TextView) findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(this);
        usernameField = (EditText) findViewById(R.id.username_textfield);
        passwordField = (EditText) findViewById(R.id.password_textfield);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);



        googleSignin = (ImageView) findViewById(R.id.google_login_btn);
        googleSignin.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


//TODO Clear shared prefs, logout of all accounts
        logoutAll();

        /**
         * This is the result from selecting facebook login button
         */
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });


/**
 * This tracks changes to facebook logged in status
 */
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {


                if (currentProfile == null) {
                    System.out.println("Profile changed: logged out");
                    prompt.setText("Logged out");
                } else {
                    System.out.println("Profile changed: " + currentProfile.getFirstName());
                    System.out.println("Save: " + currentProfile.getFirstName());
                    System.out.println("Save: " + currentProfile.getLastName());
                    System.out.println("Save: " + currentProfile.getId());
                    System.out.println("Save: " + currentProfile.getProfilePictureUri(100, 100));
                    //TODO Send to server and save local info
                    prompt.setText("Logged in as " + Profile.getCurrentProfile().getName());

                }
            }
        };


        profileTracker.startTracking();
        fbLogin = (ImageView) findViewById(R.id.facebook_login_btn);
        fbLogin.setOnClickListener(this);

        if (Profile.getCurrentProfile() != null) {
            prompt.setText("Logged in as " + Profile.getCurrentProfile().getName());
        }


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
            Log.i(TAG, "No valid Google Play Services APK found.");
        }





    }

    private void logoutAll() {
        doFacebookLogout();
        signOutFromGplus();
        getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().clear().commit();
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
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


    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                System.out.println("Params1: " + params);
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(); //TODO

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(LoginActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


            protected void onPostExecute(String msg) {
                prompt.append(msg + "\n");
            }
/*
            @Override
            protected Object doInBackground(Object[] params) {
                System.out.println("Params2: "+params[0]);
                return null;
            }*/
        }.execute(null, null, null);

    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        //SharedPreferences.Editor editor = prefs.edit();
        //editor.putString(PROPERTY_REG_ID, regId);
        //editor.putInt(PROPERTY_APP_VERSION, appVersion);
        //editor.commit();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //G+ response
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {   //FB Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == fbLogin) {
            Profile profile = Profile.getCurrentProfile();
            if (profile == null) {
                doFacebookLogin();
            } else {
                doFacebookLogout();
            }

        } else if (v == googleSignin) {
            mGoogleApiClient.connect();

            if (mGoogleApiClient.isConnected()) {
                signOutFromGplus();
                signInWithGplus();
            }

        } else if (v == loginBtn) {
            if (usernameField.getText().toString().isEmpty()) {
                prompt.setText(R.string.usernameEmpty);
                return;
            } else if (passwordField.getText().toString().isEmpty()) {
                prompt.setText(R.string.passwordEmpty);
                return;
            }
            //TODO: Database validation performed here.
            doSOSLogin("SOS", usernameField.getText().toString(), passwordField.getText().toString());

        } else if (v == signupBtn) {

            Intent signup = new Intent(this, SignupActivity.class);
            startActivity(signup);
        } else if (v == forgotLoginBtn) {

            Intent forgot = new Intent(this, ForgotLoginActivity.class);
            startActivity(forgot);
        }


    }


    public void doFacebookLogin() {
        //TODO G+, SOS Logout
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public void doFacebookLogout() {
        loginManager.logOut();
    }


    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        //Userinfo
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);


            //Need firstName, lastName, password (userId)
            //Need email, deviceId


            String firstName = currentPerson.getName().getGivenName();
            String lastName = currentPerson.getName().getFamilyName();
            String userId = currentPerson.getId();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


            prompt.setText("Welcome, " + firstName + " " + lastName);

            createSOSUser("google", firstName, lastName, userId, email);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Person information is null", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        //updateUI(false);
    }


    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    private void signInWithGplus() {
        //TODO FB, SOS Logout
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            //updateUI(false);
            prompt.setText("Logged out");
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

/*
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                prompt.setText("Logged in as " + personName);
                //txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                //new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/


    private void createSOSUser(final String provider, String firstName, String lastName,
                               final String password, final String email) {


        String deviceId = "";
        if (regid != null)
            deviceId = regid;

        //Need firstName, lastName, password (userId)
        //Need email, deviceId


        String url = "http://10.0.3.2:8080/com.mysql.services/rest/serviceclass/createUser?" +
                "firstName=" + firstName +
                "&lastName=" + lastName +
                "&password=" + password +
                "&email=" + email +
                "&deviceId=" + deviceId;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Response: " + response.toString());
                try {

                    //If success = 1, result = success string
                    //If success = 0, result = error string
                    if (response.getString("success").equalsIgnoreCase("1")) {
                        System.out.println("Successful create");
                        //Save prefs?
                    } else {
                        if (response.getString("result").substring(0, response.getString("result").indexOf(" ")).equalsIgnoreCase("Duplicate")) {
                            //TODO Watch for duplicate deviceIDS?
                        }
                    }

                    doSOSLogin(provider, email, password);


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


    private void doSOSLogin(final String provider, String email, String password) {


        String url = "http://10.0.3.2:8080/com.mysql.services/rest/serviceclass/doLogin?" +
                "email=" + email +
                "&password=" + password;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("success").equalsIgnoreCase("1")) {
                        //System.out.println("Login success Response: " + response.toString());

                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("first_name", response.getJSONArray("result").getJSONObject(0).getString("first_name"));
                        editor.putString("last_name", response.getJSONArray("result").getJSONObject(0).getString("last_name"));
                        editor.putString("email", response.getJSONArray("result").getJSONObject(0).getString("email"));
                        editor.putString("image", response.getJSONArray("result").getJSONObject(0).getString("image"));
                        editor.putString("user_id", response.getJSONArray("result").getJSONObject(0).getString("user_id"));
                        editor.putString("provider", provider);
                        editor.commit();


                        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    } else if (response.getString("success").equalsIgnoreCase("0")) {

                        System.out.println("Login Error: " + response.toString());
                        //TODO Let user know what happened
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
