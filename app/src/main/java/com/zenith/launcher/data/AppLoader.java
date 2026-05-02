package com.zenith.launcher.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppLoader.java
 * Fetches the installed applications list asynchronously.
 */
public class AppLoader {

    private final PackageManager packageManager;
    private final ExecutorService executorService;

    public interface AppLoadListener {
        void onAppsLoaded(List<AppModel> apps);
    }

    public AppLoader(Context context) {
        this.packageManager = context.getPackageManager();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Loads apps asynchronously and returns them via listener on the background thread.
     */
    public void loadApps(AppLoadListener listener) {
        executorService.execute(() -> {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);
            List<AppModel> appList = new ArrayList<>();

            for (ResolveInfo resolveInfo : availableActivities) {
                // Ignore our own launcher
                if (resolveInfo.activityInfo.packageName.equals("com.zenith.launcher")) {
                    continue;
                }
                String label = resolveInfo.loadLabel(packageManager).toString();
                String packageName = resolveInfo.activityInfo.packageName;
                appList.add(new AppModel(label, packageName));
            }

            Collections.sort(appList);

            if (listener != null) {
                listener.onAppsLoaded(appList);
            }
        });
    }
}
