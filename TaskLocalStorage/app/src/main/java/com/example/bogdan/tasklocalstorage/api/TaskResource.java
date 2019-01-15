package com.example.bogdan.tasklocalstorage.api;

import com.example.bogdan.tasklocalstorage.domain.Task;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TaskResource {

//    String BASE_URL = "http://192.168.0.104:8080/";
    String BASE_URL = "http://172.30.116.55:8080/";

    @GET("tasks")
    Call<List<Task>> getAllTasks(@Header("Authorization") String token, @Header("If-None-Match") String ETag);

    @POST("tasks")
    Call<ResponseBody> insertTask(@Header("Authorization") String token,@Body Task task);
}
