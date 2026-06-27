package com.smartroad.ui.home;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartroad.R;
import com.smartroad.databinding.FragmentHomeBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.LocationHelper;
import com.smartroad.util.SessionManager;
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

        // Stat labels
        binding.statTotal.tvStatLabel.setText(R.string.total_hazards);
        binding.statResolved.tvStatLabel.setText(R.string.resolved_hazards);
        binding.statPending.tvStatLabel.setText(R.string.pending_hazards);

        // Lite-mode map preview lifecycle
        binding.mapPreview.onCreate(savedInstanceState);
        binding.mapPreview.getMapAsync(map -> previewMap = map);

        startClock();
        loadStats();
        requestLocation();

        binding.btnViewMap.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.mapFragment));
        binding.btnReportHazard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.reportFragment));
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
        viewModel.loadHazards().observe(getViewLifecycleOwner(), hazards -> {
            int total = 0, resolved = 0;
            if (hazards != null) {
                total = hazards.size();
                for (Hazard h : hazards) {
                    if ("resolved".equalsIgnoreCase(h.getStatus())) resolved++;
                }
            }
            binding.statTotal.tvStatValue.setText(String.valueOf(total));
            binding.statResolved.tvStatValue.setText(String.valueOf(resolved));
            binding.statPending.tvStatValue.setText(String.valueOf(total - resolved));
        });
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
                if (previewMap != null) {
                    LatLng pos = new LatLng(lat, lng);
                    previewMap.clear();
                    previewMap.addMarker(new MarkerOptions().position(pos).title("You"));
                    previewMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
                }
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
    @Override public void onResume() { super.onResume(); if (binding != null) binding.mapPreview.onResume(); }
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
