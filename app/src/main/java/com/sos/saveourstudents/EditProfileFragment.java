package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDoneButtonListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "student";

    private Student currStudent;
    private OnDoneButtonListener mListener;

    private EditText mEditFirstName;
    private android.widget.EditText mEditFirstNameInput;
    private EditText mEditLastName;
    private android.widget.EditText mEditLastNameInput;
    private EditText mEditSchool;
    private android.widget.EditText mEditSchoolInput;
    private EditText mEditMajor;
    private android.widget.EditText mEditMajorInput;
    private EditText mEditDescription;
    private android.widget.EditText mEditDescriptionInput;

    private FloatingActionButton doneButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currStudent Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(Student currStudent) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, currStudent);
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currStudent = getArguments().getParcelable(ARG_PARAM1);
        } else {
            Log.e("EditProfile Error", "Must be given a student argument!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDoneButton();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDoneButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeMemberVariables();

        initializeEditTextWithEnterExit(mEditFirstName, mEditFirstNameInput);
        initializeEditTextWithEnterExit(mEditLastName, mEditLastNameInput);
        initializeEditTextWithEnterExit(mEditSchool, mEditSchoolInput);
        initializeEditTextWithEnterExit(mEditMajor, mEditMajorInput);

        doneButton = (FloatingActionButton) getActivity().findViewById(R.id.profile_doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDoneButton();
                }
            }
        });
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
    public interface OnDoneButtonListener {
        // TODO: Update argument type and name
        public void onDoneButton();
    }

    private void initializeMemberVariables() {
        mEditFirstName = (EditText) getActivity().findViewById(R.id.profile_editFirstName);
        mEditFirstNameInput = (android.widget.EditText) getActivity().findViewById(R.id.profile_editFirstName_inputId);
        mEditLastName = (EditText) getActivity().findViewById(R.id.profile_editLastName);
        mEditLastNameInput = (android.widget.EditText) getActivity().findViewById(R.id.profile_editLastName_inputId);
        mEditSchool = (EditText) getActivity().findViewById(R.id.profile_editSchool);
        mEditSchoolInput = (android.widget.EditText) getActivity().findViewById(R.id.profile_editSchool_inputId);
        mEditMajor = (EditText) getActivity().findViewById(R.id.profile_editMajor);
        mEditMajorInput = (android.widget.EditText) getActivity().findViewById(R.id.profile_editMajor_inputId);
        mEditDescription = (EditText) getActivity().findViewById(R.id.profile_editDescription);
        mEditDescriptionInput = (android.widget.EditText) getActivity().findViewById(R.id.profile_editDescription_inputId);

        mEditFirstNameInput.setText(currStudent.getFirstName());
        mEditLastNameInput.setText(currStudent.getLastName());
        mEditSchoolInput.setText(currStudent.getSchool());
        mEditMajorInput.setText(currStudent.getMajor());
        mEditDescriptionInput.setText(currStudent.getDescription());
    }

    /**
     * To be used after onDoneButton() call in ProfileActivity
     *
     * @return Newly updated student
     */

    public Student updateStudent() {
        currStudent.setFirstName(mEditFirstNameInput.getText().toString());
        currStudent.setLastName(mEditLastNameInput.getText().toString());
        currStudent.setSchool(mEditSchoolInput.getText().toString());
        currStudent.setMajor(mEditMajorInput.getText().toString());
        currStudent.setDescription(mEditDescriptionInput.getText().toString());

        return currStudent;
    }

    private void initializeEditTextWithEnterExit(final EditText editTextWrapper,
                                                 final android.widget.EditText editTextInput) {
        editTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextWrapper.setCursorVisible(true);
                editTextInput.requestFocus();
                getActivity().findViewById(R.id.profile_editProfile_overall).clearFocus();
            }
        });
        editTextWrapper.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextWrapper.getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    editTextWrapper.setCursorVisible(false);
                    return true;
                } else if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
                    InputMethodManager in = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextWrapper.getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    editTextWrapper.setCursorVisible(false);
                }
                return false;
            }
        });
    }




}
