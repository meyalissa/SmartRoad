package com.smartroad.network;

import android.os.Build;

import androidx.annotation.NonNull;

import com.smartroad.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Central Retrofit configuration.
 *
 * >>> CHANGE THE BACKEND HERE <<<
 *   - Debug builds talk to the local Laragon PHP backend: the emulator uses
 *     10.0.2.2 (its alias for the host machine's localhost), while a physical
 *     device on the same Wi-Fi uses the dev machine's LAN IP instead. Both
 *     values come from BuildConfig, sourced from local.properties'
 *     DEVICE_LAN_IP — update that file if your network changes.
 *   - Release builds use BuildConfig.BASE_URL (set in app/build.gradle).
 *
 * Every module (login, hazards, report submission, profile) talks to the
 * live PHP backend — there is no offline/demo mode.
 */
public class ApiClient {

    private static final String DEVICE_BASE_URL =
            "http://" + BuildConfig.DEVICE_LAN_IP + "/SmartRoad/web-admin/api/";

    public static final String BASE_URL = BuildConfig.DEBUG
            ? (isEmulator() ? BuildConfig.EMULATOR_BASE_URL : DEVICE_BASE_URL)
            : BuildConfig.BASE_URL;

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
            // Full request/response bodies (including the login password field)
            // are only logged in debug builds — never in release, to avoid
            // leaking credentials or user data via Logcat.
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

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
