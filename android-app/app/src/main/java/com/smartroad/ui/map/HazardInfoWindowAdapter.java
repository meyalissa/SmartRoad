package com.smartroad.ui.map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.smartroad.databinding.ViewHazardInfoWindowBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.MarkerColorUtil;

import java.util.Map;

/**
 * Renders a rich InfoWindow (hazard type, status, reporter, date, description)
 * for hazard markers, keyed off the same marker-to-hazard lookup MapFragment
 * already maintains for click handling.
 */
public class HazardInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final ViewHazardInfoWindowBinding binding;
    private final Map<Marker, Hazard> markerHazardMap;

    public HazardInfoWindowAdapter(@NonNull Context context, @NonNull Map<Marker, Hazard> markerHazardMap) {
        this.binding = ViewHazardInfoWindowBinding.inflate(LayoutInflater.from(context));
        this.markerHazardMap = markerHazardMap;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null; // keep the default window frame, only customize its contents below
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        Hazard hazard = markerHazardMap.get(marker);
        if (hazard == null) return null;

        binding.textHazardType.setText(hazard.getType());
        binding.textStatus.setText(MarkerColorUtil.displayStatus(hazard.getStatus()));
        binding.textStatus.setTextColor(Color.parseColor(MarkerColorUtil.statusColor(hazard.getStatus())));
        binding.textReporter.setText(hazard.getReporter());
        binding.textDate.setText(hazard.getDatetime());
        binding.textDescription.setText(hazard.getDescription());

        // Measure/layout explicitly: the view is never attached to a window, and GoogleMap
        // snapshots it into a bitmap immediately, so it needs valid dimensions before that.
        View root = binding.getRoot();
        root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());

        return root;
    }
}
