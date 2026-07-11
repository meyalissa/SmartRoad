package com.smartroad.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;
import com.smartroad.databinding.ActivityEditProfileBinding;
import com.smartroad.util.LoadingDialogHelper;
import com.smartroad.util.PhotoPickerHelper;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.EditProfileViewModel;
import com.smartroad.viewmodel.ProfileViewModel;

import java.io.File;

/** Lets the logged-in user update their full name, email and profile photo. */
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private EditProfileViewModel editProfileViewModel;
    private SessionManager session;
    private PhotoPickerHelper photoPickerHelper;

    private File selectedPhoto;
    private AlertDialog loadingDialog;

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) photoPickerHelper.launchCamera(this);
                else Toast.makeText(this, R.string.permission_camera_rationale, Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        session = new SessionManager(this);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        photoPickerHelper = new PhotoPickerHelper(this, this, (file, previewUri) -> {
            selectedPhoto = file;
            showPhotoPreview(previewUri);
        });

        // Prefill instantly from the cached session, then refresh with the
        // authoritative values (email, photo) once the network call returns.
        binding.etFullName.setText(session.getFullName());
        profileViewModel.loadProfile(session.getUserId()).observe(this, profile -> {
            if (profile == null || !profile.isSuccess()) return;
            if (!TextUtils.isEmpty(profile.getFullname())) binding.etFullName.setText(profile.getFullname());
            if (!TextUtils.isEmpty(profile.getEmail())) binding.etEmail.setText(profile.getEmail());
            if (!TextUtils.isEmpty(profile.getPhotoUrl())) {
                Glide.with(this).load(profile.getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.ivEditPhoto);
            }
        });

        binding.btnChangePhoto.setOnClickListener(v -> showPhotoSourceChooser());
        binding.btnSave.setOnClickListener(v -> save());
        binding.btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));
    }

    private void showPhotoSourceChooser() {
        String[] options = {
                getString(R.string.dialog_take_photo),
                getString(R.string.dialog_choose_gallery),
                getString(R.string.action_cancel),
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.action_change_photo)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) requestCameraPermission();
                    else if (which == 1) photoPickerHelper.launchGallery();
                })
                .show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            photoPickerHelper.launchCamera(this);
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showPhotoPreview(Uri uri) {
        Glide.with(this).load(uri).circleCrop().into(binding.ivEditPhoto);
    }

    private void save() {
        String fullName = textOf(binding.etFullName);
        String email = textOf(binding.etEmail);

        binding.tilFullName.setError(null);
        binding.tilEmail.setError(null);

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError(getString(R.string.error_full_name_required));
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_email_required));
            return;
        }

        binding.btnSave.setEnabled(false);
        loadingDialog = LoadingDialogHelper.show(this);

        editProfileViewModel.save(session.getUserId(), fullName, email, selectedPhoto)
                .observe(this, response -> {
                    LoadingDialogHelper.hide(loadingDialog);
                    binding.btnSave.setEnabled(true);
                    if (response != null && response.isSuccess()) {
                        // Keep the cached session name in sync so it's reflected
                        // immediately elsewhere (e.g. the Home welcome banner).
                        session.createSession(session.getUserId(), fullName, session.getUsername());
                        Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String message = (response != null && !TextUtils.isEmpty(response.getMessage()))
                                ? response.getMessage()
                                : getString(R.string.error_updating_profile);
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.title_update_failed)
                                .setMessage(message)
                                .setPositiveButton(R.string.action_ok, null)
                                .show();
                    }
                });
    }

    private String textOf(com.google.android.material.textfield.TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        LoadingDialogHelper.hide(loadingDialog);
        binding = null;
        super.onDestroy();
    }
}
