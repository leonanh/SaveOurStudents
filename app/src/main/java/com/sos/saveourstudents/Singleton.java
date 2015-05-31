package com.sos.saveourstudents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Singleton {

	private final String TAG = "SOS Tag";
	private static Singleton instance = null;
	private static Context mContext;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	/**
	 * To initialize the class. It must be called before call the method getInstance()
	 * @param ctx The Context used
	 */

	public static void initialize(Context ctx) {
		mContext = ctx;
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
			mImageLoader = new ImageLoader(mRequestQueue,
					new ImageLoader.ImageCache() {
						private final LruCache<String, Bitmap>
								cache = new LruCache<String, Bitmap>(20);

						@Override
						public Bitmap getBitmap(String url) {
							return cache.get(url);
						}

						@Override
						public void putBitmap(String url, Bitmap bitmap) {
							cache.put(url, bitmap);
						}
					});
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



	static String get_SHA_1_SecurePassword(String passwordToHash){
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			//md.update(salt.getBytes()); //No salt....
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return generatedPassword;
	}

	@SuppressLint("SimpleDateFormat")
	public String doDateLogic(String theDate){
		theDate = theDate.replace(",", "");


		String newDate = null;
		Date oldDate = null;

		DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a", Locale.ENGLISH);

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			oldDate = dateFormat.parse(theDate);
			Date currentDate = new Date();

			long diff = currentDate.getTime() - oldDate.getTime();
			long seconds = diff / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;
			long months = days / 30;
			long years = months / 12;


			if (oldDate.before(currentDate)) {

				if (days < 1) { //Less than a day
					if(hours < 1){
						newDate = minutes+"M";
					}
					else{
						newDate = hours+"H";
					}

					if(newDate.equalsIgnoreCase("0M")){
						newDate = "Just now";
					}
				}
				else{//A day
					newDate = days+"D";
				}
				if(months > 0){
					newDate = months+"MO";
				}

				if(years > 0){
					newDate = years+"YR";
				}
			}
			else
				newDate = "Just now";

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return newDate;
	}






	public String doDistanceLogic(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515; //Miles
		if (unit.equalsIgnoreCase("K")){ //Kilometers
			dist = dist * 1.609344;
		} else if (unit.equalsIgnoreCase("N")){ //Nautical miles - WAT
			dist = dist * 0.8684;
		}
		else if(unit.equalsIgnoreCase("M")){ //Meters
			dist = (dist * 1.609344) * 1000;
            NumberFormat formatter = new DecimalFormat("####");
            return (formatter.format(dist));
		}

        NumberFormat formatter = new DecimalFormat("##.0#");
		return (formatter.format(dist));
	}


	/*::  This function converts decimal degrees to radians:*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}


	/*::  This function converts radians to decimal degrees:*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}




}
