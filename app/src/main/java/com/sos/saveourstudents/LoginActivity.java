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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
    String facebookEmail = null;
    String userImageUrl = "";

    //GCM
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "862374215545"; //TODO
    GoogleCloudMessaging gcm;
    String regid;

    boolean isLogging = false;

    Toast prompt;
    Context appContext;
    TextView logoLabel;
    TextView forgotLoginBtn, signupBtn;
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

        registerInBackground();

        forgotLoginBtn = (TextView) findViewById(R.id.forgot_login_btn);
        forgotLoginBtn.setOnClickListener(this);
        signupBtn = (TextView) findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(this);
        logoLabel = (TextView)findViewById(R.id.login_logo_label);
        usernameField = (EditText) findViewById(R.id.username_textfield);
        passwordField = (EditText) findViewById(R.id.password_textfield);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        appContext = getApplicationContext();

        googleSignin = (ImageView) findViewById(R.id.google_login_btn);
        googleSignin.setOnClickListener(this);
        //btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        //btnSignIn.setOnClickListener(this);
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

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {

                                    Profile profile = Profile.getCurrentProfile();

                                    if(profile != null) {
                                        /*
                                        System.out.println("object: " + object.getString("email"));
                                        System.out.println("Profile changed: " + profile);
                                        System.out.println("Save: " + profile.getFirstName());
                                        System.out.println("Save: " + profile.getLastName());
                                        System.out.println("Save: " + profile.getId());
                                        System.out.println("Save: " + profile.getProfilePictureUri(100, 100));
                                        */

                                        userImageUrl = profile.getProfilePictureUri(100, 100).toString();
                                        facebookEmail = object.getString("email");

                                        createSOSUser("facebook",
                                                profile.getFirstName(),
                                                profile.getLastName(),
                                                profile.getId(),
                                                facebookEmail);

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
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


                //System.out.println("userImageUrl: "+userImageUrl);
                //System.out.println("facebookEmail: "+facebookEmail);

/*
                if(currentProfile != null && !isLogging && userImageUrl != null && facebookEmail!=null){

                    System.out.println("Success logging into FB");
                    userImageUrl = currentProfile.getProfilePictureUri(100, 100).toString(); //TODO send with create user
                    isLogging = true;
                    createSOSUser("facebook",
                            currentProfile.getFirstName(),
                            currentProfile.getLastName(),
                            currentProfile.getId(),
                            facebookEmail);
                }
                else{
                    System.out.println("Tried to do FB, but could not");
                    System.out.println("isLogging: "+isLogging);
                    System.out.println("currentProfile: "+currentProfile);
                    System.out.println("userImageUrl: "+userImageUrl);

                }
*/

            }
        };


        profileTracker.startTracking();
        fbLogin = (ImageView) findViewById(R.id.facebook_login_btn);
        fbLogin.setOnClickListener(this);



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
        getGCMPreferences(this).edit().clear().commit();
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
            doFacebookLogin();

        } else if (v == googleSignin) {
            mGoogleApiClient.connect();
            signOutFromGplus();

            if (mGoogleApiClient.isConnected()) {
                signInWithGplus();
            }

        } else if (v == loginBtn) {
            if (usernameField.getText().toString().isEmpty()) {
                prompt = Toast.makeText(appContext, R.string.usernameEmpty, Toast.LENGTH_SHORT);
                prompt.show();
                return;
            } else if (passwordField.getText().toString().isEmpty()) {
                prompt = Toast.makeText(appContext, R.string.passwordEmpty, Toast.LENGTH_SHORT);
                prompt.show();
                return;
            }

            //TODO Remove this.Do real validation.
            //Intent mainActivity = new Intent(this, MainActivity.class);
            //startActivity(mainActivity);
            //finish();

            Log.d("Debug","Logging in");
            doSOSLogin("SOS", usernameField.getText().toString(), passwordField.getText().toString());
            Log.d("Debug","Fiished Logging in");

        } else if (v == signupBtn) {

            Intent signup = new Intent(this, SignupActivity.class);
            startActivity(signup);
        } else if (v == forgotLoginBtn) {

            Intent forgot = new Intent(this, ForgotLoginActivity.class);
            startActivity(forgot);
        }


    }


    public void doFacebookLogin() {//TODO Glitchy, needs work. Duplicate emails present a problem.
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    public void doFacebookLogout() {
        loginManager.logOut();
    }


    @Override
    public void onConnected(Bundle arg0) {
        //mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        //Userinfo
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);

            //System.out.println("getImage: " + );
            userImageUrl = currentPerson.getImage().getUrl();
            String firstName = currentPerson.getName().getGivenName();
            String lastName = currentPerson.getName().getFamilyName();
            String userId = currentPerson.getId();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


            //prompt.setText("Welcome, " + firstName + " " + lastName);



            createSOSUser("google", firstName, lastName, userId, email);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Person information is null", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
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
            resolveSignInError();

        }

    }

    private void signInWithGplus() {

        if (!mGoogleApiClient.isConnecting()) {
            resolveSignInError();
        }
    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            //prompt.setText("Logged out");
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


    private void createSOSUser(final String provider, String firstName, String lastName,
                               final String password, final String email) {


        String deviceId = "";
        if (regid != null)
            deviceId = regid;

        //Need firstName, lastName, password (userId)
        //Need email, deviceId

        System.out.println("userImageUrl: "+userImageUrl);



        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createUser?" +
                "firstName=" + firstName +
                "&lastName=" + lastName +
                "&password=" + Singleton.get_SHA_1_SecurePassword(password) +
                "&email=" + email +
                "&image=" + userImageUrl +
                "&deviceId=" + deviceId;


/*
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("firstName", firstName));
        params.add(new BasicNameValuePair("lastName", lastName));
        params.add(new BasicNameValuePair("password", Singleton.get_SHA_1_SecurePassword(password)));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("image", userImageUrl));
        params.add(new BasicNameValuePair("deviceId", deviceId));


        String paramString = URLEncodedUtils.format(params, "utf-8");//.replace("+", "%20");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/createUser?"+paramString;
*/



        System.out.println("createUser URL: "+url);

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
                    } else {
                        if (response.getString("result").substring(0, response.getString("result").indexOf(" ")).equalsIgnoreCase("Duplicate")) {
                            //TODO Watch for duplicate deviceIDS?
                            isLogging = false;
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
                isLogging = false;
            }

        });


        Singleton.getInstance().addToRequestQueue(jsonObjReq);


    }


    private void doSOSLogin(final String provider, String email, String password) {

        //http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getTags
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/doLogin?" +
                "email=" + email +
                "&password=" + Singleton.get_SHA_1_SecurePassword(password);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("success").equalsIgnoreCase("1") && response.getString("expectResults").equalsIgnoreCase("1")) {
                        System.out.println("Login success Response: " + response.toString());
                        //TODO we still "success = 1" here even from wrong password. FIX this
                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("first_name", response.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("first_name"));
                        editor.putString("last_name", response.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("last_name"));
                        editor.putString("email", response.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("email"));
                        editor.putString("image", response.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("image"));
                        //editor.putString("image", userImageUrl);//TODO FIX this needs to be saved from createUser call
                        editor.putString("user_id", response.getJSONObject("result").getJSONArray("myArrayList").getJSONObject(0).getJSONObject("map").getString("user_id"));
                        editor.putString("provider", provider);
                        editor.commit();


                        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    } else if (response.getString("success").equalsIgnoreCase("1") && response.getString("expectResults").equalsIgnoreCase("0")) {//TODO this check is not correct
                        prompt = Toast.makeText(appContext, "Invalid login!", Toast.LENGTH_SHORT);//TODO: Use R String
                        prompt.show();
                    }else{
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