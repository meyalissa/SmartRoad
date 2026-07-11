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
    @POST("login.php")
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);

    @GET("get_hazards.php")
    Call<List<Hazard>> getHazards();

    @Multipart
    @POST("report_hazard.php")
    Call<ReportResponse> submitHazard(
            @Part("user_id") RequestBody userId,
            @Part("hazard_type") RequestBody hazardType,
            @Part("description") RequestBody description,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("datetime") RequestBody datetime,
            @Part MultipartBody.Part photo);

    @GET("profile.php")
    Call<ProfileResponse> getProfile(@Query("user_id") String userId);
}
