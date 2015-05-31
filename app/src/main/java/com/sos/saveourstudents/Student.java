package com.sos.saveourstudents;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by Brady on 4/25/15.
 */
public class Student implements Parcelable{
    private String firstName;
    private String lastName;
    private int rating;
    private String school;
    private String major;
    private String description;
    private ImageView profilePicture;
    private String profilePictureUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Student(String firstName, String lastName, int rating, String school, String major,
                   String description, String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = rating;
        this.school = school;
        this.major = major;
        this.description = description;
        this.profilePictureUrl = profilePictureUrl;
        this.profilePicture = null;
    }

    public Student(String firstName, String lastName, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageView getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageView profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}