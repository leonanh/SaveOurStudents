package com.sos.saveourstudents;

import android.support.v7.app.ActionBarActivity;

import java.util.regex.Pattern;



/**
 * Created by HTPC on 4/26/2015.
 */
public class Validations extends ActionBarActivity {

    private final Pattern hasUppercase = Pattern.compile("[A-Z]");
    private final Pattern hasLowercase = Pattern.compile("[a-z]");
    private final Pattern hasNumber = Pattern.compile("\\d");
    private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");


    //boolean = false;  //test result

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


    /**
     * Verify password is valid, can return bool or int or whatever is clear to distinguish validity
     * @param pass1
     * @return
     */
    public String validateNewPass(String pass1) {
        if (pass1 == null) {
            //return error
        }

        if (pass1.isEmpty()) {
            //return error
        }

        if (!hasUppercase.matcher(pass1).find()) {
            //error - logger.info(pass1 + " <-- needs uppercase");
        }

        if (!hasLowercase.matcher(pass1).find()) {
            //error - logger.info(pass1 + " <-- needs lowercase");
        }

        if (!hasNumber.matcher(pass1).find()) {
            //error - logger.info(pass1 + "<-- needs a number");
        }

        if (!hasSpecialChar.matcher(pass1).find()) {
            //error - logger.info(pass1 + "<-- needs a specail character");
        }


        return null; //return whatever indicator you feel is useful
    }







}
