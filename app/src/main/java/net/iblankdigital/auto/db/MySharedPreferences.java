package net.iblankdigital.auto.db;

import android.content.Context;
import android.content.SharedPreferences;

import net.iblankdigital.auto.utils.MLog;


public class MySharedPreferences {

    private static final String DATABASE_NAME = "KanoAuto";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }

    public static void clearAll(Context context) {
        if (getBooleanValue(context, "CLEAR_FIRST", true)) {
            MLog.e("MySharedPreferences clearAll");
            SharedPreferences pref = getSharedPreferences(context);
            pref.edit().clear().apply();
            putBooleanValue(context, "CLEAR_FIRST", false);
        }
    }

    public static void putIntValue(Context context, String key, int n) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, n);
        editor.apply();
    }

    public static int getIntValue(Context context, String key) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getInt(key, 0);
    }


    public static int getIntValue(Context context, String key, int defaultValues) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getInt(key, defaultValues);
    }


    public static void putStringValue(Context context, String key, String s) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.apply();
    }


    public static String getStringValue(Context context, String key) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getString(key, "");
    }

    public static String getStringValue(Context context, String key, String defaultValues) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getString(key, defaultValues);
    }


    public static void putBooleanValue(Context context, String key, Boolean b) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, b);
        editor.apply();
    }


    public static boolean getBooleanValue(Context context, String key) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getBoolean(key, false);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValues) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getBoolean(key, defaultValues);
    }


    public static void putLongValue(Context context, String key, long value) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLongValue(Context context, String key) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getLong(key, 0);
    }

    public static long getLongValue(Context context, String key, long defaultValues) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getLong(key, defaultValues);
    }

}
