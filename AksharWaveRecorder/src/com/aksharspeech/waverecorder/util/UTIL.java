/**
 * 
 */
package com.aksharspeech.waverecorder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author amitkumarsah
 * 
 */
public class UTIL {
	
	/**
	 * verify that file is text file or not 
	 * @param fname
	 * @return
	 */
	public static boolean verifyTextFile(String fname) {
		if (fname.trim().endsWith(".txt"))
			return true;
		else
			return false;

	}

	public static boolean savePrefernceStringValue(Context context, String key,
			String value) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public static boolean savePrefernceIntValue(Context context, String key,
			int value) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	public static boolean savePrefernceFloatValue(Context context, String key,
			float value) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}

	public static int getPrefernceIntValue(Context context, String key,
			int defValue) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		return pref.getInt(key, defValue);

	}
	public static float getPrefernceFloatValue(Context context, String key,
			float defValue) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		return pref.getFloat(key, defValue);

	}

	public static Boolean getPrefernceBoolValue(Context context, String key,
			Boolean defValue) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		return pref.getBoolean(key, defValue);

	}

	public static String getPrefernceStringValue(Context context, String key,
			String defValue) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		return pref.getString(key, defValue);

	}

	public static boolean savePrefernceBoolValue(Context context, String key,
			Boolean value) {
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

}
