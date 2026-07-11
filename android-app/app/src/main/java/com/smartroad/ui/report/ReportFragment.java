package com.smartroad.ui.report;

import android.Manifest;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;
import com.smartroad.databinding.FragmentReportBinding;
import com.smartroad.util.LocationHelper;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.ReportViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private ReportViewModel viewModel;
    private LocationHelper locationHelper;

    private double latitude = 0d, longitude = 0d;
    private String dateString = "", timeString = "";
    private File photoFile;
    private Uri cameraUri;
    private AlertDialog loadingDialog;

    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> fetchLocation());

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> { if (granted) launchCamera(); else
                        Toast.makeText(getContext(), R.string.permission_camera_rationale,
                                Toast.LENGTH_SHORT).show(); });

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraUri != null) {
                        photoFile = uriToCacheFile(cameraUri, "camera_" + System.currentTimeMillis() + ".jpg");
                        showPreview(cameraUri);
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        photoFile = uriToCacheFile(uri, "gallery_" + System.currentTimeMillis() + ".jpg");
                        showPreview(uri);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        locationHelper = new LocationHelper(requireContext());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.hazard_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHazardType.setAdapter(adapter);

        captureDateTime();
        requestLocation();

        binding.btnCamera.setOnClickListener(v -> requestCamera());
        binding.btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        binding.btnSubmit.setOnClickListener(v -> submit());
    }

    private void captureDateTime() {
        Date now = new Date();
        dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now);
        timeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
        binding.tvReportDateTime.setText(getString(R.string.date) + ": " + dateString
                + "   " + getString(R.string.time) + ": " + timeString);
    }

    private void requestLocation() {
        if (locationHelper.hasLocationPermission()) fetchLocation();
        else locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    private void fetchLocation() {
        if (!locationHelper.hasLocationPermission() || binding == null) return;
        locationHelper.getCurrentLocation(new LocationHelper.LocationResult() {
            @Override
            public void onLocation(Location location) {
                if (binding == null) return;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                binding.tvReportLat.setText(String.format(Locale.US, "%s: %.5f",
                        getString(R.string.latitude_label), latitude));
                binding.tvReportLng.setText(String.format(Locale.US, "%s: %.5f",
                        getString(R.string.longitude_label), longitude));
            }

            @Override
            public void onError(String message) {
                if (binding == null) return;
                binding.tvReportLat.setText(getString(R.string.latitude_label) + ": --");
                binding.tvReportLng.setText(getString(R.string.longitude_label) + ": --");
            }
        });
    }

    private void requestCamera() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void launchCamera() {
        File file = new File(requireContext().getCacheDir(),
                "capture_" + System.currentTimeMillis() + ".jpg");
        cameraUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", file);
        takePictureLauncher.launch(cameraUri);
    }

    private void showPreview(Uri uri) {
        if (binding == null) return;
        Glide.with(this).load(uri).centerCrop().into(binding.ivPhotoPreview);
    }

    private File uriToCacheFile(Uri uri, String name) {
        try {
            File out = new File(requireContext().getCacheDir(), name);
            InputStream in = requireContext().getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(out);
            byte[] buf = new byte[4096];
            int len;
            if (in != null) {
                while ((len = in.read(buf)) != -1) os.write(buf, 0, len);
                in.close();
            }
            os.close();
            return out;
        } catch (Exception e) {
            return null;
        }
    }

    private void submit() {
        String type = binding.spinnerHazardType.getSelectedItem().toString();
        String desc = binding.etDescription.getText() == null
                ? "" : binding.etDescription.getText().toString().trim();

        if (desc.isEmpty()) {
            binding.tilDescription.setError("Please describe the hazard");
            return;
        }
        binding.tilDescription.setError(null);

        String datetime = dateString + " " + timeString;
        String userId = new SessionManager(requireContext()).getUserId();
        binding.btnSubmit.setEnabled(false);
        showLoading();

        viewModel.submit(userId, type, desc,
                String.valueOf(latitude), String.valueOf(longitude),
                datetime, photoFile)
                .observe(getViewLifecycleOwner(), response -> {
                    hideLoading();
                    binding.btnSubmit.setEnabled(true);
                    if (response != null && response.isSuccess()) {
                        showSuccessDialog();
                    } else {
                        String message = (response != null && response.getMessage() != null)
                                ? response.getMessage()
                                : "Submission failed. Please check your connection and try again.";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_loading, null);
        loadingDialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    private void showSuccessDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_success, null);
        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(R.string.action_ok, (d, w) -> {
                    resetForm();
                    if (binding != null) {
                        // Home reloads its stats/map fresh on every view creation,
                        // so navigating back there is enough to reflect the new report.
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.homeFragment);
                    }
                })
                .show();
    }

    private void resetForm() {
        if (binding == null) return;
        binding.spinnerHazardType.setSelection(0);
        binding.etDescription.setText("");
        binding.ivPhotoPreview.setImageResource(R.drawable.ic_gallery);
        photoFile = null;
        cameraUri = null;
        captureDateTime();
    }

    @Override
    public void onDestroyView() {
        hideLoading();
        binding = null;
        super.onDestroyView();
    }
}
