package com.smartroad.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.ChipGroup;
import com.smartroad.R;
import com.smartroad.config.BrandColors;
import com.smartroad.databinding.FragmentMapBinding;
import com.smartroad.model.Hazard;
import com.smartroad.ui.detail.HazardDetailActivity;
import com.smartroad.util.LocationHelper;
import com.smartroad.util.MarkerIconFactory;
import com.smartroad.viewmodel.MapViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private LocationHelper locationHelper;
    private GoogleMap googleMap;

    private final Map<Marker, Hazard> markerHazardMap = new HashMap<>();

    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> enableMyLocation());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        locationHelper = new LocationHelper(requireContext());

        setupFilterChips();
        setupLegend();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(map -> {
                googleMap = map;
                googleMap.setInfoWindowAdapter(new HazardInfoWindowAdapter(requireContext(), markerHazardMap));
                googleMap.setOnInfoWindowClickListener(this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                // Default camera over Kuala Lumpur until location is known
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(3.1390, 101.6869), 12f));
                enableMyLocation();
                observeHazards();
            });
        }

        binding.fabRefresh.setOnClickListener(v -> viewModel.refresh());
        binding.fabMyLocation.setOnClickListener(v -> centerOnUser());
    }

    /** Wires the status/category ChipGroups to the ViewModel's in-memory filters. */
    private void setupFilterChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            viewModel.setStatusFilter(statusForChipId(checkedIds.get(0)));
        });

        binding.chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            viewModel.setCategoryFilter(categoryForChipId(checkedIds.get(0)));
        });
    }

    private String statusForChipId(int chipId) {
        if (chipId == R.id.chipStatusNew) return "New";
        if (chipId == R.id.chipStatusInvestigating) return "Under Investigation";
        if (chipId == R.id.chipStatusResolved) return "Resolved";
        return MapViewModel.FILTER_ALL;
    }

    private String categoryForChipId(int chipId) {
        if (chipId == R.id.chipCategoryPothole) return "Pothole";
        if (chipId == R.id.chipCategoryFlood) return "Flood";
        if (chipId == R.id.chipCategoryAccident) return "Accident";
        if (chipId == R.id.chipCategoryFallenTree) return "Fallen Tree";
        if (chipId == R.id.chipCategoryDamagedSign) return "Damaged Road Sign";
        if (chipId == R.id.chipCategoryTrafficLight) return "Broken Traffic Light";
        return MapViewModel.FILTER_ALL;
    }

    /** Tints the legend's status dots from the single source of truth, {@link BrandColors}. */
    private void setupLegend() {
        binding.legendStatusNew.setChipIconTint(
                ColorStateList.valueOf(Color.parseColor(BrandColors.STATUS_NEW)));
        binding.legendStatusInvestigating.setChipIconTint(
                ColorStateList.valueOf(Color.parseColor(BrandColors.STATUS_INVESTIGATION)));
        binding.legendStatusResolved.setChipIconTint(
                ColorStateList.valueOf(Color.parseColor(BrandColors.STATUS_RESOLVED)));
    }

    private void observeHazards() {
        viewModel.getFilteredHazards().observe(getViewLifecycleOwner(), this::renderMarkers);
    }

    /** Clears and rebuilds markers for the given (already filtered) list. Never touches the camera. */
    private void renderMarkers(@Nullable List<Hazard> hazards) {
        if (googleMap == null || binding == null) return;
        if (hazards == null) {
            Toast.makeText(getContext(), R.string.error_loading_hazards, Toast.LENGTH_SHORT).show();
        }
        googleMap.clear();
        markerHazardMap.clear();
        boolean isEmpty = hazards == null || hazards.isEmpty();
        binding.emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) return;
        for (Hazard h : hazards) {
            addHazardMarker(h);
        }
    }

    /** Adds a single marker whose icon encodes both hazard category and status (see MarkerIconFactory). */
    private void addHazardMarker(Hazard h) {
        LatLng pos = new LatLng(h.getLatitudeAsDouble(), h.getLongitudeAsDouble());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(h.getType())
                .icon(MarkerIconFactory.getMarkerIcon(requireContext(), h.getType(), h.getStatus())));
        if (marker != null) markerHazardMap.put(marker, h);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (googleMap == null) return;
        if (locationHelper.hasLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
            centerOnUser();
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    private void centerOnUser() {
        if (!locationHelper.hasLocationPermission()) return;
        locationHelper.getCurrentLocation(new LocationHelper.LocationResult() {
            @Override
            public void onLocation(Location location) {
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                }
            }

            @Override
            public void onError(String message) { /* ignore */ }
        });
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Hazard hazard = markerHazardMap.get(marker);
        if (hazard != null) {
            Intent intent = new Intent(getContext(), HazardDetailActivity.class);
            intent.putExtra(HazardDetailActivity.EXTRA_HAZARD, hazard);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
