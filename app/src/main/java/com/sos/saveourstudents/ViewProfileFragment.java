package com.sos.saveourstudents;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.FloatingActionButton;

import org.solovyev.android.views.llm.DividerItemDecoration;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.sos.saveourstudents.ViewProfileFragment.OnEditButtonListener} interface
 * to handle interaction events.
 * Use the {@link ViewProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "student";
    private FloatingActionButton editButton;

    private Student currStudent;

    private OnEditButtonListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currStudent The current logged in student
     * @return A new instance of fragment ViewProfileFragment.
     */
    public static ViewProfileFragment newInstance(Student currStudent) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, currStudent); // ARG_PARAM1 key now holds current Student
        fragment.setArguments(args);
        return fragment;
    }

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Grab the current student from our created Bundle
            currStudent = getArguments().getParcelable(ARG_PARAM1);
        }

        else currStudent = new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering", "Coffee Addict",
                null); // Unnecessary with FragmentTransaction

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateCurrentStudentView();

        LinearLayout aboutMeContents = (LinearLayout) getActivity()
                .findViewById(R.id.profile_aboutMeContents);
        aboutMeContents.setDividerDrawable(getActivity().getResources()
                .getDrawable(R.drawable.abc_list_divider_mtrl_alpha));
        aboutMeContents.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);

        // Set the OnClickListener for the edit floating action button
        editButton = (FloatingActionButton) getActivity().findViewById(R.id.profile_editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onEditButton();
                }
            }
        });
    }

    /**
     * Update the View with the student's current values
     */
    private void updateCurrentStudentView() {
        ((TextView) getActivity().findViewById(R.id.profile_firstName))
                .setText(currStudent.getFirstName());
        ((TextView) getActivity().findViewById(R.id.profile_lastName))
                .setText(currStudent.getLastName());
        ((TextView) getActivity().findViewById(R.id.profile_myRating))
                .setText(((Integer) currStudent.getRating()).toString());
        ((TextView) getActivity().findViewById(R.id.profile_mySchool))
                .setText(currStudent.getSchool());
        ((TextView) getActivity().findViewById(R.id.profile_myMajor))
                .setText(currStudent.getMajor());
        ((TextView) getActivity().findViewById(R.id.profile_myDescription))
                .setText(currStudent.getDescription());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditButtonListener");
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
    public interface OnEditButtonListener{
        void onEditButton();
    }

}
