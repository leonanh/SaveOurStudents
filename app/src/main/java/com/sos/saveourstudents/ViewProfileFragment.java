package com.sos.saveourstudents;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.FloatingActionButton;

import java.io.InputStream;


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
    private static final String ARG_ISCURRENTUSER = "isCurrentUser";
    private FloatingActionButton mEditButton;

    private Student mCurrStudent;
    private boolean mIsCurrentUser;

    private OnEditButtonListener mListener;
    private ImageView mProfileImage;
    private ImageView mCoverImageView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currStudent The current logged in student
     * @return A new instance of fragment ViewProfileFragment.
     */
    public static ViewProfileFragment newInstance(Student currStudent, boolean isCurrentUser) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, currStudent); // ARG_PARAM1 key now holds current Student
        args.putBoolean(ARG_ISCURRENTUSER, isCurrentUser);
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
            mCurrStudent = getArguments().getParcelable(ARG_PARAM1);
            mIsCurrentUser = getArguments().getBoolean(ARG_ISCURRENTUSER);
        } else {
            Log.e("ViewProfile", "Error during transaction!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mProfileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        mCoverImageView = (ImageView) rootView.findViewById(R.id.cover_image);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateCoverImage();
        updateCurrentStudentView();

        LinearLayout aboutMeContents = (LinearLayout) getActivity()
                .findViewById(R.id.profile_aboutMeContents);
        aboutMeContents.setDividerDrawable(getActivity().getResources()
                .getDrawable(R.drawable.abc_list_divider_mtrl_alpha));
        aboutMeContents.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);
        mEditButton = (FloatingActionButton) getActivity().findViewById(R.id.profile_editButton);
        if (!mIsCurrentUser) {
            mEditButton.setVisibility(View.INVISIBLE);
            mEditButton.setClickable(false);
        } else {
            // Set the OnClickListener for the edit floating action button
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onEditButton();
                    }
                }
            });

        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    /**
     * Update the View with the student's current values
     */

    private void updateCurrentStudentView() {
        ((TextView) getActivity().findViewById(R.id.profile_firstName))
                .setText(mCurrStudent.getFirstName());
        ((TextView) getActivity().findViewById(R.id.profile_lastName))
                .setText(mCurrStudent.getLastName());
        ((TextView) getActivity().findViewById(R.id.profile_myRating))
                .setText(((Integer) mCurrStudent.getRating()).toString());
        ((TextView) getActivity().findViewById(R.id.profile_mySchool))
                .setText(mCurrStudent.getSchool());
        ((TextView) getActivity().findViewById(R.id.profile_myMajor))
                .setText(mCurrStudent.getMajor());
        ((TextView) getActivity().findViewById(R.id.profile_myDescription))
                .setText(mCurrStudent.getDescription());




            //TODO
        if (mCurrStudent.getProfilePictureUrl().equals(null) ||
                mCurrStudent.getProfilePictureUrl().isEmpty()) {
            mProfileImage.setImageResource(R.drawable.defaultprofile);
            mCurrStudent.setProfilePicture(mProfileImage);
        } else {
            new DownloadImageTask(mProfileImage)
                    .execute(mCurrStudent.getProfilePictureUrl());
            mCurrStudent.setProfilePicture(mProfileImage);
        }
    }

    private void updateCoverImage(){
        if(mIsCurrentUser)
            mCoverImageView.setImageResource(getActivity().getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    .getInt("cover_photo", R.drawable.materialwallpaperdefault));

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
    public interface OnEditButtonListener {
        void onEditButton();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                mIcon11 = null;
                Log.e("Profile Activity", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) bmImage.setImageBitmap(result);
            else {
                bmImage.setImageResource(R.drawable.defaultprofile);
            }
        }
    }

}