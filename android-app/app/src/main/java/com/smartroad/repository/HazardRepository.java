package com.smartroad.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartroad.model.Hazard;
import com.smartroad.model.ReportResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;
import com.smartroad.util.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Repository responsible for fetching, submitting, and tracking hazard reports via the API. */
public class HazardRepository {

    private final ApiService api = ApiClient.getApiService();

    /** Fetches all hazard reports visible on the map. */
    public LiveData<List<Hazard>> getHazards() {
        final MutableLiveData<List<Hazard>> result = new MutableLiveData<>();

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

    /** Fetches the hazard reports submitted by the given user. */
    public LiveData<List<Hazard>> getMyReports(String userId) {
        final MutableLiveData<List<Hazard>> result = new MutableLiveData<>();

        api.getMyReports(userId).enqueue(new Callback<List<Hazard>>() {
            @Override
            public void onResponse(@NonNull Call<List<Hazard>> call,
                                   @NonNull Response<List<Hazard>> response) {
                result.setValue(response.isSuccessful() && response.body() != null
                        ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<List<Hazard>> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    /** Fetches the full details of a single hazard report by its ID. */
    public LiveData<Hazard> getReportDetails(String id) {
        final MutableLiveData<Hazard> result = new MutableLiveData<>();

        api.getReportDetails(id).enqueue(new Callback<Hazard>() {
            @Override
            public void onResponse(@NonNull Call<Hazard> call, @NonNull Response<Hazard> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<Hazard> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    private static final int MAX_UPLOAD_RETRIES = 2;
    private static final long RETRY_DELAY_MS = 1500L;

    /** Submits a new hazard report, compressing the attached photo before upload. */
    public LiveData<ReportResponse> submitHazard(String userId, String hazardType, String description,
                                                 String latitude, String longitude,
                                                 String datetime, File photo) {
        final MutableLiveData<ReportResponse> result = new MutableLiveData<>();

        MediaType text = MediaType.parse("text/plain");
        RequestBody userIdBody = RequestBody.create(userId, text);
        RequestBody typeBody = RequestBody.create(hazardType, text);
        RequestBody descBody = RequestBody.create(description, text);
        RequestBody latBody = RequestBody.create(latitude, text);
        RequestBody lngBody = RequestBody.create(longitude, text);
        RequestBody dtBody = RequestBody.create(datetime, text);

        MultipartBody.Part photoPart = null;
        File uploadPhoto = ImageUtils.compressForUpload(photo);
        if (uploadPhoto != null && uploadPhoto.exists()) {
            RequestBody fileBody = RequestBody.create(uploadPhoto, MediaType.parse("image/jpeg"));
            photoPart = MultipartBody.Part.createFormData("photo", uploadPhoto.getName(), fileBody);
        }

        submitWithRetry(userIdBody, typeBody, descBody, latBody, lngBody, dtBody, photoPart,
                MAX_UPLOAD_RETRIES, result);
        return result;
    }

    private void submitWithRetry(RequestBody userIdBody, RequestBody typeBody, RequestBody descBody,
                                 RequestBody latBody, RequestBody lngBody, RequestBody dtBody,
                                 MultipartBody.Part photoPart, int retriesLeft,
                                 MutableLiveData<ReportResponse> result) {
        api.submitHazard(userIdBody, typeBody, descBody, latBody, lngBody, dtBody, photoPart)
                .enqueue(new Callback<ReportResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ReportResponse> call,
                                           @NonNull Response<ReportResponse> response) {
                        result.setValue(response.isSuccessful() ? response.body() : null);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReportResponse> call, @NonNull Throwable t) {
                        // Retry on transient network failures (timeouts, dropped
                        // connections) but not once retries are exhausted.
                        if (retriesLeft > 0 && t instanceof IOException) {
                            new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    submitWithRetry(userIdBody, typeBody, descBody, latBody, lngBody,
                                            dtBody, photoPart, retriesLeft - 1, result),
                                    RETRY_DELAY_MS);
                        } else {
                            result.setValue(null);
                        }
                    }
                });
    }
}
