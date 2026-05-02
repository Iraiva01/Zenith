package com.zenith.launcher.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.zenith.launcher.R;
import com.zenith.launcher.core.ThemeEngine;

/**
 * MainActivity.java
 * The core UI entry point of the launcher.
 * Contains a ViewPager2 to swipe between App List and Home Screen.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LauncherPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeEngine.applyMonochromeTheme(this);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new LauncherPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Start on Home screen (index 0)
        viewPager.setCurrentItem(0, false);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) { // If on App list
            boolean handled = pagerAdapter.getAppListFragment().handleBackPressed();
            if (!handled) {
                // Go back to Home
                viewPager.setCurrentItem(0, true);
            }
        } else if (viewPager.getCurrentItem() == 0) { // If on Home
            // Do nothing, already on home
        }
    }
}
