package com.smartroad.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;
import com.smartroad.databinding.FragmentProfileBinding;
import com.smartroad.ui.auth.LoginActivity;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        session = new SessionManager(requireContext());

        binding.profileTotal.tvStatLabel.setText(R.string.total_reports);
        binding.profileResolved.tvStatLabel.setText(R.string.reports_resolved);
        binding.profilePending.tvStatLabel.setText(R.string.reports_pending);

        binding.tvProfileName.setText(session.getFullName());
        binding.tvProfileUsername.setText("@" + session.getUsername());

        viewModel.loadProfile(session.getUserId(), session.getFullName(), session.getUsername())
                .observe(getViewLifecycleOwner(), profile -> {
                    if (profile == null || binding == null) return;
                    if (!TextUtils.isEmpty(profile.getFullname()))
                        binding.tvProfileName.setText(profile.getFullname());
                    if (!TextUtils.isEmpty(profile.getUsername()))
                        binding.tvProfileUsername.setText("@" + profile.getUsername());
                    binding.profileTotal.tvStatValue.setText(String.valueOf(profile.getTotalReports()));
                    binding.profileResolved.tvStatValue.setText(String.valueOf(profile.getResolvedReports()));
                    binding.profilePending.tvStatValue.setText(String.valueOf(profile.getPendingReports()));
                    if (!TextUtils.isEmpty(profile.getPhotoUrl())) {
                        Glide.with(this).load(profile.getPhotoUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(binding.ivProfilePhoto);
                    }
                });

        binding.btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show());

        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.action_logout)
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(R.string.action_logout, (d, w) -> {
                    session.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
