package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import com.rey.material.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HTPC on 4/26/2015.
 */
public class SignupActivity extends Activity implements View.OnClickListener {
    Validations validations = new Validations();

    Button signUpBtn;
    EditText userInput, passInput1, passInput2, emailInput;
    Toast prompt;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userInput = (EditText) findViewById(R.id.signup_username_textfield);
        passInput1 = (EditText) findViewById(R.id.signup_password_textfield);
        passInput2 = (EditText) findViewById(R.id.signup_password_confirm_textfield);
        emailInput = (EditText) findViewById(R.id.signup_email_textfield);
        signUpBtn = (Button) findViewById(R.id.signup_confirm_signup_btn);
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

        if (v == signUpBtn) {

            usernameInput = userInput.getText().toString();
            emailInput1 = emailInput.getText().toString();
            passwordInput1 = passInput1.getText().toString();
            passwordInput2 = passInput2.getText().toString();

            boolean user = verifyUser(usernameInput);
            boolean email = verifyEmail(emailInput1);
            boolean passCheck = verifyPassword(passwordInput1,passwordInput2);

            if (!passCheck || !user || !email) {
                // do not continue its bad
            }
            //its good, sign up successful
        }
    }

    private boolean verifyPassword(String incomingPass1, String incomingPass2) {
        int verifyPassword = validations.testPass(incomingPass1,incomingPass2);


        if (verifyPassword == Validations.INCORRECT_LENGTH_TOP) {
            userInput.setError("INCORRECT LENFT");
            return false;
        }

        if ( verifyPassword == Validations.INCORRECT_LENGTH_BOT) {
            return false;
        }

        if (verifyPassword == Validations.REPEAT_NOT_SAME){
            return false;
        }

        if (verifyPassword == Validations.VALIDATION_PASSED) {
            return true;
        }

        return true;
    }

    private boolean verifyEmail(String incomingEmail) {
        boolean emailIsValid = validations.testEmailSignUp(incomingEmail);


        if (emailIsValid == false) {
            return false;
        }
        return true;

    }

    private boolean verifyUser(String incomingUser) {
        boolean user = validations.testUserName(incomingUser);

        if (!user) {
            return false;
        }

        return true;

    }
}