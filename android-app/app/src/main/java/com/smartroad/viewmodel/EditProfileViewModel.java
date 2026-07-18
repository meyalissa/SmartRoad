package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.ApiResponse;
import com.smartroad.repository.UserRepository;

import java.io.File;

/** Backs the edit-profile screen, delegating profile updates to {@link UserRepository}. */
public class EditProfileViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();

    public LiveData<ApiResponse> save(String userId, String fullName, String email, File photo) {
        return repository.updateProfile(userId, fullName, email, photo);
    }
}
