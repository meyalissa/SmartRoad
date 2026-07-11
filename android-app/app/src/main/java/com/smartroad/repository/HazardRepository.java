package com.smartroad.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartroad.model.Hazard;
import com.smartroad.model.ReportResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        // Hazard retrieval always hits the real API now, regardless of
        // DEMO_MODE — report submission and profile still respect it.
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

    private static final int MAX_UPLOAD_RETRIES = 2;
    private static final long RETRY_DELAY_MS = 1500L;
    private static final int MAX_PHOTO_DIMENSION = 1280;
    private static final int JPEG_QUALITY = 80;

    public LiveData<ReportResponse> submitHazard(String userId, String hazardType, String description,
                                                 String latitude, String longitude,
                                                 String datetime, File photo) {
        // Report submission always hits the real API now, same as hazard
        // retrieval — DEMO_MODE still governs profile and any other
        // not-yet-implemented module.
        final MutableLiveData<ReportResponse> result = new MutableLiveData<>();

        MediaType text = MediaType.parse("text/plain");
        RequestBody userIdBody = RequestBody.create(text, userId);
        RequestBody typeBody = RequestBody.create(text, hazardType);
        RequestBody descBody = RequestBody.create(text, description);
        RequestBody latBody = RequestBody.create(text, latitude);
        RequestBody lngBody = RequestBody.create(text, longitude);
        RequestBody dtBody = RequestBody.create(text, datetime);

        MultipartBody.Part photoPart = null;
        File uploadPhoto = compressPhoto(photo);
        if (uploadPhoto != null && uploadPhoto.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), uploadPhoto);
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

    /** Downscales/re-encodes the photo as JPEG before upload; falls back to the original file if it fails. */
    private File compressPhoto(File original) {
        if (original == null || !original.exists()) return null;
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(original.getAbsolutePath(), bounds);
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return original;

            int sample = 1;
            while ((bounds.outWidth / sample) > MAX_PHOTO_DIMENSION
                    || (bounds.outHeight / sample) > MAX_PHOTO_DIMENSION) {
                sample *= 2;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bitmap = BitmapFactory.decodeFile(original.getAbsolutePath(), opts);
            if (bitmap == null) return original;

            File compressed = new File(original.getParentFile(), "upload_" + original.getName());
            try (FileOutputStream out = new FileOutputStream(compressed)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            }
            bitmap.recycle();
            return compressed;
        } catch (Exception e) {
            return original;
        }
    }
}
