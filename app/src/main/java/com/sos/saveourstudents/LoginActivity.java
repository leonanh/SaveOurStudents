package com.sos.saveourstudents;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends Activity implements android.view.View.OnClickListener {

    TextView prompt, forgotLoginBtn, signupBtn;
    EditText usernameField, passwordField;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        prompt = (TextView) findViewById(R.id.login_prompt);
        forgotLoginBtn = (TextView) findViewById(R.id.forgot_login_btn);
        forgotLoginBtn.setOnClickListener(this);
        signupBtn = (TextView) findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(this);
        usernameField = (EditText) findViewById(R.id.username_textfield);
        passwordField = (EditText) findViewById(R.id.password_textfield);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginBtn) {
            if(usernameField.getText().toString().isEmpty()){
                prompt.setText(R.string.usernameEmpty);
                return;
            }else if(passwordField.getText().toString().isEmpty()){
                prompt.setText(R.string.passwordEmpty);
                return;
            }
            //TODO: Database validation performed here.
            Intent mainActivity = new Intent(this, MainActivity.class);
            MainActivity.LOGGED_IN = true;//TODO: Temporary variable
            startActivity(mainActivity);
            finish();
        } else if (v == signupBtn) {
            Intent signup = new Intent(this, SignupActivity.class);
            startActivity(signup);
        } else if (v == forgotLoginBtn) {
            Intent forgot = new Intent(this, ForgotLoginActivity.class);
            startActivity(forgot);
        }
    }
}
