package com.smartroad.ui.detail;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartroad.R;
import com.smartroad.databinding.ActivityHazardDetailBinding;
import com.smartroad.databinding.ItemDetailRowBinding;
import com.smartroad.model.Hazard;
import com.smartroad.model.Maintenance;
import com.smartroad.util.MarkerColorUtil;
import com.smartroad.viewmodel.HazardDetailViewModel;

import java.util.Locale;

/** Shows one hazard report's full details, status timeline and (if logged) maintenance info. */
public class HazardDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HAZARD = "extra_hazard";

    private ActivityHazardDetailBinding binding;
    private HazardDetailViewModel viewModel;
    private Hazard hazard;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHazardDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        hazard = getHazardExtra();
        if (hazard == null) { finish(); return; }

        viewModel = new ViewModelProvider(this).get(HazardDetailViewModel.class);

        // Render immediately from whatever was passed in via the Intent (fast,
        // no blank screen), then refresh from the server so status changes
        // made by the admin since the list was last loaded, and any
        // maintenance record, are reflected without needing a full app restart.
        renderHazard(hazard);

        binding.mapDetail.onCreate(savedInstanceState);
        binding.mapDetail.getMapAsync(map -> {
            googleMap = map;
            renderMapMarker();
        });

        viewModel.loadReportDetails(hazard.getId()).observe(this, fresh -> {
            if (fresh == null) return;
            hazard = fresh;
            renderHazard(hazard);
            renderMapMarker();
        });
    }

    private void renderHazard(Hazard h) {
        binding.tvDetailType.setText(h.getType());
        renderStatusTimeline(h.getStatus());

        bindRow(binding.rowDescription, getString(R.string.description), h.getDescription());
        bindRow(binding.rowReporter, getString(R.string.reported_by),
                TextUtils.isEmpty(h.getReporter()) ? "Anonymous" : h.getReporter());
        bindRow(binding.rowLocation, "Location",
                String.format(Locale.US, "%.5f, %.5f",
                        h.getLatitudeAsDouble(), h.getLongitudeAsDouble()));
        bindRow(binding.rowDateTime, getString(R.string.date) + " / " + getString(R.string.time),
                TextUtils.isEmpty(h.getDatetime()) ? "--" : h.getDatetime());

        if (!TextUtils.isEmpty(h.getPhotoUrl())) {
            Glide.with(this).load(h.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_gallery)
                    .into(binding.ivHazardPhoto);
        }

        renderMaintenance(h.getMaintenance());
    }

    /** Marks each timeline step complete/pending based on status: New=1, Under Investigation=2, Resolved=3 steps done. */
    private void renderStatusTimeline(String status) {
        int completedSteps = 1;
        if ("Under Investigation".equalsIgnoreCase(status)) completedSteps = 2;
        else if ("Resolved".equalsIgnoreCase(status)) completedSteps = 3;

        setTimelineStep(binding.ivStepSubmitted, binding.tvStepSubmitted, completedSteps >= 1);
        setTimelineStep(binding.ivStepInvestigating, binding.tvStepInvestigating, completedSteps >= 2);
        setTimelineStep(binding.ivStepResolved, binding.tvStepResolved, completedSteps >= 3);

        binding.lineSubmittedToInvestigating.setBackgroundColor(lineColor(completedSteps >= 2));
        binding.lineInvestigatingToResolved.setBackgroundColor(lineColor(completedSteps >= 3));
    }

    private void setTimelineStep(android.widget.ImageView icon, android.widget.TextView label, boolean complete) {
        icon.setImageResource(complete ? R.drawable.ic_status_resolved : R.drawable.ic_circle_outline);
        label.setTextColor(getColor(complete ? R.color.statusResolved : R.color.textSecondaryColor));
    }

    private int lineColor(boolean active) {
        return getColor(active ? R.color.statusResolved : R.color.textSecondaryColor);
    }

    private void renderMaintenance(@Nullable Maintenance maintenance) {
        if (maintenance == null) {
            binding.cardMaintenance.setVisibility(View.GONE);
            return;
        }
        binding.cardMaintenance.setVisibility(View.VISIBLE);
        bindRow(binding.rowMaintenanceTeam, getString(R.string.maintenance_team_label),
                TextUtils.isEmpty(maintenance.getTeam()) ? "--" : maintenance.getTeam());
        bindRow(binding.rowRepairDate, getString(R.string.repair_date_label),
                TextUtils.isEmpty(maintenance.getRepairDate()) ? "--" : maintenance.getRepairDate());
        bindRow(binding.rowCompletedDate, getString(R.string.completed_date_label),
                TextUtils.isEmpty(maintenance.getCompletedDate()) ? "--" : maintenance.getCompletedDate());
        bindRow(binding.rowMaintenanceNotes, getString(R.string.maintenance_notes_label),
                TextUtils.isEmpty(maintenance.getNotes()) ? "--" : maintenance.getNotes());
    }

    private void renderMapMarker() {
        if (googleMap == null || hazard == null) return;
        googleMap.clear();
        LatLng pos = new LatLng(hazard.getLatitudeAsDouble(), hazard.getLongitudeAsDouble());
        googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(hazard.getType())
                .icon(BitmapDescriptorFactory.defaultMarker(
                        MarkerColorUtil.hueForStatus(hazard.getStatus()))));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
    }

    private void bindRow(ItemDetailRowBinding row, String label, String value) {
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
