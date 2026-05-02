package com.zenith.launcher.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * FrictionHelper.java
 * Implements a slight delay for specific apps to encourage mindful usage.
 */
public class FrictionHelper {
    
    // A simple list of package names that might be considered distracting.
    // In a future feature_settings module, this would be user-configurable.
    private static final String[] DISTRACTING_APPS = {
        "com.instagram.android",
        "com.zhiliaoapp.musically", // TikTok
        "com.twitter.android",
        "com.facebook.katana"
    };

    /**
     * Checks if a package name belongs to a known distracting app.
     * @param packageName The package name to check
     * @return true if distracting
     */
    public static boolean isDistracting(String packageName) {
        for (String app : DISTRACTING_APPS) {
            if (app.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Applies a timed friction before running the proceed action.
     * @param context the context
     * @param onProceed the action to run after friction
     */
    public static void applyFriction(Context context, Runnable onProceed) {
        // Apply a 2 second delay for distracting apps.
        new Handler(Looper.getMainLooper()).postDelayed(onProceed, 2000);
    }
}
