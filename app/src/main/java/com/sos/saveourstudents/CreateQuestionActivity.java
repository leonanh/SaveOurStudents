package com.sos.saveourstudents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class CreateQuestionActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);


        if (findViewById(R.id.fragment_container) != null) {
            String questionId ="";
            if(getIntent().getExtras().containsKey("questionId")){
                questionId = getIntent().getExtras().getString("questionId");
            }
            Fragment theFragToShow = CreateQuestionFragment.newInstance(questionId);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, theFragToShow).commit();


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }





}
