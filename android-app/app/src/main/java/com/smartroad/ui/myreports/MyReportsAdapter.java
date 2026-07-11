package com.smartroad.ui.myreports;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smartroad.R;
import com.smartroad.databinding.ItemMyReportBinding;
import com.smartroad.model.Hazard;
import com.smartroad.util.MarkerColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ViewHolder> {

    public interface OnReportClickListener {
        void onReportClick(Hazard hazard);
    }

    private final List<Hazard> reports = new ArrayList<>();
    private final OnReportClickListener listener;

    public MyReportsAdapter(OnReportClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Hazard> newReports) {
        reports.clear();
        if (newReports != null) reports.addAll(newReports);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyReportBinding binding = ItemMyReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reports.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyReportBinding binding;

        ViewHolder(ItemMyReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Hazard hazard, OnReportClickListener listener) {
            binding.tvReportType.setText(hazard.getType());
            binding.tvReportDescription.setText(hazard.getDescription());
            binding.tvReportDate.setText(hazard.getDatetime());
            binding.tvReportLocation.setText(String.format(Locale.US, "%.5f, %.5f",
                    hazard.getLatitudeAsDouble(), hazard.getLongitudeAsDouble()));

            binding.tvStatus.setText(hazard.getStatus());
            binding.ivStatusIcon.setImageResource(MarkerColorUtil.iconForStatus(hazard.getStatus()));
            try {
                GradientDrawable bg = (GradientDrawable) binding.statusBadge.getBackground().mutate();
                bg.setColor(android.graphics.Color.parseColor(
                        MarkerColorUtil.statusColor(hazard.getStatus())));
            } catch (Exception ignored) { }

            if (hazard.getPhotoUrl() != null && !hazard.getPhotoUrl().isEmpty()) {
                Glide.with(binding.getRoot()).load(hazard.getPhotoUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.ic_gallery)
                        .into(binding.ivReportPhoto);
            } else {
                binding.ivReportPhoto.setImageResource(R.drawable.ic_gallery);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onReportClick(hazard);
            });
        }
    }
}
