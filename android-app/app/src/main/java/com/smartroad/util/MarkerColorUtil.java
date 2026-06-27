package com.smartroad.util;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.smartroad.config.BrandColors;

/** Maps a hazard type to a Google Maps marker hue and a brand hex color. */
public class MarkerColorUtil {

    public static float hueFor(String type) {
        if (type == null) return BitmapDescriptorFactory.HUE_RED;
        switch (type.toLowerCase()) {
            case "pothole":             return BitmapDescriptorFactory.HUE_RED;
            case "flood":               return BitmapDescriptorFactory.HUE_AZURE;
            case "traffic accident":
            case "accident":            return BitmapDescriptorFactory.HUE_ORANGE;
            case "fallen tree":         return BitmapDescriptorFactory.HUE_GREEN;
            case "damaged road sign":
            case "road sign":           return BitmapDescriptorFactory.HUE_VIOLET;
            case "broken traffic light":
            case "traffic light":       return BitmapDescriptorFactory.HUE_YELLOW;
            default:                    return BitmapDescriptorFactory.HUE_RED;
        }
    }

    public static String hexFor(String type) {
        if (type == null) return BrandColors.POTHOLE_MARKER_COLOR;
        switch (type.toLowerCase()) {
            case "pothole":             return BrandColors.POTHOLE_MARKER_COLOR;
            case "flood":               return BrandColors.FLOOD_MARKER_COLOR;
            case "traffic accident":
            case "accident":            return BrandColors.ACCIDENT_MARKER_COLOR;
            case "fallen tree":         return BrandColors.TREE_MARKER_COLOR;
            case "damaged road sign":
            case "road sign":           return BrandColors.ROADSIGN_MARKER_COLOR;
            case "broken traffic light":
            case "traffic light":       return BrandColors.TRAFFICLIGHT_MARKER_COLOR;
            default:                    return BrandColors.POTHOLE_MARKER_COLOR;
        }
    }

    public static String statusColor(String status) {
        if (status == null) return BrandColors.STATUS_NEW;
        switch (status.toLowerCase()) {
            case "resolved":            return BrandColors.STATUS_RESOLVED;
            case "under investigation": return BrandColors.STATUS_INVESTIGATION;
            default:                    return BrandColors.STATUS_NEW;
        }
    }
}
