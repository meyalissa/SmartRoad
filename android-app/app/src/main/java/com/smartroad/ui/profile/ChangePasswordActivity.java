package com.smartroad.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.smartroad.R;
import com.smartroad.databinding.ActivityChangePasswordBinding;
import com.smartroad.util.LoadingDialogHelper;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.ChangePasswordViewModel;

/** Lets the logged-in user change their password after re-entering the current one. */
public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;
    private SessionManager session;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        session = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        binding.btnSavePassword.setOnClickListener(v -> submit());
    }

    private void submit() {
        String currentPassword = textOf(binding.etCurrentPassword);
        String newPassword = textOf(binding.etNewPassword);
        String confirmPassword = textOf(binding.etConfirmPassword);

        binding.tilCurrentPassword.setError(null);
        binding.tilNewPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        if (TextUtils.isEmpty(currentPassword)) {
            binding.tilCurrentPassword.setError(getString(R.string.error_current_password_required));
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            binding.tilNewPassword.setError(getString(R.string.error_new_password_required));
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_confirm_password_required));
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            return;
        }

        binding.btnSavePassword.setEnabled(false);
        loadingDialog = LoadingDialogHelper.show(this);

        viewModel.changePassword(session.getUserId(), currentPassword, newPassword, confirmPassword)
                .observe(this, response -> {
                    LoadingDialogHelper.hide(loadingDialog);
                    binding.btnSavePassword.setEnabled(true);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = (response != null && !TextUtils.isEmpty(response.getMessage()))
                                ? response.getMessage()
                                : getString(R.string.error_changing_password);
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.title_change_failed)
                                .setMessage(message)
                                .setPositiveButton(R.string.action_ok, null)
                                .show();
                    }
                });
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        LoadingDialogHelper.hide(loadingDialog);
        binding = null;
        super.onDestroy();
    }
}
