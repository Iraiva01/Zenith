package com.zenith.launcher.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.AlarmClock;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.zenith.launcher.R;
import android.os.Build;
import com.zenith.launcher.receiver.ZenithDeviceAdminReceiver;
import com.zenith.launcher.service.ZenithAccessibilityService;
import java.lang.reflect.Method;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up favorite apps clicks (Dummy package names based on the screenshot
        // text)
        setupFavApp(view, R.id.fav_atom, "com.subconscious.thrive");
        setupFavApp(view, R.id.fav_finch, "com.finch.finch");
        setupFavApp(view, R.id.fav_phonepe, "com.phonepe.app");
        setupFavApp(view, R.id.fav_regain, "ai.regainapp");
        setupFavApp(view, R.id.fav_homeworkout, "homeworkout.homeworkouts.noequipment");

        // Set up bottom icons
        view.findViewById(R.id.icon_phone).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            launchAppWithIconReveal(intent, v);
        });

        view.findViewById(R.id.icon_camera).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            launchAppWithIconReveal(intent, v);
        });

        // Clock Widget Click
        view.findViewById(R.id.clock_container).setOnClickListener(v -> {
            Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            launchAppWithBottomCenterReveal(intent);
        });

        // Gestures Setup
        GestureDetector gestureDetector = new GestureDetector(requireContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        view.performClick();
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        lockScreen();
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (e1 == null || e2 == null)
                            return false;
                        float deltaY = e2.getY() - e1.getY();
                        float deltaX = e2.getX() - e1.getX();
                        if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > 100 && Math.abs(velocityY) > 100) {
                            if (deltaY > 0) {
                                openNotifications();
                            } else {
                                openSearch((int) e1.getX(), (int) e1.getY());
                            }
                            return true;
                        }
                        return false;
                    }
                });

        view.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getX() - startX);
                        float deltaY = Math.abs(event.getY() - startY);
                        if (deltaY > deltaX && deltaY > 10) {
                            if (v.getParent() != null) {
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        break;
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void setupFavApp(View view, int id, String packageName) {
        view.findViewById(id).setOnClickListener(v -> {
            Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                launchAppWithBottomCenterReveal(launchIntent);
            } else {
                Toast.makeText(requireContext(), "App not installed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void lockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ZenithAccessibilityService.instance != null) {
                ZenithAccessibilityService.lockScreen();
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(requireContext(), "Please enable Zenith Launcher Accessibility Service to lock screen with fingerprint support", Toast.LENGTH_LONG).show();
            }
        } else {
            // Fallback for Android 8 and below
            DevicePolicyManager dpm = (DevicePolicyManager) requireContext()
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(requireContext(), ZenithDeviceAdminReceiver.class);

            if (dpm != null && dpm.isAdminActive(adminComponent)) {
                dpm.lockNow();
            } else {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock the screen.");
                startActivity(intent);
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void openNotifications() {
        try {
            Object service = requireContext().getSystemService("statusbar");
            if (service != null) {
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method expand = null;
                try {
                    expand = statusbarManager.getMethod("expandNotificationsPanel");
                } catch (NoSuchMethodException e) {
                    expand = statusbarManager.getMethod("expand");
                }
                if (expand != null) {
                    expand.invoke(service);
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            Toast.makeText(requireContext(), "Cannot open notifications: " + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void openSearch(int startX, int startY) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        launchAppWithBottomCenterReveal(intent);
    }

    private void launchAppWithIconReveal(Intent intent, View iconView) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            Bundle bundle = ActivityOptions.makeScaleUpAnimation(
                iconView, 
                0, 
                0, 
                iconView.getWidth(), 
                iconView.getHeight()
            ).toBundle();
            startActivity(intent, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "App not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchAppWithBottomCenterReveal(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            Bundle bundle = null;
            View sourceView = getView();
            if (sourceView != null) {
                int centerX = sourceView.getWidth() / 2;
                int bottomY = sourceView.getHeight();
                bundle = ActivityOptions.makeScaleUpAnimation(sourceView, centerX, bottomY, 0, 0).toBundle();
            }
            startActivity(intent, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "App not found", Toast.LENGTH_SHORT).show();
        }
    }
}
