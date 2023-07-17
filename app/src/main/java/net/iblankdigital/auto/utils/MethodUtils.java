package net.iblankdigital.auto.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.iblankdigital.auto.AutoApp;
import net.iblankdigital.auto.checker.Utils;
import net.iblankdigital.auto.checker.detectors.Detector;
import net.iblankdigital.auto.checker.detectors.LollipopDetector;
import net.iblankdigital.auto.checker.detectors.PreLollipopDetector;
import net.iblankdigital.auto.AutoApp;
import net.iblankdigital.auto.checker.Utils;
import net.iblankdigital.auto.checker.detectors.Detector;
import net.iblankdigital.auto.checker.detectors.LollipopDetector;
import net.iblankdigital.auto.checker.detectors.PreLollipopDetector;


public class MethodUtils {
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focus = activity.getCurrentFocus();
            if (focus != null) {
                inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            }
        } catch (Exception e) {
            MLog.e(e);
        }
    }

    public static void gotoSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            (context).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void killApp(Context context, String packageName) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(packageName);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void startApp(Context context, String packageName) {
        try {
//            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://feed"));
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            (context).startActivity(launchIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        Detector detector;
        if (Utils.postLollipop()) {
            detector = new LollipopDetector();
        } else {
            detector = new PreLollipopDetector();
        }
        String foregroundApp = detector.getForegroundApp(context);
        if (foregroundApp == null) {
            MLog.e("ForegroundApp null");
            return true;
        }
        MLog.e("ForegroundApp -> " + foregroundApp);
        return packageName.equals(foregroundApp);
    }

    public static int getScreenWidth(Activity act) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
//            return BaseApp.getInstance().getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            MLog.e(e);
        }
        return 0;
    }

    public static int getScreenHeight(Activity act) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
//            return BaseApp.getInstance().getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            MLog.e(e);
        }
        return 0;
    }

    @SuppressLint("InvalidWakeLockTag")
    public static void turnOnScreen() {
        try {
            AutoApp context = AutoApp.instance;
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    public static void turnOffScreen() {
        try {
            AutoApp context = AutoApp.instance;
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
            mWakeLock.acquire(180 * 60 * 1000L /*10 minutes*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSize(Context context) {
        int navigationBarHeight = getNavigationBarHeight(context);
        int statusBarHeight = getStatusBarHeight(context);
        MLog.e("navi : " + navigationBarHeight);
        MLog.e("status : " + statusBarHeight);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
