package com.zenith.launcher.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zenith.launcher.R;
import com.zenith.launcher.data.AppModel;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private List<AppModel> fullAppList = new ArrayList<>();
    private List<AppModel> filteredAppList = new ArrayList<>();
    private final OnAppClickListener listener;

    public interface OnAppClickListener {
        void onAppClicked(AppModel app);
    }

    public AppListAdapter(OnAppClickListener listener) {
        this.listener = listener;
    }

    public void setApps(List<AppModel> apps) {
        this.fullAppList = new ArrayList<>(apps);
        this.filteredAppList = new ArrayList<>(apps);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredAppList.clear();
        if (query.isEmpty()) {
            filteredAppList.addAll(fullAppList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AppModel app : fullAppList) {
                if (app.getLabel().toLowerCase().contains(lowerCaseQuery)) {
                    filteredAppList.add(app);
                }
            }
        }
        notifyDataSetChanged();
    }
    
    public AppModel getFirstFilteredApp() {
        if (!filteredAppList.isEmpty()) {
            return filteredAppList.get(0);
        }
        return null;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppModel app = filteredAppList.get(position);
        holder.bind(app, listener);
    }

    @Override
    public int getItemCount() {
        return filteredAppList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private final TextView appNameText;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameText = itemView.findViewById(R.id.app_name_text);
        }

        public void bind(AppModel app, OnAppClickListener listener) {
            appNameText.setText(app.getLabel());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAppClicked(app);
                }
            });
        }
    }
}
