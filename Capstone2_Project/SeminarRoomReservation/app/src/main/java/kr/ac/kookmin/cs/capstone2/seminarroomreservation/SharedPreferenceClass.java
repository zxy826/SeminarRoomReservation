package kr.ac.kookmin.cs.capstone2.seminarroomreservation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * Created by ehye on 2015-09-23.
 */
public class SharedPreferenceClass {
    static Context context;

    public final static String PREF_NAME = "seminarroomreservation.pref";

    public SharedPreferenceClass(Context context) {
        this.context = context;
    }

    public void put(String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public int getValue(String key, int defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            return preferences.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public String  getValue(String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            return preferences.getString(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }


}
