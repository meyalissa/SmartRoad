package com.smartroad.ui.myreports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smartroad.R;
import com.smartroad.databinding.ActivityMyReportsBinding;
import com.smartroad.model.Hazard;
import com.smartroad.ui.detail.HazardDetailActivity;
import com.smartroad.util.SessionManager;
import com.smartroad.viewmodel.MyReportsViewModel;

public class MyReportsActivity extends AppCompatActivity {

    private ActivityMyReportsBinding binding;
    private MyReportsViewModel viewModel;
    private MyReportsAdapter adapter;
    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        session = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(MyReportsViewModel.class);

        adapter = new MyReportsAdapter(hazard -> {
            Intent intent = new Intent(this, HazardDetailActivity.class);
            intent.putExtra(HazardDetailActivity.EXTRA_HAZARD, hazard);
            startActivity(intent);
        });
        binding.rvMyReports.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyReports.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadReports);

        // Show the spinner immediately on first load too, not just on a
        // manual pull-to-refresh, so the screen never looks blank/frozen
        // while the initial request is in flight.
        binding.swipeRefresh.setRefreshing(true);
        loadReports();
    }

    private void loadReports() {
        viewModel.loadMyReports(session.getUserId()).observe(this, hazards -> {
            binding.swipeRefresh.setRefreshing(false);
            if (hazards == null) {
                Toast.makeText(this, R.string.error_loading_reports, Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.submitList(hazards);
            binding.emptyState.setVisibility(hazards.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
