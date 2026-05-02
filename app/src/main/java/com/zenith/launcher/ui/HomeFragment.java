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

import com.zenith.launcher.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up favorite apps clicks (Dummy package names based on the screenshot text)
        setupFavApp(view, R.id.fav_atom, "com.subconscious.thrive");
        setupFavApp(view, R.id.fav_finch, "com.finch.finch");
        setupFavApp(view, R.id.fav_phonepe, "com.phonepe.app");
        setupFavApp(view, R.id.fav_regain, "ai.regainapp");
        setupFavApp(view, R.id.fav_homeworkout, "homeworkout.homeworkouts.noequipment");

        // Set up bottom icons
        view.findViewById(R.id.icon_phone).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Phone app not found", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.icon_camera).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Camera app not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFavApp(View view, int id, String packageName) {
        view.findViewById(id).setOnClickListener(v -> {
            Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(requireContext(), "App not installed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
