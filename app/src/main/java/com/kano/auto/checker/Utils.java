package com.kano.auto.checker;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;


public class Utils {
    private Utils() {

    }

    public static boolean postLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    public static boolean checkPermission(Context context,boolean requestPermission) {
        if (postLollipop() && !Utils.hasUsageStatsPermission(context)) {
            if (requestPermission) {
                 context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
            return false;
        }
        return true;
    }

}
