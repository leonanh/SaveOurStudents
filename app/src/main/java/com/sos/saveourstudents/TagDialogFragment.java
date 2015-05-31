package com.sos.saveourstudents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by deamon on 5/13/15.
 */
@SuppressLint("ValidFragment")
public class TagDialogFragment extends DialogFragment implements View.OnClickListener {

    private NoticeDialogListener mListener;
    final int SEARCH_FILTERS = 0;
    final int QUESTION_FILTERS = 1;
    int dialogType;

    ViewGroup flowLayout, flowLayout2;
    JSONArray popularTags = null;
    Context mContext;
    LayoutInflater mInflater;
    EditText editText;
    ImageView addButton;

    Set<String> activeFilters;



    public interface NoticeDialogListener {
        void passTagList(DialogFragment dialog, Set<String> activeFilters);
    }


    static TagDialogFragment newInstance(int dialogType, ArrayList<String> tagList) {
        TagDialogFragment fragment = new TagDialogFragment();

        System.out.println("Input tagList:"+ tagList);
        System.out.println("Input dialogType:"+ dialogType);
        Bundle args = new Bundle();
        args.putInt("dialog_type", dialogType);
        args.putStringArrayList("list", tagList);
        fragment.setArguments(args);

        return fragment;
    }

    public TagDialogFragment() {

    }


