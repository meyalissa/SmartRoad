package com.smartroad.network;

import com.smartroad.model.Hazard;
import com.smartroad.model.LoginResponse;
import com.smartroad.model.ProfileResponse;
import com.smartroad.model.ReportResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);

    @GET("hazards")
    Call<List<Hazard>> getHazards();

    @Multipart
    @POST("report")
    Call<ReportResponse> submitHazard(
            @Part("hazard_type") RequestBody hazardType,
            @Part("description") RequestBody description,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("datetime") RequestBody datetime,
            @Part MultipartBody.Part photo);

    @GET("profile")
    Call<ProfileResponse> getProfile(@Query("id") String userId);
}
