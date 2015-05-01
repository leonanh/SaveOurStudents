package com.sos.saveourstudents;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.util.regex.*;

private final Pattern hasUppercase = Pattern.compile("[A-Z]");
private final Pattern hasLowercase = Pattern.compile("[a-z]");
private final Pattern hasNumber = Pattern.compile("\\d");
private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");

/**
 * Created by HTPC on 4/26/2015.
 */
public class Validations extends Activity implements View.OnClickListener {

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
    }

    @Override
    public void onClick(View v) {
    }
    */

    boolean = false;  //test result

    public static boolean validUsername( String userName )
    {
        if (userName.length()  > 16)
            return false;
        if (userName.length() <  3)
            return false;
        return true;
    }

    public static boolean validPassword( String password )
    {
        if (password.length() < 8)
            return false;

        return true;

    }
}
