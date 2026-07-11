package com.smartroad.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.smartroad.model.ApiResponse;
import com.smartroad.model.LoginResponse;
import com.smartroad.model.ProfileResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;
import com.smartroad.util.ImageUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<LoginResponse> login(String username, String password) {
        final MutableLiveData<LoginResponse> result = new MutableLiveData<>();

        api.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    return;
                }
                // Non-2xx (400/401/500 from login.php) — the JSON error body
                // lands in errorBody(), not body(), so it must be parsed manually.
                LoginResponse parsedError = parseErrorBody(response.errorBody(), LoginResponse.class);
                if (parsedError == null) {
                    parsedError = new LoginResponse();
                    parsedError.setStatus("error");
                    parsedError.setMessage("Server error (" + response.code() + "). Please try again.");
                }
                result.setValue(parsedError);
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Covers: no network, server unreachable, timeout, and malformed/
                // non-JSON responses (Gson conversion failures land here too).
                LoginResponse error = new LoginResponse();
                error.setStatus("error");
                error.setMessage(networkFailureMessage(t));
                result.setValue(error);
            }
        });
        return result;
    }

    public LiveData<ProfileResponse> getProfile(String userId) {
        final MutableLiveData<ProfileResponse> result = new MutableLiveData<>();

        api.getProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call,
                                   @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    return;
                }
                result.setValue(parseErrorBody(response.errorBody(), ProfileResponse.class));
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                ProfileResponse error = new ProfileResponse();
                error.setStatus("error");
                result.setValue(error);
            }
        });
        return result;
    }

    public LiveData<ApiResponse> updateProfile(String userId, String fullName, String email, File photo) {
        final MutableLiveData<ApiResponse> result = new MutableLiveData<>();

        MediaType text = MediaType.parse("text/plain");
        RequestBody userIdBody = RequestBody.create(userId, text);
        RequestBody fullNameBody = RequestBody.create(fullName, text);
        RequestBody emailBody = RequestBody.create(email, text);

        MultipartBody.Part photoPart = null;
        File uploadPhoto = ImageUtils.compressForUpload(photo);
        if (uploadPhoto != null && uploadPhoto.exists()) {
            RequestBody fileBody = RequestBody.create(uploadPhoto, MediaType.parse("image/jpeg"));
            photoPart = MultipartBody.Part.createFormData("photo", uploadPhoto.getName(), fileBody);
        }

        api.updateProfile(userIdBody, fullNameBody, emailBody, photoPart).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    return;
                }
                result.setValue(parseErrorBody(response.errorBody(), ApiResponse.class));
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                result.setValue(failedApiResponse(networkFailureMessage(t)));
            }
        });
        return result;
    }

    public LiveData<ApiResponse> changePassword(String userId, String currentPassword,
                                                String newPassword, String confirmPassword) {
        final MutableLiveData<ApiResponse> result = new MutableLiveData<>();

        api.changePassword(userId, currentPassword, newPassword, confirmPassword)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(response.body());
                            return;
                        }
                        result.setValue(parseErrorBody(response.errorBody(), ApiResponse.class));
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        result.setValue(failedApiResponse(networkFailureMessage(t)));
                    }
                });
        return result;
    }

    /** Parses a non-2xx JSON error body into the given response type; every mobile API shares the same {status, message} shape on error. */
    private <T> T parseErrorBody(ResponseBody errorBody, Class<T> type) {
        if (errorBody == null) return null;
        try {
            return new Gson().fromJson(errorBody.charStream(), type);
        } catch (Exception e) {
            return null;
        }
    }

    private String networkFailureMessage(Throwable t) {
        return (t instanceof IOException)
                ? "Cannot reach the server. Check your connection and try again."
                : "Unexpected error. Please try again.";
    }

    private ApiResponse failedApiResponse(String message) {
        ApiResponse r = new ApiResponse();
        r.setStatus("error");
        r.setMessage(message);
        return r;
    }
}
