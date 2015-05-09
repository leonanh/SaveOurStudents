package com.sos.saveourstudents;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HTPC on 4/26/2015.
 */
public class Validations  {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 16;

    public static final int REPEAT_NOT_SAME = 0;
    public static final int INCORRECT_LENGTH_TOP = 1;
    public static final int INCORRECT_LENGTH_BOT = 2;
    public static final int VALIDATION_PASSED = 3;

    public boolean testEmailSignUp(String incomingEmail) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = incomingEmail;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;

    }

    public boolean testUserName(String incomingUser) {
        if (incomingUser.length() > MAX_LENGTH) {
            return false;
        } else if (incomingUser.length() < MIN_LENGTH) {
            return false;
        } else {
            return true;
        }
    }

    public int testPass(String incomingPass1,String incomingPass2) {
        //TextView passBottom, passBottom2;


        if (incomingPass1.length() > MAX_LENGTH || incomingPass1.length() < MIN_LENGTH) {
            //passBottom.setText(R.string.create_password_promp_err);
            return INCORRECT_LENGTH_TOP;
        }

        if (incomingPass2.length() > MAX_LENGTH || incomingPass2.length() < MIN_LENGTH) {
            //passBottom2.setText(R.string.create_password_promp_err);
            return INCORRECT_LENGTH_BOT;
        }

        if (incomingPass1.equals(incomingPass2)) {
            //passBottom2.setText(R.string.create_password_promp_notsame);
            //passBottom.setText(R.string.create_password_prompt);
            return REPEAT_NOT_SAME;
        }

        return VALIDATION_PASSED;
    }





}