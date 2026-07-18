package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.Hazard;
import com.smartroad.repository.HazardRepository;

import java.util.List;

/** Backs the home screen, delegating hazard list retrieval to {@link HazardRepository}. */
public class HomeViewModel extends ViewModel {

    private final HazardRepository repository = new HazardRepository();

    /** Loads all hazard reports for display on the home screen. */
    public LiveData<List<Hazard>> loadHazards() {
        return repository.getHazards();
    }
}
