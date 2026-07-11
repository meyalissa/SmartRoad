package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.ApiResponse;
import com.smartroad.repository.UserRepository;

public class ChangePasswordViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();

    public LiveData<ApiResponse> changePassword(String userId, String currentPassword,
                                                String newPassword, String confirmPassword) {
        return repository.changePassword(userId, currentPassword, newPassword, confirmPassword);
    }
}
