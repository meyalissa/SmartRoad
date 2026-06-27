package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.Hazard;
import com.smartroad.repository.HazardRepository;

import java.util.List;

public class MapViewModel extends ViewModel {

    private final HazardRepository repository = new HazardRepository();

    public LiveData<List<Hazard>> getHazards() {
        return repository.getHazards();
    }
}
