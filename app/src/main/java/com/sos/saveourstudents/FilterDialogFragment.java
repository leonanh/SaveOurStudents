package com.sos.saveourstudents;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by deamon on 5/13/15.
 */
@SuppressLint("ValidFragment")
public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {

    DisplayMetrics dispMetrics;
    ViewGroup flowLayout, flowLayout2;
    JSONArray popularTags = null;
    Context mContext;
    LayoutInflater mInflater;
    EditText editText;
    ImageView addButton;
    SharedPreferences sharedPref;


    public FilterDialogFragment(Context context, DisplayMetrics metrics, SharedPreferences sharedPref) {
        mContext = context;
        this.sharedPref = sharedPref;
        dispMetrics = metrics;

        //System.out.println(sharedPref.getStringSet("filter_list", null).toString());

        if (!sharedPref.contains("filter_list")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            Set filterList = new HashSet();
            editor.putStringSet("filter_list", filterList);
            editor.commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, 0);



    }

    @Override
    public void onResume() {

        //Set dimensions of Dialog box
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();


        //System.out.println(dispMetrics.widthPixels +" , "+dispMetrics.heightPixels);
        params.width = (int) (dispMetrics.widthPixels * (.8));//ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (dispMetrics.heightPixels * .7);
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
        flowLayout = (ViewGroup) view.findViewById(R.id.flow_container);
        flowLayout2 = (ViewGroup) view.findViewById(R.id.flow_container_active_tags);


        editText = (EditText) view.findViewById(R.id.edit_text);
        addButton = (ImageView) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
        //System.out.println("editText.getThreshold(): " + editText.getThreshold());

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

                            JSONObject result = new JSONObject(response.toString());
                            if (!result.getString("success").equalsIgnoreCase("1")) {
                                //Error getting data
                                return;
                            }
                            if (result.getString("expectResults").equalsIgnoreCase("0")) {
                                //No results to show
                                return;
                            } else {

                                //JSONObject temp = result.getJSONObject("result");
                                //System.out.println("temp: "+temp.getJSONArray("myArrayList"));

                                popularTags = result.getJSONObject("result").getJSONArray("myArrayList");
                            }


                            //System.out.println("popularTags " + popularTags.toString());

                            showTags();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            updateActiveTagsUI();
                        }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("Error: " + error.toString());


                updateActiveTagsUI();
            }
        });

        // Access the RequestQueue through your singleton class.
        Singleton.getInstance().addToRequestQueue(jsObjRequest);
    }


    private void updateActiveTagsUI(){


        TextView activeTagView = null;
        Set activeFilters = sharedPref.getStringSet("filter_list", null);
        Object[] activeFiltersArray = activeFilters.toArray();

        flowLayout2.removeAllViews();
        //if(flowLayout2.getChildCount() > 1)
        //flowLayout2.removeViews(1, flowLayout2.getChildCount()-1 );
        //else


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
        Set activeFilters = sharedPref.getStringSet("filter_list", null);


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
                    flowLayout.addView(tagView);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(activeFilters != null){
            //for (int a = 0; a < activeFilters.size(); a++) {
            updateActiveTagsUI();
            //}

        }

    }


    private void showTags() {


        updateActiveTagsUI();


        Set activeFilters = sharedPref.getStringSet("filter_list", null);
        String[] tagsArray = new String[popularTags.length()];
        View tagView = null;
        try {

            for (int a = 0; a < popularTags.length(); a++) {

                //System.out.println("want to display tag: " + popularTags.getJSONObject(a));
                tagView = mInflater.inflate(R.layout.tag_item_layout, null, false);
                tagView.findViewById(R.id.the_linear).setOnClickListener(this);
                TextView tagText = (TextView) tagView.findViewById(R.id.tag_text);
                String text = popularTags.getJSONObject(a).getJSONObject("map").getString("tag");
                tagText.setText(text);

                if(activeFilters.contains(text))
                    tagView.setSelected(true);

                //Add tag item view to flowlayout
                flowLayout.addView(tagView);


                //Add to autocomplete list
                tagsArray[a] = text;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.my_simple_dropdown_item_1line, tagsArray);

        editText.setAdapter(adapter);
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
                addTagToList(text.getText().toString());
            }


        } else if (v.getId() == R.id.add_button) {
            //System.out.println("v is selected: " + v.isSelected());
            //Add tag to selected list
            addTagToList(editText.getText().toString());
        }
        else if(v.getId() == R.id.active_tag_text){
            TextView temp = (TextView) v;
            removeTagFromList(temp.getText().toString());
        }


    }


    private void addTagToList(String tag) {
/*
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        */
        SharedPreferences.Editor editor = sharedPref.edit();

        if (tag.equalsIgnoreCase("") || tag.toString() == null)
            return;

        //user has no filter list, create one and add tag
        if (!sharedPref.contains("filter_list")) {
            Set filterList = new HashSet();
            filterList.add(tag);
            editor.putStringSet("filter_list", filterList);
            editor.commit();
        } else {  //User has a filter list, does this tag exist in it?
            //Set filterList = sharedPref.getStringSet("filter_list", null);
            Set<String> filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));
            if (!filterList.contains(tag)) {
                filterList.add(tag);

                editor.putStringSet("filter_list", filterList);
                editor.commit();
            } else {
                //Tag exists, ignore it?
            }

        }

        //System.out.println(sharedPref.getStringSet("filter_list", new HashSet<String>()).toString());

        updateActiveTagsUI();


    }

    private void removeTagFromList(String tag) {
/*
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        */
        SharedPreferences.Editor editor = sharedPref.edit();
        if (tag.equalsIgnoreCase("") || tag.toString() == null)
            return;

        Set<String> filterList = new HashSet<String>(sharedPref.getStringSet("filter_list", new HashSet<String>()));

        if (filterList.contains(tag)) {
            filterList.remove(tag);
            editor.putStringSet("filter_list", filterList);
            editor.commit();
        }


        //System.out.println(sharedPref.getStringSet("filter_list", new HashSet<String>()).toString());

        updateActiveTagsUI();
        updatePopularTagsUI();
    }


}