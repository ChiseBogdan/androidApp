package com.example.bogdan.tasklocalstorage.api;

import com.example.bogdan.tasklocalstorage.domain.Task;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface PingResource {

//    String BASE_URL = "http://192.168.0.104:8080/";
    String BASE_URL = "http://172.30.116.55:8080/";


    @GET("ping")
    Call<ResponseBody> checkConnection();
}
