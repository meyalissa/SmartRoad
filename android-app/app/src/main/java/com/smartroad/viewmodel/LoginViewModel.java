package com.smartroad.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.smartroad.model.LoginResponse;
import com.smartroad.repository.UserRepository;

/** Backs the login screen, delegating authentication to {@link UserRepository}. */
public class LoginViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();

    public LiveData<LoginResponse> login(String username, String password) {
        return repository.login(username, password);
    }
}
