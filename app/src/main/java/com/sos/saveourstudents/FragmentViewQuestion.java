package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rey.material.widget.Button;
import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deamon on 5/6/15.
 */
public class FragmentViewQuestion extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    RecycleViewAdapter mAdapter;
    RecyclerView mRecyclerView;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    static List mQuestionList;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = this.getActivity();

        rootView = inflater.inflate(R.layout.fragment_view_question, container,
                false);

        //Call this method to initiate volley request
        getQuestionData();

        View taglist = rootView.findViewById(R.id.tag_list_layout);

        for(int i = 0; i < 3; i++) {
            TextView tag = new TextView(taglist.getContext());
            tag.setText(" #dummyTag ");
            tag.setTextColor(getResources().getColor(R.color.primary_dark));
            ((LinearLayout)taglist).addView(tag);
        }

        FloatingActionButton tutorOrMember = (FloatingActionButton) rootView.findViewById(R.id.group_action);
        tutorOrMember.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to join as a tutor or a group member?");

        builder.setPositiveButton("Member", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the tutor button
            }
        });
        builder.setNegativeButton("Tutor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the member button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Helper method to initiate Volley call and refresh UI
     */
    private void getQuestionData() {


        //TODO turn into server call (volley)
        mQuestionList = new ArrayList<Question>();
        for(int a = 0; a < 6; a++){
            Question temp = new Question("Question "+a);
            mQuestionList.add(temp);
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new RecycleViewAdapter(mQuestionList, R.layout.fragment_question_comment, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /**
     * Handle card views
     */
    public static class CardManager {

        CardManager mCardManagerInstance = null;

        public CardManager getInstance() {
            if (mCardManagerInstance == null) {
                mCardManagerInstance = new CardManager();
            }

            return mCardManagerInstance;
        }

        public static List<Question> getCounters() {
            if (mQuestionList == null) {
                mQuestionList = new ArrayList<Question>();
            }
            return mQuestionList;
        }
    }


    /**
     * Handle heavy lifting for Recyclerview
     */

    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_MEMBER = 1;

        Context context;
        private int rowLayout;
        RecycleViewAdapter memberAdapter;
        RecyclerView memberRecyclerView;

        public class ViewHolder extends RecyclerView.ViewHolder {
            int Holderid;

            TextView textView;
            ImageView imageView;
            ImageView profile;
            TextView userName;
            TextView email;

            public ViewHolder(View itemView, int viewType) {
                super(itemView);

                if(viewType == TYPE_ITEM) {
                    //textView = (TextView) itemView.findViewById(R.id.textview);
                    //imageView = (ImageView) itemView.findViewById(R.id.imageview);
                    Holderid = TYPE_ITEM;
                }
                else if(viewType == TYPE_MEMBER){
                    memberRecyclerView = (RecyclerView) itemView.findViewById(R.id.my_recycler_view);
                    buildMemberRecyclerView();
                    Holderid = TYPE_MEMBER;
                }
            }

        }

        private void buildMemberRecyclerView(){

            memberRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            memberRecyclerView.setItemAnimator(new DefaultItemAnimator());

            String[] dataset = new String[5];
            dataset[0] = "12343";
            dataset[1] = "fdgf";
            dataset[2] = "12fvdfggfd343";
            dataset[3] = "gfdgdfg";
            MemberListAdapter adapter = new MemberListAdapter(dataset);
            memberRecyclerView.setAdapter(adapter);

        }

        public RecycleViewAdapter(List mQuestionList, int rowLayout, Context context) {
            //this.questions = questions;
            this.rowLayout = rowLayout;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            if (viewType == TYPE_ITEM) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_question_comment,parent,false);
            } else if (viewType == TYPE_MEMBER) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_group_item,parent,false);
            }

            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;

        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if(viewHolder.Holderid == TYPE_ITEM) {
                //viewHolder.textView.setText(mNavTitles[position - 1]);
                //viewHolder.imageView.setImageResource(mIcons[position -1]);
            }
            else if(viewHolder.Holderid == TYPE_MEMBER) {
                //viewHolder.textView.setText(mNavTitles[position - 1]);
                //viewHolder.imageView.setImageResource(mIcons[position -1]);
            }
            else{
                //viewHolder.profile.setImageResource(profile);
                //viewHolder.userName.setText(questions.get(position-1).title);
                //viewHolder.email.setText(email);
            }

        }

        @Override
        public int getItemCount() {
            return mQuestionList == null ? 0 : mQuestionList.size();
        }

        // With the following method we check what type of view is being passed
        @Override
        public int getItemViewType(int position) {
           if (position == 0)
                return TYPE_MEMBER;

            return TYPE_ITEM;
        }

    }

}


class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    private String[] mDataset;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = v;
        }
    }

    public MemberListAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_nav_drawer_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

