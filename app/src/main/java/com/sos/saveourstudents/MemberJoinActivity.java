package com.sos.saveourstudents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.rey.material.widget.FloatingActionButton;


/**
 * Created by Xian on 5/16/2015.
 */
public class MemberJoinActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton acceptButton;
    FloatingActionButton declineButton;
    RelativeLayout userProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_join_layout);


        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        //String name = sharedPref.getString("first_name", "") + " "+ sharedPref.getString("last_name", "");

        Student enteringStudent = new Student(sharedPref.getString("first_name", ""), sharedPref.getString("last_name", ""), 0, "UCSD", "Computer Engineering",
                "Coffee Addict", null);






        TextView joiningMemberName = (TextView) findViewById(R.id.member_name);
        joiningMemberName.setText(enteringStudent.getFirstName());

        TextView memberInfoName = (TextView) findViewById(R.id.joining_member_name);
        memberInfoName.setText(enteringStudent.getFirstName() + " " + enteringStudent.getLastName());

        TextView memberInfoMajor = (TextView) findViewById(R.id.joining_member_major);
        memberInfoMajor.setText(enteringStudent.getMajor());

        ImageView userImage = (ImageView) findViewById(R.id.joining_member_image);
        getUserImage(sharedPref.getString("image", ""), userImage);

        userProfile = (RelativeLayout) findViewById(R.id.joining_member_info);
        acceptButton = (FloatingActionButton) findViewById(R.id.accept_fab);
        declineButton = (FloatingActionButton) findViewById(R.id.decline_fab);

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
            //Check out member
            startActivity(new Intent(this, ProfileActivity.class));
            //finish();
        }
    }



    private void getUserImage(String imageUrl, final ImageView imageView){



        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        // If you are using normal ImageView
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Image Load Error: " + error.getMessage());
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {

                    imageView.setImageBitmap(response.getBitmap());
                    //TODO imageview.setImageBitmap(response.getBitmap());
                }
                else{
                    // Default image...
                }
            }
        });



    }


}
