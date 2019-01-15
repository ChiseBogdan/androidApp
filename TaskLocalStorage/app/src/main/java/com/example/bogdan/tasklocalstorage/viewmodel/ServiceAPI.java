package com.example.bogdan.tasklocalstorage.viewmodel;

import com.example.bogdan.tasklocalstorage.api.UserApplicationResource;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceAPI {

    private static Retrofit INSTANCE = null;

    private ServiceAPI(){}

    public static Retrofit getInstance()
    {
        if (INSTANCE == null){

            INSTANCE = new Retrofit.Builder()
                    .baseUrl(UserApplicationResource.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return INSTANCE;
    }
}
