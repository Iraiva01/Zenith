package com.zenith.launcher.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class ZenithAccessibilityService extends AccessibilityService {

    public static ZenithAccessibilityService instance;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used
    }

    @Override
    public void onInterrupt() {
        // Not used
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
    
    public static void lockScreen() {
        if (instance != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            instance.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
        }
    }
}
