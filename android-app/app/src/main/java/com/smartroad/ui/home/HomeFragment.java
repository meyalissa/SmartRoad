package com.smartroad.ui.home;

import android.Manifest;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartroad.R;
import com.smartroad.config.BrandColors;
import com.smartroad.databinding.FragmentHomeBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.LocationHelper;
import com.smartroad.util.MarkerColorUtil;
import com.smartroad.util.SessionManager;
import com.smartroad.util.StatCardHelper;
import com.smartroad.viewmodel.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private LocationHelper locationHelper;
    private GoogleMap previewMap;
    private LatLng cachedUserLocation;
    private List<Hazard> cachedHazards;

    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private Runnable clockRunnable;

    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> fetchLocation());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        locationHelper = new LocationHelper(requireContext());

        SessionManager session = new SessionManager(requireContext());
        binding.tvWelcome.setText(getString(R.string.welcome_user, session.getFullName()));

        setupStatCards();

        // Lite-mode map preview lifecycle
        binding.mapPreview.onCreate(savedInstanceState);
        binding.mapPreview.getMapAsync(map -> {
            previewMap = map;
            refreshPreviewMap();
        });

        binding.swipeRefreshHome.setOnRefreshListener(this::loadStats);

        startClock();
        requestLocation();

        binding.btnViewMap.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.mapFragment));
        binding.btnReportHazard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.reportFragment));
    }

    /** One-time setup of each stat card's icon, icon tint, and label (values are filled in by loadStats()). */
    private void setupStatCards() {
        StatCardHelper.configure(binding.statTotal.tvStatLabel, binding.statTotal.ivStatIcon,
                R.string.total_reports, R.drawable.ic_total_reports,
                requireContext().getColor(R.color.primaryColor));

        StatCardHelper.configure(binding.statNew.tvStatLabel, binding.statNew.ivStatIcon,
                R.string.status_new_label, MarkerColorUtil.iconForStatus("New"),
                Color.parseColor(BrandColors.STATUS_NEW));

        StatCardHelper.configure(binding.statInvestigating.tvStatLabel, binding.statInvestigating.ivStatIcon,
                R.string.status_investigating_label, MarkerColorUtil.iconForStatus("Under Investigation"),
                Color.parseColor(BrandColors.STATUS_INVESTIGATION));

        StatCardHelper.configure(binding.statResolved.tvStatLabel, binding.statResolved.ivStatIcon,
                R.string.status_resolved_label, MarkerColorUtil.iconForStatus("Resolved"),
                Color.parseColor(BrandColors.STATUS_RESOLVED));
    }

    private void startClock() {
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat fmt = new SimpleDateFormat("EEEE, dd MMM yyyy  •  HH:mm:ss",
                        Locale.getDefault());
                binding.tvDateTime.setText(fmt.format(new Date()));
                clockHandler.postDelayed(this, 1000);
            }
        };
        clockHandler.post(clockRunnable);
    }

    private void loadStats() {
        binding.swipeRefreshHome.setRefreshing(true);
        viewModel.loadHazards().observe(getViewLifecycleOwner(), hazards -> {
            if (binding == null) return;
            if (hazards == null) {
                Toast.makeText(getContext(), R.string.error_loading_hazards, Toast.LENGTH_SHORT).show();
            }
            int total = 0, newCount = 0, investigating = 0, resolved = 0;
            if (hazards != null) {
                total = hazards.size();
                for (Hazard h : hazards) {
                    switch (h.getStatus() == null ? "" : h.getStatus().toLowerCase()) {
                        case "resolved":            resolved++;      break;
                        case "under investigation": investigating++; break;
                        default:                    newCount++;      break; // "New" and anything unrecognized
                    }
                }
            }
            binding.statTotal.tvStatValue.setText(String.valueOf(total));
            binding.statNew.tvStatValue.setText(String.valueOf(newCount));
            binding.statInvestigating.tvStatValue.setText(String.valueOf(investigating));
            binding.statResolved.tvStatValue.setText(String.valueOf(resolved));

            cachedHazards = hazards;
            refreshPreviewMap();
            binding.swipeRefreshHome.setRefreshing(false);
        });
    }

    /** Redraws the "You are here" marker plus every hazard marker on the lite-mode preview map. */
    private void refreshPreviewMap() {
        if (previewMap == null) return;
        previewMap.clear();

        com.google.android.gms.maps.model.LatLngBounds.Builder bounds =
                new com.google.android.gms.maps.model.LatLngBounds.Builder();
        boolean hasAny = false;

        if (cachedUserLocation != null) {
            previewMap.addMarker(new MarkerOptions().position(cachedUserLocation).title("You"));
            bounds.include(cachedUserLocation);
            hasAny = true;
        }

        if (cachedHazards != null) {
            for (Hazard h : cachedHazards) {
                LatLng pos = new LatLng(h.getLatitudeAsDouble(), h.getLongitudeAsDouble());
                previewMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(h.getType())
                        .snippet(h.getStatus())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                MarkerColorUtil.hueForStatus(h.getStatus()))));
                bounds.include(pos);
                hasAny = true;
            }
        }

        if (!hasAny) return;
        // Fit the camera to every marker rather than a fixed zoom, since hazard
        // reports and the user's own location can be many km apart; falls back
        // to a simple zoom on the user if bounds can't be computed yet (the
        // lite-mode MapView may not have finished laying out).
        try {
            previewMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 80));
        } catch (Exception e) {
            if (cachedUserLocation != null) {
                previewMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cachedUserLocation, 12f));
            }
        }
    }

    private void requestLocation() {
        if (locationHelper.hasLocationPermission()) {
            fetchLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    private void fetchLocation() {
        if (!locationHelper.hasLocationPermission()) {
            binding.tvLatitude.setText(getString(R.string.latitude_label) + ": permission needed");
            return;
        }
        locationHelper.getCurrentLocation(new LocationHelper.LocationResult() {
            @Override
            public void onLocation(Location location) {
                if (binding == null) return;
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                binding.tvLatitude.setText(String.format(Locale.US, "%s: %.5f",
                        getString(R.string.latitude_label), lat));
                binding.tvLongitude.setText(String.format(Locale.US, "%s: %.5f",
                        getString(R.string.longitude_label), lng));
                cachedUserLocation = new LatLng(lat, lng);
                refreshPreviewMap();
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.tvLatitude.setText(getString(R.string.latitude_label) + ": --");
                binding.tvLongitude.setText(getString(R.string.longitude_label) + ": --");
            }
        });
    }

    // ----- MapView lifecycle forwarding -----
    // loadStats() also lives here (not onViewCreated) so Home's counts are always fresh
    // whenever the screen becomes visible again - e.g. after submitting a report or
    // switching back from another tab - with no separate "did this already load" flag needed.
    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;
        binding.mapPreview.onResume();
        loadStats();
    }
    @Override public void onPause() { if (binding != null) binding.mapPreview.onPause(); super.onPause(); }
    @Override public void onLowMemory() { super.onLowMemory(); if (binding != null) binding.mapPreview.onLowMemory(); }

    @Override
    public void onDestroyView() {
        clockHandler.removeCallbacks(clockRunnable);
        if (binding != null) binding.mapPreview.onDestroy();
        binding = null;
        super.onDestroyView();
    }
}
