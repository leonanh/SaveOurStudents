package com.sos.saveourstudents;

/**
 * Created by Toni on 4/22/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLogin extends Fragment {

    EditText username, password;
    Button login;
    TextView signup, forgotLogin, loginPrompt;

    public FragmentLogin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

//        username = (EditText) getView().findViewById(R.id.username_textfield);
//        password = (EditText) getView().findViewById(R.id.password_textfield);
//        login = (Button) getView().findViewById(R.id.login_btn);
//        signup = (TextView) getView().findViewById(R.id.signup_btn);
//        forgotLogin = (TextView) getView().findViewById(R.id.forgot_login_btn);
//        loginPrompt = (TextView) getView().findViewById(R.id.login_prompt);

        return rootView;
    }
}