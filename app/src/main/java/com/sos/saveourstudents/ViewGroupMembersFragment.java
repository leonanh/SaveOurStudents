package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rey.material.app.Dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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

    private List<Student> mStudentsList;
    private List<Student> mTutorsList;

    private OnTutorRatingListener mTutorRatingListener;
    private OnMemberRemoveListener mMemberRemoveListener;

    private static ListView mStudentsListView;
    private static ListView mTutorsListView;

    private StudentsArrayAdapter mStudentsListViewArrayAdapter;
    private TutorsArrayAdapter mTutorsListViewArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param studentsList Parameter 1.
     * @param tutorsList   Parameter 2.
     * @return A new instance of fragment ViewGroupMembersFragment.
     */
    public static ViewGroupMembersFragment newInstance(List<Student> studentsList,
                                                       List<Student> tutorsList) {
        ViewGroupMembersFragment fragment = new ViewGroupMembersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LISTOFSTUDENTS, (ArrayList<Student>) studentsList);
        args.putParcelableArrayList(ARG_LISTOFTUTORS, (ArrayList<Student>) tutorsList);
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

    public void onTutorRating(boolean rating, String currUserId) {
        if (mTutorRatingListener != null) {
            mTutorRatingListener.onTutorRatingInteraction(rating, currUserId);
        }
    }

    public void onMemberRemove(String memberUserId, boolean tutor) {
        if (mMemberRemoveListener != null) {
            mMemberRemoveListener.onMemberRemoveInteraction(memberUserId, tutor);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set up ListViews for Students and Tutors
        mStudentsListView = (ListView) getActivity()
                .findViewById(R.id.view_group_members_students_listView);
        mTutorsListView = (ListView) getActivity()
                .findViewById(R.id.view_group_members_tutors_listView);

        mStudentsListViewArrayAdapter = new StudentsArrayAdapter(getActivity(),
                mStudentsList);

        mTutorsListViewArrayAdapter = new TutorsArrayAdapter(getActivity(),
                mTutorsList);

        mStudentsListView.setAdapter(mStudentsListViewArrayAdapter);

        mTutorsListView.setAdapter(mTutorsListViewArrayAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTutorRatingListener = (OnTutorRatingListener) activity;
            mMemberRemoveListener = (OnMemberRemoveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTutorRatingListener" +
                    " and OnMemberRemoveListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTutorRatingListener = null;
        mMemberRemoveListener = null;
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
        void onTutorRatingInteraction(boolean rating, String currUserId);
    }

    public interface OnMemberRemoveListener {
        void onMemberRemoveInteraction(String memberUserId, boolean tutor);
    }

    public void notifyStudentsDataSetChanged() {
        mStudentsListViewArrayAdapter.notifyDataSetChanged();
    }

    public void notifyTutorsDataSetChanged() {
        mTutorsListViewArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Array adapter to be used with the Students ListView
     */
    private class StudentsArrayAdapter extends ArrayAdapter<Student> {
        HashMap<Student, Integer> mIdMap;

        public StudentsArrayAdapter(Context context,
                                    List<Student> objects) {
            super(context, 0, objects);
            mIdMap = new HashMap<>();

            for (int i = 0; i < objects.size(); i++) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Student currStudentMember = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.view_group_members_student_layout, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.student_firstName))
                    .setText(currStudentMember.getFirstName());
            ((TextView) convertView.findViewById(R.id.student_lastName))
                    .setText(currStudentMember.getLastName());

            // Only set up long click listeners if the current viewer is the owner of the group
            if (((ViewGroupActivity) getActivity()).getmUserId()
                    .equals(((ViewGroupActivity) getActivity()).getmViewerUserId())) {

                // Do not set up long click listener on the owner of the group
                if (!currStudentMember.getUserId()
                        .equals(((ViewGroupActivity) getActivity()).getmViewerUserId())) {

                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final Dialog removeTutorDialog = new Dialog(getActivity());
                            removeTutorDialog.title("Remove Tutor?")
                                    .positiveAction("Yes")
                                    .negativeAction("No")
                                    .cancelable(true)
                                    .show();
                            removeTutorDialog.positiveActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onMemberRemove(currStudentMember.getUserId(), false);
                                    removeTutorDialog.dismiss();
                                }
                            });

                            removeTutorDialog.negativeActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeTutorDialog.cancel();
                                }
                            });

                            return true;
                        }
                    });
                }
            }

            return convertView;
        }
    }

    /**
     * Array adapter to be used with the Tutors ListView
     * Incorporates Thumbs Up/Down functionality
     */
    private class TutorsArrayAdapter extends ArrayAdapter<Student> {
        private HashMap<Student, Integer> mIdMap;
        private final int mThumbsUpButtonGreyId = R.drawable.ic_thumb_up_grey_500_18dp;
        private final int mThumbsUpButtonBlueId = R.drawable.ic_thumb_up_light_blue_500_18dp;
        private final int mThumbsDownButtonGreyId = R.drawable.ic_thumb_down_grey_500_18dp;
        private final int mThumbsDownButtonRedId = R.drawable.ic_thumb_down_red_400_18dp;

        public TutorsArrayAdapter(Context context, List<Student> objects) {
            super(context, 0, objects);
            mIdMap = new HashMap<>();

            for (int i = 0; i < objects.size(); i++) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            return mIdMap.get(getItem(position));
        }

        /**
         * Sets up the view for the ListView
         *
         * @param position    The position of the current item in the ListView
         * @param convertView The view of the current item in the ListView
         * @param parent      The ListView
         * @return convertView after setup
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Student currStudentMember = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.view_group_members_tutors_layout, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.tutor_firstName))
                    .setText(currStudentMember.getFirstName());
            ((TextView) convertView.findViewById(R.id.tutor_lastName))
                    .setText(currStudentMember.getLastName());

            // Only set up long click listeners if the current viewer is the owner of the group
            // Unnecessary to do second if check because current owner cannot be a tutor
            if (((ViewGroupActivity) getActivity()).getmUserId()
                    .equals(((ViewGroupActivity) getActivity()).getmViewerUserId())) {

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Dialog removeTutorDialog = new Dialog(getActivity());
                        removeTutorDialog.title("Remove Tutor?")
                                .positiveAction("Yes")
                                .negativeAction("No")
                                .cancelable(true)
                                .show();
                        removeTutorDialog.positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onMemberRemove(currStudentMember.getUserId(), true);
                                removeTutorDialog.dismiss();
                            }
                        });

                        removeTutorDialog.negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeTutorDialog.cancel();
                            }
                        });

                        return true;
                    }
                });

            }

            ImageButton mThumbsUpButton =
                    ((ImageButton) convertView.findViewById(R.id.view_group_members_thumbs_up));
            ImageButton mThumbsDownButton =
                    ((ImageButton) convertView.findViewById(R.id.view_group_members_thumbs_down));
            setUpThumbsUpButton(mThumbsUpButton, mThumbsDownButton, currStudentMember.getUserId());
            setUpThumbsDownButton(mThumbsUpButton, mThumbsDownButton,
                    currStudentMember.getUserId());

            return convertView;
        }

        private void setUpThumbsUpButton(final ImageButton mThumbsUpButton,
                                         final ImageButton mThumbsDownButton, final String currUserId) {
            if (!(((ViewGroupActivity) getActivity()).isCurrViewerIsInGroup())) {
                mThumbsUpButton.setClickable(false);
                mThumbsUpButton.setVisibility(View.INVISIBLE);
                return;
            }

            mThumbsUpButton.setTag(false);
            mThumbsUpButton.setImageResource(mThumbsUpButtonGreyId);
            mThumbsUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thumbs Up Button is not clicked
                    if ((Boolean) mThumbsUpButton.getTag() == false) {
                        Log.v("ThumbsUp", "SettingLightBlue");
                        mThumbsUpButton
                                .setImageResource(mThumbsUpButtonBlueId);
                        mThumbsUpButton.setTag(true);
                        onTutorRating(true, currUserId);
                        // Case 2 - Thumbs down button is currently clicked
                        if ((Boolean) mThumbsDownButton.getTag() == true) {
                            mThumbsDownButton
                                    .setImageResource(mThumbsDownButtonGreyId);
                            mThumbsDownButton.setTag(false);
                            onTutorRating(true, currUserId);
                        }
                    }
                    // Thumbs Up Button is currently clicked
                    else {
                        mThumbsUpButton
                                .setImageResource(mThumbsUpButtonGreyId);
                        mThumbsUpButton.setTag(false);
                        onTutorRating(false, currUserId);
                    }

                }
            });
        }

        private void setUpThumbsDownButton(final ImageButton mThumbsUpButton,
                                           final ImageButton mThumbsDownButton, final String currUserId) {

            if (!(((ViewGroupActivity) getActivity()).isCurrViewerIsInGroup())) {
                mThumbsDownButton.setClickable(false);
                mThumbsDownButton.setVisibility(View.INVISIBLE);
                return;
            }

            mThumbsDownButton.setTag(false);
            mThumbsDownButton.setImageResource(mThumbsDownButtonGreyId);
            mThumbsDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thumbs Down Button is not clicked
                    if ((Boolean) mThumbsDownButton.getTag() == false) {
                        mThumbsDownButton
                                .setImageResource(mThumbsDownButtonRedId);
                        mThumbsDownButton.setTag(true);
                        onTutorRating(false, currUserId);
                        // Case 2 - Thumbs Up Button is currently clicked
                        if ((Boolean) mThumbsUpButton.getTag() == true) {
                            mThumbsUpButton
                                    .setImageResource(mThumbsUpButtonGreyId);
                            mThumbsUpButton.setTag(false);
                            onTutorRating(false, currUserId);
                        }
                    }
                    // Thumbs Down Button is currently clicked
                    else {
                        mThumbsDownButton
                                .setImageResource(mThumbsDownButtonGreyId);
                        mThumbsDownButton.setTag(false);
                        onTutorRating(true, currUserId);
                    }
                }
            });
        }

    }

}
