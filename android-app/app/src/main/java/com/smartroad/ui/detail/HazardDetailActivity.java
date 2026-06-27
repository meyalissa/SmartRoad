package com.smartroad.ui.detail;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.smartroad.R;
import com.smartroad.databinding.ActivityHazardDetailBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.MarkerColorUtil;

import java.util.Locale;

public class HazardDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HAZARD = "extra_hazard";

    private ActivityHazardDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHazardDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        Hazard hazard = (Hazard) getIntent().getSerializableExtra(EXTRA_HAZARD);
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
    }

    private void bindRow(com.smartroad.databinding.ItemDetailRowBinding row,
                         String label, String value) {
        row.tvRowLabel.setText(label);
        row.tvRowValue.setText(value);
    }
}
