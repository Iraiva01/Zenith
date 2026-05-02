package com.zenith.launcher.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zenith.launcher.R;
import com.zenith.launcher.data.AppLoader;
import com.zenith.launcher.data.AppModel;
import com.zenith.launcher.util.FrictionHelper;

public class AppListFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView appListRecyclerView;
    private AppListAdapter adapter;
    private AppLoader appLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchInput = view.findViewById(R.id.search_input);
        appListRecyclerView = view.findViewById(R.id.app_list);

        setupRecyclerView();
        setupSearch();

        appLoader = new AppLoader(requireContext());
        loadApps();
    }

    private void setupRecyclerView() {
        appListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AppListAdapter(this::launchApp);
        appListRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                AppModel firstApp = adapter.getFirstFilteredApp();
                if (firstApp != null) {
                    launchApp(firstApp);
                }
                return true;
            }
            return false;
        });
    }

    private void loadApps() {
        appLoader.loadApps(apps -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setApps(apps));
            }
        });
    }

    private void launchApp(AppModel app) {
        Runnable launchAction = () -> {
            try {
                PackageManager pm = requireContext().getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(app.getPackageName());
                if (launchIntent != null) {
                    startActivity(launchIntent);
                    searchInput.setText("");
                } else {
                    Toast.makeText(requireContext(), "Cannot launch app", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (FrictionHelper.isDistracting(app.getPackageName())) {
            Toast.makeText(requireContext(), getString(R.string.friction_message), Toast.LENGTH_SHORT).show();
            FrictionHelper.applyFriction(requireContext(), launchAction);
        } else {
            launchAction.run();
        }
    }
    
    public boolean handleBackPressed() {
        if (searchInput.getText().length() > 0) {
            searchInput.setText("");
            return true;
        }
        return false;
    }
}
