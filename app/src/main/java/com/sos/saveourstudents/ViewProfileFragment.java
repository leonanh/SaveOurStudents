package com.sos.saveourstudents;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFloat;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;


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
    private Button editButton;

    private Student currStudent;

    private OnEditButtonListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currStudent Parameter 1.
     * @return A new instance of fragment ViewProfileFragment.
     */
    public static ViewProfileFragment newInstance(Student currStudent) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, currStudent);
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
            currStudent = (Student) (getArguments().getParcelable(ARG_PARAM1));
        }

        // TODO: Grab Student information from database
        else currStudent = new Student("Brady", "Shi", 0, "UCSD", "Computer Engineering", "Coffee Addict",
                null);


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
        MaterialLargeImageCard materialCard = new MaterialLargeImageCard(getActivity());
        materialCard.setTextOverImage(currStudent.getFirstName() + " " + currStudent.getLastName());
        materialCard.build();

        TopColoredCard aboutMeCard = TopColoredCard.with(getActivity())
                .setColorResId(R.color.accent)
                .setSubTitleOverColor("")
                .setTitleOverColor(R.string.profile_aboutMe)
                .setupSubLayoutId(R.layout.aboutme_layout)
                .setupInnerElements(new TopColoredCard.OnSetupInnerElements() {
                    @Override
                    public void setupInnerViewElementsSecondHalf(View view) {
                        ((TextView) view.findViewById(R.id.profile_school)).setText("School:");
                        ((TextView) view.findViewById(R.id.profile_major)).setText("Major:");
                        ((TextView) view.findViewById(R.id.profile_description)).setText("Description:");
                        TextView schoolTextView = (TextView) view.findViewById(R.id.profile_mySchool);
                        schoolTextView.setText(currStudent.getSchool());

                        ((TextView) view.findViewById(R.id.profile_myMajor)).setText(currStudent.getMajor());
                        ((TextView) view.findViewById(R.id.profile_myDescription)).setText(currStudent.getDescription());

                    }
                })
                .build();
        CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.profile_header);
        cardView.setCard(materialCard);

        CardViewNative aboutMeCardView = (CardViewNative) getActivity().findViewById(R.id.profile_aboutMe);
        aboutMeCardView.setCard(aboutMeCard);

        editButton = (ButtonFloat) getActivity().findViewById(R.id.profile_editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onEditButton();
                }
            }
        });


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditButtonListener) activity;
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
        public void onEditButton();
    }

}
