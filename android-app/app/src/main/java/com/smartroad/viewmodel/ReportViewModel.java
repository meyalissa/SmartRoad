package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.ReportResponse;
import com.smartroad.repository.HazardRepository;

import java.io.File;

public class ReportViewModel extends ViewModel {

    private final HazardRepository repository = new HazardRepository();

    public LiveData<ReportResponse> submit(String userId, String type, String description,
                                           String lat, String lng,
                                           String datetime, File photo) {
        return repository.submitHazard(userId, type, description, lat, lng, datetime, photo);
    }
}
