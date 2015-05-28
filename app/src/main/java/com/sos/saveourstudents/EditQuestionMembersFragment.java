package com.sos.saveourstudents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class EditQuestionMembersFragment extends Fragment {

    private RecycleViewAdapter mTutorAdapter, mStudentAdapter;
    private RecyclerView mTutorRecyclerView, mStudentRecyclerView;

    private String mQuestionId;
    private Context mContext;




    public static EditQuestionMembersFragment newInstance(String questionId) {
        EditQuestionMembersFragment fragment = new EditQuestionMembersFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuestionMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionId = getArguments().getString("questionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_edit_group_members, container, false);

        mQuestionId = getArguments().getString("questionId");
        if(mQuestionId.equalsIgnoreCase(""))
            Toast.makeText(mContext, "QuestionId empty in viewQuestiomFrag", Toast.LENGTH_SHORT).show();


        mTutorRecyclerView = (RecyclerView) rootView.findViewById(R.id.group_tutor_recyclerview);
        mTutorRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //mTutorRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mStudentRecyclerView = (RecyclerView) rootView.findViewById(R.id.group_student_recyclerview);
        mStudentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //mStudentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        getMemberData();


        return rootView;
    }

    private void getMemberData() {

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String currentUserId = sharedPref.getString("user_id", "");


        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("questionId", mQuestionId));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        String url = "http://54.200.33.91:8080/com.mysql.services/rest/serviceclass/viewMembers?"+paramString;


        System.out.println("view member url: " + url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                (JSONObject)null,
                new Response.Listener<JSONObject>(){



                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray tutorList = new JSONArray();
                        tutorList.put(new JSONObject());
                        tutorList.put(new JSONObject());
                        tutorList.put(new JSONObject());

                        JSONArray studentList = new JSONArray();
                        studentList.put(new JSONObject());
                        studentList.put(new JSONObject());
                        studentList.put(new JSONObject());

                        try {

                            JSONObject result = new JSONObject(response.toString());
                            System.out.println("Members result "+result);
                            if(result.getString("success").equalsIgnoreCase("1")){



                                JSONArray memberList = result.getJSONObject("result").getJSONArray("myArrayList");
                                for(int a = 0; a < memberList.length();a++){
                                    if(memberList.getJSONObject(a).getString("user_id").equalsIgnoreCase(currentUserId)){

                                    }


                                }




                                if(tutorList != null && tutorList.length() > 0) {
                                    mTutorAdapter = new RecycleViewAdapter(tutorList, R.layout.view_group_members_student_layout); //view_group_members_tutors_layout
                                    mTutorRecyclerView.setAdapter(mTutorAdapter);
                                }

                                if(studentList != null && studentList.length() > 1) {
                                    mStudentAdapter = new RecycleViewAdapter(studentList, R.layout.view_group_members_student_layout);
                                    mStudentRecyclerView.setAdapter(mStudentAdapter);
                                }



                            }
                            else{
                                //Error...
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error with connection or url: " + error.toString());
            }

        });


        Singleton.getInstance().addToRequestQueue(jsObjRequest);

    }


    public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{

        private JSONArray dataList;
        private int rowLayout;

        public RecycleViewAdapter(JSONArray commentList, int rowLayout) {
            this.dataList = commentList;
            this.rowLayout = rowLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            System.out.println("member: ");
/*
            try {
                System.out.println("member: ");//+dataList.getJSONObject(position).getJSONObject("map"));

                //String firstName = dataList.getJSONObject(position).getJSONObject("map").getString("first_name");
                //String lastName = dataList.getJSONObject(position).getJSONObject("map").getString("last_name");


                if(dataList.getJSONObject(position).getJSONObject("map").has("image") &&
                        !dataList.getJSONObject(position).getJSONObject("map").getString("image").equalsIgnoreCase("")){
                    String userImageUrl = dataList.getJSONObject(position).getJSONObject("map").getString("image");
                    getUserImage(userImageUrl, viewHolder.userImage);
                }

                //viewHolder.nameText.setText(firstName + " " + lastName);




            } catch (JSONException e) {
                e.printStackTrace();
            }
*/

        }

        @Override
        public int getItemCount() {
            return dataList.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ImageView userImage;
            public TextView nameText;

            public ViewHolder(View itemView) {
                super(itemView);
                nameText = (TextView) itemView.findViewById(R.id.user_name);
                userImage = (ImageView) itemView.findViewById(R.id.user_image);

                nameText.setVisibility(View.INVISIBLE);
                userImage.setVisibility(View.INVISIBLE);

                userImage.setOnClickListener(this);
                nameText.setOnClickListener(this);

            }


            @Override
            public void onClick(View v) {

                if((v == nameText || v == userImage)){

                    try {
                        String userId = dataList.getJSONObject(getAdapterPosition()).getJSONObject("map").getString("user_id");
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }




        }

    }





    private void showRemoveUserDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to remove this member from your group?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



    private void getUserImage(String imageUrl, final ImageView imageView){

        ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                } else {
                    // Default image...
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
                }
            }
        });

    }





}
