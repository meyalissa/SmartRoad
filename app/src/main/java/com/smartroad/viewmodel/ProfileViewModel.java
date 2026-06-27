package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.ProfileResponse;
import com.smartroad.repository.UserRepository;

public class ProfileViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();

    public LiveData<ProfileResponse> loadProfile(String userId, String name, String username) {
        return repository.getProfile(userId, name, username);
    }
}
