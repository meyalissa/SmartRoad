package com.smartroad.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;
import com.smartroad.databinding.ActivityEditProfileBinding;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.EditProfileViewModel;
import com.smartroad.viewmodel.ProfileViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/** Lets the logged-in user update their full name, email and profile photo. */
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private EditProfileViewModel editProfileViewModel;
    private SessionManager session;

    private File selectedPhoto;
    private Uri cameraUri;
    private AlertDialog loadingDialog;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && cameraUri != null) {
                    selectedPhoto = uriToCacheFile(cameraUri, "profile_camera_" + System.currentTimeMillis() + ".jpg");
                    showPhotoPreview(cameraUri);
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedPhoto = uriToCacheFile(uri, "profile_gallery_" + System.currentTimeMillis() + ".jpg");
                    showPhotoPreview(uri);
                }
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

        binding.btnChangePhoto.setOnClickListener(v -> launchCamera());
        binding.btnSave.setOnClickListener(v -> save());
        binding.btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));
    }

    private void launchCamera() {
        File file = new File(getCacheDir(), "profile_capture_" + System.currentTimeMillis() + ".jpg");
        cameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        takePictureLauncher.launch(cameraUri);
    }

    private void showPhotoPreview(Uri uri) {
        Glide.with(this).load(uri).circleCrop().into(binding.ivEditPhoto);
    }

    private File uriToCacheFile(Uri uri, String name) {
        try {
            File out = new File(getCacheDir(), name);
            InputStream in = getContentResolver().openInputStream(uri);
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
        showLoading();

        editProfileViewModel.save(session.getUserId(), fullName, email, selectedPhoto)
                .observe(this, response -> {
                    hideLoading();
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

    private void showLoading() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        loadingDialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        binding = null;
        super.onDestroy();
    }
}
