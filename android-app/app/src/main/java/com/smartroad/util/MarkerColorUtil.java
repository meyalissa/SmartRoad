package com.smartroad.util;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.smartroad.R;
import com.smartroad.config.BrandColors;

/** Maps a hazard report's status to a Google Maps marker hue, badge hex color, and badge icon. */
public class MarkerColorUtil {

    public static String statusColor(String status) {
        if (status == null) return BrandColors.STATUS_NEW;
        switch (status.toLowerCase()) {
            case "resolved":            return BrandColors.STATUS_RESOLVED;
            case "under investigation": return BrandColors.STATUS_INVESTIGATION;
            default:                    return BrandColors.STATUS_NEW;
        }
    }

    /** Map marker hue by report status (New/Under Investigation/Resolved), not hazard type. */
    public static float hueForStatus(String status) {
        if (status == null) return BitmapDescriptorFactory.HUE_AZURE;
        switch (status.toLowerCase()) {
            case "resolved":            return BitmapDescriptorFactory.HUE_GREEN;
            case "under investigation": return BitmapDescriptorFactory.HUE_ORANGE;
            default:                    return BitmapDescriptorFactory.HUE_AZURE; // New
        }
    }

    public static int iconForStatus(String status) {
        if (status == null) return R.drawable.ic_status_new;
        switch (status.toLowerCase()) {
            case "resolved":            return R.drawable.ic_status_resolved;
            case "under investigation": return R.drawable.ic_status_investigating;
            default:                    return R.drawable.ic_status_new;
        }
    }

    /**
     * User-facing label for a raw backend status value. The backend/API/database keep using
     * "New" - this only renames how it reads on screen, keeping wording consistent with the
     * "Reported -> Under Investigation -> Resolved" terminology used across the whole app.
     */
    public static String displayStatus(String status) {
        if (status == null) return "Reported";
        switch (status.toLowerCase()) {
            case "resolved":            return "Resolved";
            case "under investigation": return "Under Investigation";
            default:                    return "Reported";
        }
    }
}
