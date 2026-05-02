package com.zenith.launcher.data;

/**
 * AppModel.java
 * Represents an installed application with its label and package name.
 */
public class AppModel implements Comparable<AppModel> {
    private final String label;
    private final String packageName;

    public AppModel(String label, String packageName) {
        this.label = label != null ? label : "";
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int compareTo(AppModel other) {
        return this.label.compareToIgnoreCase(other.label);
    }
}
