package com.sos.saveourstudents;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTutorRatingListener} interface
 * to handle interaction events.
 * Use the {@link ViewGroupMembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewGroupMembersFragment extends android.support.v4.app.Fragment {
    private static final String ARG_LISTOFSTUDENTS = "studentsList";
    private static final String ARG_LISTOFTUTORS = "tutorsList";

    private ArrayList<Student> mStudentsList;
    private ArrayList<Student> mTutorsList;

    private OnTutorRatingListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param studentsList Parameter 1.
     * @param tutorsList Parameter 2.
     * @return A new instance of fragment ViewGroupMembersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewGroupMembersFragment newInstance(ArrayList<Student> studentsList,
                                                       ArrayList<Student> tutorsList) {
        ViewGroupMembersFragment fragment = new ViewGroupMembersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LISTOFSTUDENTS, studentsList);
        args.putParcelableArrayList(ARG_LISTOFTUTORS, tutorsList);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewGroupMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudentsList = getArguments().getParcelableArrayList(ARG_LISTOFSTUDENTS);
            mTutorsList = getArguments().getParcelableArrayList(ARG_LISTOFTUTORS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_group_members, container, false);
    }

    // TODO: Hook method into UI event
    public void onTutorRating(boolean rating) {
        if (mListener != null) {
            mListener.onTutorRatingInteraction(rating);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTutorRatingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTutorRatingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTutorRatingListener {
        public void onTutorRatingInteraction(boolean rating);
    }

}
