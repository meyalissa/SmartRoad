package com.smartroad.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartroad.R;
import com.smartroad.config.BrandColors;
import com.smartroad.databinding.FragmentProfileBinding;
import com.smartroad.ui.about.AboutActivity;
import com.smartroad.ui.auth.LoginActivity;
import com.smartroad.ui.myreports.MyReportsActivity;
import com.smartroad.util.MarkerColorUtil;
import com.smartroad.util.SessionManager;
import com.smartroad.util.StatCardHelper;
import com.smartroad.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private SessionManager session;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadProfile();
                }
            });

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

        setupStatCards();

        binding.tvProfileName.setText(session.getFullName());
        binding.tvProfileUsername.setText("@" + session.getUsername());
        binding.tvAppVersion.setText(getString(R.string.version_label, appVersionName()));

        loadProfile();

        binding.btnMyReports.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyReportsActivity.class)));

        binding.btnEditProfile.setOnClickListener(v ->
                editProfileLauncher.launch(new Intent(requireContext(), EditProfileActivity.class)));

        binding.btnAbout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AboutActivity.class)));

        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    /** One-time setup of each stat card's icon, icon tint, and label (values are filled in by loadProfile()). */
    private void setupStatCards() {
        StatCardHelper.configure(binding.profileTotal.tvStatLabel, binding.profileTotal.ivStatIcon,
                R.string.total_reports, R.drawable.ic_total_reports,
                requireContext().getColor(R.color.primaryColor));

        StatCardHelper.configure(binding.profileNew.tvStatLabel, binding.profileNew.ivStatIcon,
                R.string.status_new_label, MarkerColorUtil.iconForStatus("New"),
                Color.parseColor(BrandColors.STATUS_NEW));

        StatCardHelper.configure(binding.profileInvestigating.tvStatLabel, binding.profileInvestigating.ivStatIcon,
                R.string.status_investigating_label, MarkerColorUtil.iconForStatus("Under Investigation"),
                Color.parseColor(BrandColors.STATUS_INVESTIGATION));

        StatCardHelper.configure(binding.profileResolved.tvStatLabel, binding.profileResolved.ivStatIcon,
                R.string.status_resolved_label, MarkerColorUtil.iconForStatus("Resolved"),
                Color.parseColor(BrandColors.STATUS_RESOLVED));
    }

    private void loadProfile() {
        viewModel.loadProfile(session.getUserId())
                .observe(getViewLifecycleOwner(), profile -> {
                    if (binding == null) return;
                    if (profile == null || !profile.isSuccess()) {
                        String message = (profile != null && !TextUtils.isEmpty(profile.getMessage()))
                                ? profile.getMessage()
                                : getString(R.string.error_loading_profile);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(profile.getFullname()))
                        binding.tvProfileName.setText(profile.getFullname());
                    if (!TextUtils.isEmpty(profile.getUsername()))
                        binding.tvProfileUsername.setText("@" + profile.getUsername());
                    binding.tvProfileEmail.setText(profile.getEmail());
                    binding.tvProfileEmail.setVisibility(
                            TextUtils.isEmpty(profile.getEmail()) ? View.GONE : View.VISIBLE);
                    if (!TextUtils.isEmpty(profile.getJoinDate())) {
                        binding.tvProfileJoinDate.setText(getString(R.string.joined_on, profile.getJoinDate()));
                        binding.tvProfileJoinDate.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvProfileJoinDate.setVisibility(View.GONE);
                    }
                    binding.profileTotal.tvStatValue.setText(String.valueOf(profile.getTotalReports()));
                    binding.profileNew.tvStatValue.setText(String.valueOf(profile.getPendingReports()));
                    binding.profileInvestigating.tvStatValue.setText(String.valueOf(profile.getInvestigatingReports()));
                    binding.profileResolved.tvStatValue.setText(String.valueOf(profile.getResolvedReports()));
                    if (TextUtils.isEmpty(profile.getPhotoUrl())) {
                        binding.ivProfilePhoto.setImageResource(R.drawable.ic_profile);
                    } else {
                        // Falls back to the default avatar if the URL fails to load.
                        Glide.with(this).load(profile.getPhotoUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(binding.ivProfilePhoto);
                    }
                });
    }

    private String appVersionName() {
        try {
            return requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setNegativeButton(R.string.action_cancel, null)
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
