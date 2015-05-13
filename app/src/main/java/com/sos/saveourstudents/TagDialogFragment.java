package com.sos.saveourstudents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.app.DialogFragment;

public class TagDialogFragment extends DialogFragment {
        int mNum;


        static TagDialogFragment newInstance(int num) {
            TagDialogFragment f = new TagDialogFragment();

            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments().getInt("num");


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            /*
            View v = inflater.inflate(R.layout.fragment_dialog, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("Dialog #" + mNum + ": using style "
                    + getNameForNum(mNum));

            // Watch for button clicks.
            Button button = (Button)v.findViewById(R.id.show);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // When button is clicked, call up to owning activity.
                    ((FragmentDialog)getActivity()).showDialog();
                }
            });


            */
            return null;
        }
    }