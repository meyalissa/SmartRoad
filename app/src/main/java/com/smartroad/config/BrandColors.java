package com.smartroad.config;

import android.graphics.Color;

/**
 * SINGLE SOURCE OF TRUTH for SmartRoad branding.
 *
 * To rebrand the ENTIRE app, a future developer only needs to edit the
 * hex values in this one file (and optionally mirror them in colors.xml
 * for XML previews). Everything in code reads colors from here.
 */
public final class BrandColors {

    private BrandColors() { } // no instances

    // ---- Core brand palette ----
    public static final String PRIMARY        = "#1976D2";
    public static final String PRIMARY_DARK   = "#115293";
    public static final String SECONDARY      = "#0288D1";
    public static final String ACCENT         = "#FF9800";

    // ---- Semantic ----
    public static final String SUCCESS = "#2E7D32";
    public static final String WARNING = "#F9A825";
    public static final String ERROR   = "#C62828";

    // ---- Hazard marker colors ----
    public static final String POTHOLE_MARKER_COLOR        = "#E53935"; // Red
    public static final String FLOOD_MARKER_COLOR          = "#1E88E5"; // Blue
    public static final String ACCIDENT_MARKER_COLOR       = "#FB8C00"; // Orange
    public static final String TREE_MARKER_COLOR           = "#43A047"; // Green
    public static final String ROADSIGN_MARKER_COLOR       = "#8E24AA"; // Purple
    public static final String TRAFFICLIGHT_MARKER_COLOR   = "#FDD835"; // Yellow

    // ---- Status colors ----
    public static final String STATUS_NEW           = "#1E88E5";
    public static final String STATUS_INVESTIGATION = "#F9A825";
    public static final String STATUS_RESOLVED      = "#2E7D32";

    /** Convenience: parse any of the constants above into an int color. */
    public static int parse(String hex) {
        return Color.parseColor(hex);
    }
}
