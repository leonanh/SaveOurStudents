package com.sos.saveourstudents;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            currStudent = (Student) (getArguments().getParcelable(ARG_PARAM1));
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

        ((TextView) getActivity().findViewById(R.id.profile_firstName))
                .setText(currStudent.getFirstName());

        ((TextView) getActivity().findViewById(R.id.profile_lastName))
                .setText(currStudent.getLastName());

        // Begin inserting data into the About Me of the Student
        RecyclerView aboutMeContents = (RecyclerView) getActivity().
                findViewById(R.id.profile_aboutMeContents);

        // About Me will ALWAYS have a School, Major, and Description
        aboutMeContents.setHasFixedSize(true);

        // LinearLayoutManager will include line separations between each About Me descriptor
        LinearLayoutManager aboutMeContentsLLM = new LinearLayoutManager(getActivity(),
                android.support.v7.widget.LinearLayoutManager.VERTICAL, false);
        aboutMeContents.setLayoutManager(aboutMeContentsLLM);
        aboutMeContents.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        aboutMeContents.setAdapter(new RVAdapter(initializeData()));

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
     * Helper method to grab data from the current student
     * @return New ArrayList of StudentInformations which contain a descriptor and description
     */
    private ArrayList<StudentInformation> initializeData() {
        ArrayList<StudentInformation> studentInfoList = new ArrayList<>();
        studentInfoList.add(new StudentInformation(getResources()
                .getString(R.string.profile_school), currStudent.getSchool()));
        studentInfoList.add(new StudentInformation(getResources()
                .getString(R.string.profile_major), currStudent.getMajor()));
        studentInfoList.add(new StudentInformation(getResources()
                .getString(R.string.profile_description), currStudent.getDescription()));

        return studentInfoList;
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
        public void onEditButton();
    }

    /**
     * Helper class for the RecyclerView that contains a descriptor/description
     * (e.g. School, <name of school>)
     */
    class StudentInformation {
        String descriptor;
        String description;

        StudentInformation(String descriptor, String description) {
            this.descriptor = descriptor;
            this.description = description;
        }
    }

    /**
     * RecyclerView adapter that will set the TextView information of each StudentViewHolder
     */
    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.StudentViewHolder> {

        private ArrayList<StudentInformation> studentInfo;

        public RVAdapter(ArrayList<StudentInformation> studentInfo) {
            this.studentInfo = studentInfo;
        }

        @Override
        public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.aboutme_layout, parent, false);
            StudentViewHolder svh = new StudentViewHolder(v);
            return svh;
        }

        @Override
        public void onBindViewHolder(StudentViewHolder holder, int position) {
            holder.descriptor.setText(studentInfo.get(position).descriptor);
            holder.description.setText(studentInfo.get(position).description);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public int getItemCount() {
            return studentInfo.size();
        }

        public class StudentViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout viewHolderLayout;
            TextView descriptor;
            TextView description;

            public StudentViewHolder(View itemView) {
                super(itemView);
                viewHolderLayout = ((RelativeLayout) itemView
                        .findViewById(R.id.profile_aboutMe_layout));
                descriptor = ((TextView) itemView.findViewById(R.id.profile_aboutMe_descriptor));
                description = ((TextView) itemView.findViewById(R.id.profile_aboutMe_description));

            }
        }
    }

}
