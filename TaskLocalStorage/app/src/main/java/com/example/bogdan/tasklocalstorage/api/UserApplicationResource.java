package com.example.bogdan.tasklocalstorage.api;

import com.example.bogdan.tasklocalstorage.domain.UserApplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApplicationResource {

//    String BASE_URL = "http://192.168.0.104:8080/";
    String BASE_URL = "http://172.30.116.55:8080/";

    @POST("login")
    Call<ResponseBody> login(@Body UserApplication user);
}
