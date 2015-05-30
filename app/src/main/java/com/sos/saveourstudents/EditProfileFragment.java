package com.sos.saveourstudents;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDoneButtonListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private static final String ARG_CURRSTUDENT = "student";
    private final String mUserURL =
            "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/updateProfile?";
    private final String userIdTag_sharedPreferences = "user_id";

    private Student mCurrStudent;
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

    private ImageView mProfilePicture;
    private String mProfilePictureUrl;

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
        args.putParcelable(ARG_CURRSTUDENT, currStudent);
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
            mCurrStudent = getArguments().getParcelable(ARG_CURRSTUDENT);
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
    public void onDoneButton() {
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

        initializeProfileImageEdit();
        doneButton = (FloatingActionButton) getActivity().findViewById(R.id.profile_doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButton();
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

        mEditFirstNameInput.setText(mCurrStudent.getFirstName());
        mEditLastNameInput.setText(mCurrStudent.getLastName());
        mEditSchoolInput.setText(mCurrStudent.getSchool());
        mEditMajorInput.setText(mCurrStudent.getMajor());
        mEditDescriptionInput.setText(mCurrStudent.getDescription());

        mProfilePicture = (ImageView) getActivity().findViewById(R.id.profile_editImage);
        mProfilePicture.setImageDrawable(mCurrStudent.getProfilePicture().getDrawable());
        mProfilePictureUrl = mCurrStudent.getProfilePictureUrl();

    }

    /**
     * To be used after onDoneButton() call in ProfileActivity
     * @return Newly updated student
     */

    public Student updateStudent() {

        mCurrStudent.setFirstName(mEditFirstNameInput.getText().toString());
        mCurrStudent.setLastName(mEditLastNameInput.getText().toString());
        mCurrStudent.setSchool(mEditSchoolInput.getText().toString());
        mCurrStudent.setMajor(mEditMajorInput.getText().toString());
        mCurrStudent.setDescription(mEditDescriptionInput.getText().toString());
        mCurrStudent.setProfilePicture(mProfilePicture);
        mCurrStudent.setProfilePictureUrl(mProfilePictureUrl);

        String newFirstName = "";
        String newLastName = "";
        String newSchool = "";
        String newMajor = "";
        String newDescription = "";

        try {
            newFirstName = java.net.URLEncoder.encode(mCurrStudent.getFirstName(), "utf-8");
            newLastName = java.net.URLEncoder.encode(mCurrStudent.getLastName(), "utf-8");
            newSchool = java.net.URLEncoder.encode(mCurrStudent.getSchool(), "utf-8");
            newMajor = java.net.URLEncoder.encode(mCurrStudent.getMajor(), "utf-8");
            newDescription = java.net.URLEncoder.encode(mCurrStudent.getDescription(), "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String updateProfileUrl = mUserURL + "userId=" +
                getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        .getString(userIdTag_sharedPreferences, "")
                + "&firstName=" + newFirstName + "&lastName=" + newLastName
                + "&school=" + newSchool + "&major=" + newMajor
                + "&description=" + newDescription + "&image=" + mProfilePictureUrl;

        JsonObjectRequest studentRequest = new JsonObjectRequest(Request.Method.GET,
                updateProfileUrl, (JSONObject) null, new EditProfileResponseListener(),
                new EditProfileErrorListener());

        Singleton.getInstance().addToRequestQueue(studentRequest);

        return mCurrStudent;
    }

    /**
     * Initializes EditText onEditorActionListeners and onClickListeners
     * @param editTextWrapper The wrapped EditText (i.e. the com.rey...EditText)
     * @param editTextInput The input (et_inputId)
     */
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

    /**
     * Initializes OnClickListener for the editable profile picture
     */
    private void initializeProfileImageEdit() {
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("Edit Image")
                        .content("Enter the URL for your new profile image")
                        .input("Image URL", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                new DownloadImageTask(mProfilePicture)
                                        .execute(charSequence.toString());
                            }
                        }).show();
            }
        });
    }


    /**
     * AsyncTask for downloading the image from the provided URL
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ImageView originalImage;
        String passedUrl;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            originalImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            passedUrl = urls[0];
            String urldisplay = new String();
            if(!(passedUrl.contains("http://") || passedUrl.contains("https://"))) {
                passedUrl = "http://" + passedUrl;
                urldisplay = passedUrl;
            }

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
            if (result != null) {
                bmImage.setImageBitmap(result);
                mProfilePictureUrl = passedUrl;
            }
            else {
                bmImage = originalImage;
                Toast.makeText(getActivity(), "Invalid Image URL!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class EditProfileResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
        }
    }
    class EditProfileErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }



}