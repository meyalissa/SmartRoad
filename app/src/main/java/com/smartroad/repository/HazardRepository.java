package com.smartroad.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartroad.model.Hazard;
import com.smartroad.model.ReportResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;
import com.smartroad.util.DemoData;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HazardRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<List<Hazard>> getHazards() {
        final MutableLiveData<List<Hazard>> result = new MutableLiveData<>();

        if (ApiClient.DEMO_MODE) {
            result.setValue(DemoData.sampleHazards());
            return result;
        }

        api.getHazards().enqueue(new Callback<List<Hazard>>() {
            @Override
            public void onResponse(@NonNull Call<List<Hazard>> call,
                                   @NonNull Response<List<Hazard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Hazard>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<ReportResponse> submitHazard(String hazardType, String description,
                                                 String latitude, String longitude,
                                                 String datetime, File photo) {
        final MutableLiveData<ReportResponse> result = new MutableLiveData<>();

        if (ApiClient.DEMO_MODE) {
            // Demo: pretend the upload succeeded.
            result.setValue(demoSuccess());
            return result;
        }

        MediaType text = MediaType.parse("text/plain");
        RequestBody typeBody = RequestBody.create(text, hazardType);
        RequestBody descBody = RequestBody.create(text, description);
        RequestBody latBody = RequestBody.create(text, latitude);
        RequestBody lngBody = RequestBody.create(text, longitude);
        RequestBody dtBody = RequestBody.create(text, datetime);

        MultipartBody.Part photoPart = null;
        if (photo != null && photo.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), photo);
            photoPart = MultipartBody.Part.createFormData("photo", photo.getName(), fileBody);
        }

        api.submitHazard(typeBody, descBody, latBody, lngBody, dtBody, photoPart)
                .enqueue(new Callback<ReportResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ReportResponse> call,
                                           @NonNull Response<ReportResponse> response) {
                        result.setValue(response.isSuccessful() ? response.body() : null);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReportResponse> call, @NonNull Throwable t) {
                        result.setValue(null);
                    }
                });
        return result;
    }

    private ReportResponse demoSuccess() {
        // ReportResponse parses from JSON; emulate a success object using Gson.
        return new com.google.gson.Gson().fromJson(
                "{\"status\":\"success\",\"message\":\"Report submitted\",\"id\":\"99\"}",
                ReportResponse.class);
    }
}
