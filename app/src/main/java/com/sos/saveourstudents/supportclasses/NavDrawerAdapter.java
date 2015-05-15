package com.sos.saveourstudents.supportclasses;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.sos.saveourstudents.R;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder>  {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private int profile;        //int Resource for header view profile picture
    private String email;       //String Resource for header view email



    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public class ViewHolder extends RecyclerView.ViewHolder {
        int holderid;

        RippleView itemLayout;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView name;
        TextView email;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.textview); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.imageview);// Creating ImageView object with the id of ImageView from item_row.xml
                holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row

            }
            else{
                name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }

        }




    }



    public NavDrawerAdapter(String Titles[], int Icons[], String Name, String Email, int Profile){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;

    }



    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View theView = null;
        TextView text;

        if (viewType == TYPE_ITEM) {
            theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_nav_drawer_item,parent,false);
            text = (TextView)theView.findViewById(R.id.textview);
            //text.setTypeface(Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/plane.ttf"));

        } else if (viewType == TYPE_HEADER) {
            theView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header,parent,false);
        }


        return new ViewHolder(theView,viewType);

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
        if(holder.holderid ==1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons
        }
        else{

            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
            //holder.name.setText(name);
            //holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
