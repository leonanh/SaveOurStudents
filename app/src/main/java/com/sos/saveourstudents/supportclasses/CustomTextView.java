package com.sos.saveourstudents.supportclasses;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sos.saveourstudents.Singleton;


public class CustomTextView extends TextView {


    public CustomTextView(Context context) {
        super(context);

        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(context);
        }
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(context);
        }
        //this.setTypeface(Singleton.getInstance().face);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!Singleton.hasBeenInitialized()){
            Singleton.initialize(context);
        }
        //this.setTypeface(Singleton.getInstance().face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}