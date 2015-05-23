package com.sos.saveourstudents;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewGroupLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewGroupLocationFragment extends android.support.v4.app.Fragment {
    private static final String ARG_COORDINATES = "paramCoordinates";

    private LatLng mGroupCoordinates;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewGroupLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewGroupLocationFragment newInstance(LatLng param1) {
        ViewGroupLocationFragment fragment = new ViewGroupLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COORDINATES, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewGroupLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupCoordinates = getArguments().getParcelable(ARG_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_group_location, container, false);
    }

}
