package com.sos.saveourstudents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



public class EditQuestionMembersFragment extends Fragment {


    private String mQuestionId;
    private Context mContext;



    public static EditQuestionMembersFragment newInstance(String questionId) {
        EditQuestionMembersFragment fragment = new EditQuestionMembersFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuestionMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionId = getArguments().getParcelable("questionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_view_group_members, container, false);

        mQuestionId = getArguments().getString("questionId");
        if(mQuestionId.equalsIgnoreCase(""))
            Toast.makeText(mContext, "QuestionId empty in viewQuestiomFrag", Toast.LENGTH_SHORT).show();




        return rootView;
    }



}
