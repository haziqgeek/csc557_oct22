package com.example.csc557_oct22.remote;


import com.example.csc557_oct22.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("api/users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/users/login")
    Call<User> loginEmail(@Field("email") String email, @Field("password") String password);

    @GET("api/users/?role=lecturer")
    Call<List<User>> getAllLecturers(@Header("api-key") String apiKey);

}