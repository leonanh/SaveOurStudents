package com.sos.saveourstudents;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by deamon on 4/21/15.
 */
public class Question implements Parcelable {
    public String title;
    private boolean mStudyGroup;
    private boolean mTutorsAllowed;
    private LatLng mLocation;
    private boolean mActive;
    private Student mGroupOwner;
    private String mContents;

    public String getmQuestionId() {
        return mQuestionId;
    }

    public void setmQuestionId(String mQuestionId) {
        this.mQuestionId = mQuestionId;
    }

    private String mQuestionId;


    public Question(){
        this.title = "Unset";
    }
    public Question(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean ismStudyGroup() {
        return mStudyGroup;
    }

    public void setmStudyGroup(boolean mStudyGroup) {
        this.mStudyGroup = mStudyGroup;
    }

    public boolean ismTutorsAllowed() {
        return mTutorsAllowed;
    }

    public void setmTutorsAllowed(boolean mTutorsAllowed) {
        this.mTutorsAllowed = mTutorsAllowed;
    }

    public LatLng getmLocation() {
        return mLocation;
    }

    public void setmLocation(LatLng mLocation) {
        this.mLocation = mLocation;
    }

    public boolean ismActive() {
        return mActive;
    }

    public void setmActive(boolean mActive) {
        this.mActive = mActive;
    }

    public Student getmGroupOwner() {
        return mGroupOwner;
    }

    public void setmGroupOwner(Student mGroupOwner) {
        this.mGroupOwner = mGroupOwner;
    }

    public String getmContents() {
        return mContents;
    }

    public void setmContents(String mContents) {
        this.mContents = mContents;
    }

    public Question(boolean mStudyGroup, boolean mTutorsAllowed, LatLng mLocation,
                    boolean mActive, Student mGroupOwner, String mContents) {
        this.mStudyGroup = mStudyGroup;
        this.mTutorsAllowed = mTutorsAllowed;
        this.mLocation = mLocation;
        this.mActive = mActive;
        this.mGroupOwner = mGroupOwner;
        this.mContents = mContents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeByte(mStudyGroup ? (byte) 1 : (byte) 0);
        dest.writeByte(mTutorsAllowed ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mLocation, flags);
        dest.writeByte(mActive ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mGroupOwner, 0);
        dest.writeString(this.mContents);
    }

    private Question(Parcel in) {
        this.title = in.readString();
        this.mStudyGroup = in.readByte() != 0;
        this.mTutorsAllowed = in.readByte() != 0;
        this.mLocation = in.readParcelable(LatLng.class.getClassLoader());
        this.mActive = in.readByte() != 0;
        this.mGroupOwner = in.readParcelable(Student.class.getClassLoader());
        this.mContents = in.readString();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
