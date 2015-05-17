package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by Xian on 5/16/2015.
 */
public class MemberJoinActivity extends Activity implements View.OnClickListener{

    Button acceptButton;
    Button declineButton;
    RelativeLayout userProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_join_layout);

        Student enteringStudent = new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null);

        TextView joiningMemberName = (TextView) findViewById(R.id.member_name);
        joiningMemberName.setText(enteringStudent.getFirstName());

        TextView memberInfoName = (TextView) findViewById(R.id.joining_member_name);
        memberInfoName.setText(enteringStudent.getFirstName() + " " + enteringStudent.getLastName());

        TextView memberInfoMajor = (TextView) findViewById(R.id.joining_member_major);
        memberInfoMajor.setText(enteringStudent.getMajor());

        userProfile = (RelativeLayout) findViewById(R.id.joining_member_info);
        acceptButton = (Button) findViewById(R.id.member_accept_button);
        declineButton = (Button) findViewById(R.id.member_decline_button);

        userProfile.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        if(v == acceptButton)
        {
            //Accept member
            Intent questionActivity = new Intent(this, QuestionActivity.class);
            questionActivity.putExtra("type", 0);
            startActivity(questionActivity);
            finish();
        }
        else if (v == declineButton)
        {
            //Decline member
            //Intent questionActivity = new Intent(this, QuestionActivity.class);
            //questionActivity.putExtra("type", 0);
            //startActivity(questionActivity);
            finish();
        }
        else if (v == userProfile)
        {
            //Decline member
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }


}
