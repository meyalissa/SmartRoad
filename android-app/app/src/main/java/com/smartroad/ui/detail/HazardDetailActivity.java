package com.smartroad.ui.detail;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartroad.R;
import com.smartroad.databinding.ActivityHazardDetailBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.MarkerColorUtil;

import java.util.Locale;

public class HazardDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HAZARD = "extra_hazard";

    private ActivityHazardDetailBinding binding;
    private Hazard hazard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHazardDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        hazard = getHazardExtra();
        if (hazard == null) { finish(); return; }

        binding.tvDetailType.setText(hazard.getType());

        // Status badge
        binding.tvStatusBadge.setText(hazard.getStatus());
        try {
            GradientDrawable bg = (GradientDrawable) binding.tvStatusBadge.getBackground().mutate();
            bg.setColor(android.graphics.Color.parseColor(
                    MarkerColorUtil.statusColor(hazard.getStatus())));
        } catch (Exception ignored) { }

        bindRow(binding.rowDescription, getString(R.string.description), hazard.getDescription());
        bindRow(binding.rowReporter, getString(R.string.reported_by),
                TextUtils.isEmpty(hazard.getReporter()) ? "Anonymous" : hazard.getReporter());
        bindRow(binding.rowLocation, "Location",
                String.format(Locale.US, "%.5f, %.5f",
                        hazard.getLatitudeAsDouble(), hazard.getLongitudeAsDouble()));
        bindRow(binding.rowDateTime, getString(R.string.date) + " / " + getString(R.string.time),
                TextUtils.isEmpty(hazard.getDatetime()) ? "--" : hazard.getDatetime());

        if (!TextUtils.isEmpty(hazard.getPhotoUrl())) {
            Glide.with(this).load(hazard.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_gallery)
                    .into(binding.ivHazardPhoto);
        }

        binding.mapDetail.onCreate(savedInstanceState);
        binding.mapDetail.getMapAsync(map -> {
            LatLng pos = new LatLng(hazard.getLatitudeAsDouble(), hazard.getLongitudeAsDouble());
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(hazard.getType())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            MarkerColorUtil.hueForStatus(hazard.getStatus()))));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
        });
    }

    private void bindRow(com.smartroad.databinding.ItemDetailRowBinding row,
                         String label, String value) {
        row.tvRowLabel.setText(label);
        row.tvRowValue.setText(value);
    }

    @SuppressWarnings("deprecation")
    private Hazard getHazardExtra() {
        if (Build.VERSION.SDK_INT >= 33) {
            return getIntent().getSerializableExtra(EXTRA_HAZARD, Hazard.class);
        }
        return (Hazard) getIntent().getSerializableExtra(EXTRA_HAZARD);
    }

    // ----- MapView lifecycle forwarding -----
    @Override
    protected void onResume() {
        super.onResume();
        if (binding != null) binding.mapDetail.onResume();
    }

    @Override
    protected void onPause() {
        if (binding != null) binding.mapDetail.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (binding != null) binding.mapDetail.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        if (binding != null) binding.mapDetail.onDestroy();
        binding = null;
        super.onDestroy();
    }
}
