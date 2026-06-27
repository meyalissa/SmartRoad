package com.smartroad.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartroad.model.LoginResponse;
import com.smartroad.model.ProfileResponse;
import com.smartroad.network.ApiClient;
import com.smartroad.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<LoginResponse> login(String username, String password) {
        final MutableLiveData<LoginResponse> result = new MutableLiveData<>();

        if (ApiClient.DEMO_MODE) {
            // Demo: accept any non-empty credentials.
            LoginResponse demo = new LoginResponse();
            demo.setStatus("success");
            demo.setId("1");
            demo.setUsername(username);
            demo.setFullname(prettyName(username));
            result.setValue(demo);
            return result;
        }

        api.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(failed("Server error: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                result.setValue(failed(t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<ProfileResponse> getProfile(String userId, String cachedName, String cachedUsername) {
        final MutableLiveData<ProfileResponse> result = new MutableLiveData<>();

        if (ApiClient.DEMO_MODE) {
            ProfileResponse demo = new ProfileResponse();
            demo.setFullname(cachedName);
            demo.setUsername(cachedUsername);
            demo.setTotalReports(15);
            demo.setResolvedReports(12);
            demo.setPendingReports(3);
            result.setValue(demo);
            return result;
        }

        api.getProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call,
                                   @NonNull Response<ProfileResponse> response) {
                result.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    private LoginResponse failed(String message) {
        LoginResponse r = new LoginResponse();
        r.setStatus("error");
        r.setMessage(message);
        return r;
    }

    private String prettyName(String username) {
        if (username == null || username.trim().isEmpty()) return "SmartRoad User";
        String u = username.trim();
        return Character.toUpperCase(u.charAt(0)) + u.substring(1);
    }
}
