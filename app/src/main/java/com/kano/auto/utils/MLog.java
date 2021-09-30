package com.kano.auto.utils;

import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by Ha on 8/5/2016.
 */
public class MLog {
    private static String TAG = "Kano_Auto";
    public static boolean active = true;

    public static boolean isActive() {
        return active;
    }

    public static void e(String s) {
        if (isActive()) {
            if (s == null) {
                Log.e(TAG, "null");
            } else {
                Log.e(TAG, s);
            }
        }
    }

    public static void special(String tag, String s) {
        if (isActive()) {
            MLog.e(tag, "****************************");
            MLog.e(tag, ".");
            MLog.e(tag, ".");
            MLog.e(tag, "==> " + s);
            MLog.e(tag, ".");
            MLog.e(tag, ".");
            MLog.e(tag, "****************************");
        }
    }


    public static void e(Object obj) {
        try {
            if (isActive()) {
                if (obj == null) {
                    Log.e(TAG, "null obj");
                } else {
                    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                    Log.e(TAG, "json: \n" + gson.toJson(obj));
                }
            }
        } catch (Exception e) {
            MLog.e(e);
        }
    }

    public static void e(String tag, String s) {
        if (isActive()) {
            Log.e(TAG + "_" + tag, s);
        }
    }

    public static void se(String msg) {
        if (isActive()) {
            if (msg.length() > 1000) {
                Log.e(TAG, msg.substring(0, 1000));
                se(msg.substring(1000));
            } else {
                Log.e(TAG, msg);
            }
        }
    }

    public static void i(String tag, String s) {
        if (isActive()) {
            Log.i(TAG + "_" + tag, s);
        }
    }
}