    /*
    public TagDialogFragment(Context context, int dialogType) {
        mContext = context;
        this.dialogType = dialogType;


        //If calling this dialog from mainActivity
        if(dialogType == SEARCH_FILTERS){
            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);


            if (!sharedPref.contains("filter_list")) {
                SharedPreferences.Editor editor = sharedPref.edit();
                activeFilters = new HashSet();
                editor.putStringSet("filter_list", activeFilters);
                editor.commit();
            }
            else{
                activeFilters = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

            }

        }//If calling this dialog from createQuestion
        else if(dialogType == QUESTION_FILTERS){

            activeFilters = new HashSet();

        }

    }
*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 1);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
/*
        try {
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement NoticeDialogListener");
        }*/
        //((NoticeDialogListener)getTargetFragment()).onDialogPositiveClick(this);
    }


    @Override
    public void onResume() {

        DisplayMetrics metrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Set dimensions of Dialog box
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();


        //System.out.println(dispMetrics.widthPixels +" , "+dispMetrics.heightPixels);
        params.width = (int) (metrics.widthPixels * (.8));//ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (metrics.heightPixels * .8);
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);



        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);

        getDialog().getWindow().addFlags(2);
        getDialog().getWindow().setDimAmount(0.5f);
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_dialog_layout, container, false);
        mInflater = inflater;
        mContext = getActivity();

        flowLayout = (ViewGroup) view.findViewById(R.id.flow_container);
        flowLayout2 = (ViewGroup) view.findViewById(R.id.flow_container_active_tags);

        activeFilters = new HashSet();

        if (getArguments() != null) {
            dialogType = getArguments().getInt("dialog_type");
            activeFilters.addAll(getArguments().getStringArrayList("list"));

        }else{
            Toast.makeText(mContext, "args empty in tag dialog", Toast.LENGTH_SHORT).show();
        }

        System.out.println("activeFilters:"+ activeFilters);
        System.out.println("dialogType:"+ dialogType);

        editText = (EditText) view.findViewById(R.id.edit_text);
        addButton = (ImageView) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
        //System.out.println("editText.getThreshold(): " + editText.getThreshold());


        /*
        if(dialogType == SEARCH_FILTERS){
            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);


            if (!sharedPref.contains("filter_list")) {
                SharedPreferences.Editor editor = sharedPref.edit();
                activeFilters = new HashSet();
                editor.putStringSet("filter_list", activeFilters);
                editor.commit();
            }
            else{
                activeFilters = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

            }

        }//If calling this dialog from createQuestion
        else if(dialogType == QUESTION_FILTERS){

            activeFilters = new HashSet();

        }

*/






        updateActiveTagsUI();
        getTagData();

        return view;
    }


    private void getTagData() {

        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/getTags";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            System.out.println("Response: "+response.toString());
                            JSONObject result = new JSONObject(response.toString());
                            if (!result.getString("success").equalsIgnoreCase("1")) {
                                //Error getting data
                                return;
                            }
                            if (result.getString("expectResults").equalsIgnoreCase("0")) {
                                //empty list
                                popularTags = result.getJSONObject("result").getJSONArray("myArrayList");
                                return;
                            } else {

                                popularTags = result.getJSONObject("result").getJSONArray("myArrayList");
                                ArrayList<String> autoCompleteArray = new ArrayList<>();
                                for(int a = 0; a < popularTags.length(); a++){
                                    String text = popularTags.getJSONObject(a).getJSONObject("map").getString("tag");

                                    if(!text.equalsIgnoreCase("")) {
                                        autoCompleteArray.add(text);
                                    }

                                }


                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.my_simple_dropdown_item_1line, autoCompleteArray);
                                editText.setAdapter(adapter);

                                updatePopularTagsUI();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("Error: " + error.toString());

            }
        });

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }


    private void updateActiveTagsUI(){


        TextView activeTagView = null;
        //Set activeFilters = sharedPref.getStringSet("filter_list", null);
        Object[] activeFiltersArray = activeFilters.toArray();

        flowLayout2.removeAllViews();



        for(int a = 0; a < activeFiltersArray.length; a++){
            activeTagView = (TextView) mInflater.inflate(R.layout.tag_active_item_layout, null, false);
            activeTagView.setText(activeFiltersArray[a].toString());
            activeTagView.setOnClickListener(this);
            flowLayout2.addView(activeTagView);
        }
        //System.out.println("Child count: "+flowLayout2.getChildCount());

    }

    private void updatePopularTagsUI(){
        flowLayout.removeAllViews();

        View tagView = null;

        if(popularTags != null && popularTags.length() > 0) {
            try {

                for (int a = 0; a < popularTags.length(); a++) {


                    tagView = mInflater.inflate(R.layout.tag_item_layout, null, false);
                    tagView.findViewById(R.id.the_linear).setOnClickListener(this);
                    TextView tagText = (TextView) tagView.findViewById(R.id.tag_text);
                    String text = popularTags.getJSONObject(a).getJSONObject("map").getString("tag");
                    tagText.setText(text);

                    if (activeFilters.contains(text))
                        tagView.setSelected(true);

                    //Add tag item view to flowlayout
                    if(!text.equalsIgnoreCase(""))
                        flowLayout.addView(tagView);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.the_linear) {

            TextView text = (TextView) v.findViewById(R.id.tag_text);

            //System.out.println("v tag is: " + text.getText());
            if(v.isSelected()){
                v.setSelected(false);
                removeTagFromList(text.getText().toString());
            }else{
                v.setSelected(true);
                addTagToActiveList(text.getText().toString());
            }


        } else if (v.getId() == R.id.add_button) {
            //System.out.println("v is selected: " + v.isSelected());
            //Add tag to selected list
            addTagToActiveList(editText.getText().toString());
            editText.setText("");
        }
        else if(v.getId() == R.id.active_tag_text){
            TextView temp = (TextView) v;
            removeTagFromList(temp.getText().toString());
        }

    }


    private void addTagToActiveList(String tag) {

        if (tag.equalsIgnoreCase("") || tag.toString() == null)
            return;

        if(dialogType == SEARCH_FILTERS){

            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            Set<String> filterList;
            //user has no filter list, create one and add tag
            if (!sharedPref.contains("filter_list")) {
                filterList = new HashSet();
                filterList.add(tag);
                editor.putStringSet("filter_list", filterList);
                editor.commit();
            } else {  //User has a filter list, does this tag exist in it?
                //Set filterList = sharedPref.getStringSet("filter_list", null);
                filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));
                if (!filterList.contains(tag)) {
                    filterList.add(tag);
                    editor.putStringSet("filter_list", filterList);
                    editor.commit();

                } else {
                    //Tag exists, ignore it?
                }

            }
            activeFilters = filterList;
            System.out.println(filterList.toString());

        }
        else if(dialogType == QUESTION_FILTERS){

            //Dont update sharedPref, just update local data
            activeFilters.add(tag);

        }



        updateActiveTagsUI();
        updatePopularTagsUI();

    }

    private void removeTagFromList(String tag) {

        if (tag.equalsIgnoreCase("") || tag.toString() == null)
            return;


        if(dialogType == SEARCH_FILTERS){

            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();


            Set<String> filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

            if (filterList.contains(tag)) {
                filterList.remove(tag);
                editor.putStringSet("filter_list", filterList);
                editor.commit();
                activeFilters = filterList;
            }


        }
        else if(dialogType == QUESTION_FILTERS){

            //Dont update sharedPref, just update local data
            activeFilters.remove(tag);

        }


        System.out.println(activeFilters.toString());

        updateActiveTagsUI();
        updatePopularTagsUI();
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        if(dialogType == QUESTION_FILTERS){
            if(getTargetFragment() != null && getTargetFragment().getActivity() != null)
                ((CreateQuestionFragment) getTargetFragment()).passTagList(activeFilters);
        }
        else{
            if(getActivity() != null)
                ((MainActivity) getActivity()).updateFragments();
        }


    }



}