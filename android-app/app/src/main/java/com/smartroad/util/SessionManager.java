package com.smartroad.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Manages the logged-in user session via SharedPreferences. */
public class SessionManager {

    private static final String PREF_NAME = "smartroad_session";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createSession(String userId, String fullname, String username) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_FULLNAME, fullname)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUserId() { return prefs.getString(KEY_USER_ID, ""); }
    public String getFullName() { return prefs.getString(KEY_FULLNAME, "User"); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, ""); }

    public void logout() {
        prefs.edit().clear().apply();
    }

    // ---- FCM token (structure only — not yet uploaded to the backend) ----
    public void saveFcmToken(String token) { prefs.edit().putString(KEY_FCM_TOKEN, token).apply(); }
}
