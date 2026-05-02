package com.zenith.launcher.core;

import android.app.Activity;
import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;

/**
 * ThemeEngine.java
 * Enforces the strict black-and-white theme across the application.
 */
public class ThemeEngine {
    
    /**
     * Applies monochrome theme to the given activity's window.
     * @param activity The activity to theme.
     */
    public static void applyMonochromeTheme(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
    }
}
