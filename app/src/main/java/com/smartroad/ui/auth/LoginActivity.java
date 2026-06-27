package com.smartroad.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smartroad.databinding.ActivityLoginBinding;
import com.smartroad.ui.main.MainActivity;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);

        // Already logged in? Skip straight to the app.
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = textOf(binding.etUsername.getText());
        String password = textOf(binding.etPassword.getText());

        binding.tilUsername.setError(null);
        binding.tilPassword.setError(null);

        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError(getString(com.smartroad.R.string.error_username_required));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(com.smartroad.R.string.error_password_required));
            return;
        }

        setLoading(true);
        viewModel.login(username, password).observe(this, response -> {
            setLoading(false);
            if (response != null && response.isSuccess()) {
                session.createSession(response.getId(), response.getFullname(),
                        response.getUsername() != null ? response.getUsername() : username);
                goToMain();
            } else {
                String msg = (response != null && response.getMessage() != null)
                        ? response.getMessage()
                        : getString(com.smartroad.R.string.login_failed);
                binding.tilPassword.setError(msg);
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String textOf(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}
