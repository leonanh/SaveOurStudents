package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    //TODO: REDO TO USE NEW TEXTFIELDS
    private boolean verifyPassword(String incomingPass1, String incomingPass2) {
        int verifyPassword1 = validations.testPass(incomingPass1,incomingPass2);


        if (verifyPassword1 == Validations.INCORRECT_LENGTH_TOP) {
            prompt = Toast.makeText(appContext, R.string.create_password_promp_err, Toast.LENGTH_SHORT);
            prompt.show();
            return false;
        }

        if ( verifyPassword1 == Validations.INCORRECT_LENGTH_BOT) {
            prompt = Toast.makeText(appContext, R.string.create_password_promp_err, Toast.LENGTH_SHORT);
            prompt.show();
            return false;
        }

        if (verifyPassword1 == Validations.REPEAT_NOT_SAME){
            prompt = Toast.makeText(appContext, R.string.create_password_promp_notsame, Toast.LENGTH_SHORT);
            prompt.show();
            return false;
        }

        if (verifyPassword1 == Validations.VALIDATION_PASSED) {
            prompt = Toast.makeText(appContext, R.string.create_password_promp_err, Toast.LENGTH_SHORT);
            prompt.show();
            passBottom1.setTextColor(Color.BLACK);
            passBottom2.setTextColor(Color.BLACK);
            passBottom2.setText(R.string.create_password_confirmation_prompt);
            passBottom1.setText(R.string.create_password_prompt);
            return true;
        }

        return true;
    }

    private boolean verifyEmail(String incomingEmail) {
        boolean emailIsValid = validations.testEmailSignUp(incomingEmail);

        emailText.setTextColor(Color.BLACK);
        emailText.setText(R.string.create_email_prompt);

        if (emailIsValid == false) {
            emailText.setTextColor(Color.RED);
            emailText.setText(R.string.create_email_prompt_err);
            return false;
        }
        return true;

    }

    private boolean verifyUser(String incomingUser) {
        boolean user = validations.testUserName(incomingUser);
        userText.setTextColor(Color.BLACK);
        userText.setText(R.string.create_username_prompt);

        if (!user) {
            userText.setTextColor(Color.RED);
            userText.setText(R.string.create_username_prompt_err);
            return false;
        }

        return true;

    }
}