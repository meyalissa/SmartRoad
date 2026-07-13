package com.smartroad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.smartroad.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds and caches custom hazard map marker bitmaps: a teardrop pin tinted by
 * report status ({@link MarkerColorUtil}), with a small Material-style icon for
 * the hazard category centered inside the pin's head. Status color and category
 * icon are resolved independently so every (type, status) combination is just a
 * cache lookup after its first render.
 */
public final class MarkerIconFactory {

    private MarkerIconFactory() { }

    private static final Map<String, BitmapDescriptor> CACHE = new HashMap<>();

    private static final float PIN_SIZE_DP = 44f;
    private static final float ICON_SCALE = 0.42f;
    private static final float ICON_TOP_OFFSET = 0.14f;

    /** Returns the cached marker icon for this hazard type + status, rendering it on first use. */
    public static BitmapDescriptor getMarkerIcon(Context context, String hazardType, String status) {
        String key = normalize(hazardType) + "|" + normalize(status);
        BitmapDescriptor cached = CACHE.get(key);
        if (cached != null) return cached;

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(renderPin(context, hazardType, status));
        CACHE.put(key, descriptor);
        return descriptor;
    }

    private static Bitmap renderPin(Context context, String hazardType, String status) {
        int size = dpToPx(context, PIN_SIZE_DP);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Drawable pin = ContextCompat.getDrawable(context, R.drawable.ic_marker_pin_bg);
        if (pin != null) {
            pin = pin.mutate();
            DrawableCompat.setTint(pin, Color.parseColor(MarkerColorUtil.statusColor(status)));
            pin.setBounds(0, 0, size, size);
            pin.draw(canvas);
        }

        Drawable icon = ContextCompat.getDrawable(context, categoryIcon(hazardType));
        if (icon != null) {
            icon = icon.mutate();
            DrawableCompat.setTint(icon, Color.WHITE);
            int iconSize = Math.round(size * ICON_SCALE);
            int left = (size - iconSize) / 2;
            int top = Math.round(size * ICON_TOP_OFFSET);
            icon.setBounds(left, top, left + iconSize, top + iconSize);
            icon.draw(canvas);
        }

        return bitmap;
    }

    /** Maps a hazard_type string from the API to its Material-style category icon resource. */
    public static int categoryIcon(String hazardType) {
        if (hazardType == null) return R.drawable.ic_hazard_pothole;
        switch (hazardType.trim().toLowerCase()) {
            case "flood":                return R.drawable.ic_hazard_flood;
            case "accident":             return R.drawable.ic_hazard_accident;
            case "fallen tree":          return R.drawable.ic_hazard_fallen_tree;
            case "damaged road sign":    return R.drawable.ic_hazard_damaged_sign;
            case "broken traffic light": return R.drawable.ic_hazard_traffic_light;
            case "pothole":
            default:                     return R.drawable.ic_hazard_pothole;
        }
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
