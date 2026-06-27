package com.smartroad.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartroad.R;
import com.smartroad.databinding.FragmentMapBinding;
import com.smartroad.model.Hazard;
import com.smartroad.ui.detail.HazardDetailActivity;
import com.smartroad.util.LocationHelper;
import com.smartroad.util.MarkerColorUtil;
import com.smartroad.viewmodel.MapViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

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

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(map -> {
                googleMap = map;
                googleMap.setOnMarkerClickListener(this);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                // Default camera over Kuala Lumpur until location is known
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(3.1390, 101.6869), 12f));
                enableMyLocation();
                loadHazards();
            });
        }

        binding.fabRefresh.setOnClickListener(v -> loadHazards());
        binding.fabMyLocation.setOnClickListener(v -> centerOnUser());
    }

    private void loadHazards() {
        viewModel.getHazards().observe(getViewLifecycleOwner(), hazards -> {
            if (googleMap == null) return;
            googleMap.clear();
            markerHazardMap.clear();
            if (hazards == null || hazards.isEmpty()) {
                Toast.makeText(getContext(), "No hazards available", Toast.LENGTH_SHORT).show();
                return;
            }
            for (Hazard h : hazards) {
                LatLng pos = new LatLng(h.getLatitudeAsDouble(), h.getLongitudeAsDouble());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(h.getType())
                        .snippet(h.getStatus())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                MarkerColorUtil.hueFor(h.getType()))));
                if (marker != null) markerHazardMap.put(marker, h);
            }
        });
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
    public boolean onMarkerClick(@NonNull Marker marker) {
        Hazard hazard = markerHazardMap.get(marker);
        if (hazard != null) {
            Intent intent = new Intent(getContext(), HazardDetailActivity.class);
            intent.putExtra(HazardDetailActivity.EXTRA_HAZARD, hazard);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
