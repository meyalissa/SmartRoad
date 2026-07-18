package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.Hazard;
import com.smartroad.repository.HazardRepository;

import java.util.List;

/** Backs the "my reports" screen, delegating retrieval of a user's own reports to {@link HazardRepository}. */
public class MyReportsViewModel extends ViewModel {

    private final HazardRepository repository = new HazardRepository();

    /** Loads the hazard reports submitted by the given user. */
    public LiveData<List<Hazard>> loadMyReports(String userId) {
        return repository.getMyReports(userId);
    }
}
