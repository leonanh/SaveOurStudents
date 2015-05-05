package com.sos.saveourstudents;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.sos.saveourstudents.supportclasses.LruBitmapCache;


public class Singleton {
	
	private final String TAG = "LiveSpot Tag";
	private static Singleton instance = null;
	private static Context mContext;
	
	private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

	//static Typeface face;
	static public android.graphics.Typeface face;

	/**
	 * To initialize the class. It must be called before call the method getInstance()
	 * @param ctx The Context used
	 */
	
	public static void initialize(Context ctx) {
		mContext = ctx;
		face = Typeface.createFromAsset(mContext.getAssets(), "plane.ttf");
	}

	
	/**
	 * Check if the class has been initialized
	 * @return true  if the class has been initialized
	 *         false Otherwise
	 */
	public static boolean hasBeenInitialized() {
		return mContext != null;

	}

	/**
	 * The private constructor. Here you can use the context to initialize your variables.
	 */
	private Singleton() {
		
	}
	/**
	 * The main method used to get the instance
	 */
	public static synchronized Singleton getInstance() {
		if (mContext == null) {
			throw new IllegalArgumentException("Impossible to get the instance. This class must be initialized before");
		}

		if (instance == null) {
			instance = new Singleton();
		}

		return instance;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	
	
	public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
 
        return mRequestQueue;
    }


	public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
	
	public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
	
	
}