package com.smartroad.network;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Central Retrofit configuration.
 *
 * >>> CHANGE THE BACKEND HERE <<<
 *   - BASE_URL : local Laragon PHP backend. Emulator uses 10.0.2.2 (the
 *                emulator's alias for the host machine's localhost);
 *                a physical device on the same Wi-Fi must use the dev
 *                machine's LAN IP instead — update DEVICE_LAN_IP below if
 *                your network changes.
 *   - DEMO_MODE: legacy offline-demo flag. Every module (login, hazards,
 *                report submission, profile) now always calls the real API
 *                regardless of this flag — kept in place only as a single
 *                kill switch should a future module need to fall back to
 *                sample data before its backend is ready.
 */
public class ApiClient {

    // Physical-device fallback: this machine's LAN IP (Wi-Fi). Update if it changes.
    private static final String DEVICE_LAN_IP = "10.82.146.84";

    private static final String EMULATOR_BASE_URL = "http://10.0.2.2/SmartRoad/web-admin/api/";
    private static final String DEVICE_BASE_URL = "http://" + DEVICE_LAN_IP + "/SmartRoad/web-admin/api/";

    public static final String BASE_URL = isEmulator() ? EMULATOR_BASE_URL : DEVICE_BASE_URL;

    public static final boolean DEMO_MODE = false;

    private static Retrofit retrofit;

    /** Standard heuristic for detecting an Android emulator without needing a Context. */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
