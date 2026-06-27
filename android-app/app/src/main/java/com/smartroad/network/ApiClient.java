package com.smartroad.network;

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
 *   - BASE_URL : point this at your PHP/MySQL (or any) server. Must end with "/".
 *   - DEMO_MODE: when true, the app uses built-in sample data so it is fully
 *                demonstrable WITHOUT a live server (useful for the video).
 *                Set to false once your real backend is running.
 */
public class ApiClient {

    // TODO: replace with your real server, e.g. "https://smartroad.example.com/api/"
    public static final String BASE_URL = "https://your-server.com/api/";

    // TODO: set false when your backend is live.
    public static final boolean DEMO_MODE = true;

    private static Retrofit retrofit;

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
