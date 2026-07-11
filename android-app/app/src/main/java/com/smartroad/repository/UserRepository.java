package com.smartroad.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.smartroad.model.LoginResponse;
import com.smartroad.model.ProfileResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<LoginResponse> login(String username, String password) {
        // Login always hits the real API now, regardless of DEMO_MODE — every
        // other feature (hazards, report submit, profile) still respects it.
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
                LoginResponse parsedError = parseErrorBody(response.errorBody());
                result.setValue(parsedError != null
                        ? parsedError
                        : failed("Server error (" + response.code() + "). Please try again."));
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Covers: no network, server unreachable, timeout, and malformed/
                // non-JSON responses (Gson conversion failures land here too).
                String message = (t instanceof IOException)
                        ? "Cannot reach the server. Check your connection and try again."
                        : "Unexpected error. Please try again.";
                result.setValue(failed(message));
            }
        });
        return result;
    }

    private LoginResponse parseErrorBody(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            return new Gson().fromJson(errorBody.charStream(), LoginResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public LiveData<ProfileResponse> getProfile(String userId) {
        // Profile now always hits the real API too — DEMO_MODE has no
        // remaining callers, every module has live backend support.
        final MutableLiveData<ProfileResponse> result = new MutableLiveData<>();

        api.getProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call,
                                   @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                    return;
                }
                ProfileResponse parsedError = parseProfileErrorBody(response.errorBody());
                result.setValue(parsedError);
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

    private ProfileResponse parseProfileErrorBody(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            return new Gson().fromJson(errorBody.charStream(), ProfileResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    private LoginResponse failed(String message) {
        LoginResponse r = new LoginResponse();
        r.setStatus("error");
        r.setMessage(message);
        return r;
    }
}
