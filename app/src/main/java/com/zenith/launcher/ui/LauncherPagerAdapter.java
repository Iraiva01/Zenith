package com.zenith.launcher.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LauncherPagerAdapter extends FragmentStateAdapter {

    private final AppListFragment appListFragment;
    private final HomeFragment homeFragment;

    public LauncherPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        appListFragment = new AppListFragment();
        homeFragment = new HomeFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return homeFragment;
        }
        return appListFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public AppListFragment getAppListFragment() {
        return appListFragment;
    }
}
