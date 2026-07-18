package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.Hazard;
import com.smartroad.repository.HazardRepository;

/** Backs the hazard detail screen, delegating report lookups to {@link HazardRepository}. */
public class HazardDetailViewModel extends ViewModel {

    private final HazardRepository repository = new HazardRepository();

    /** Fetches the report's live status and maintenance info — the Intent-passed Hazard may be stale. */
    public LiveData<Hazard> loadReportDetails(String id) {
        return repository.getReportDetails(id);
    }
}
