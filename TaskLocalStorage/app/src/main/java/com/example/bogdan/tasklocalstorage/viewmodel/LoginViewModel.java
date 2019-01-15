package com.example.bogdan.tasklocalstorage.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bogdan.tasklocalstorage.api.UserApplicationResource;
import com.example.bogdan.tasklocalstorage.domain.UserApplication;
import com.example.bogdan.tasklocalstorage.domain.UserToken;
import com.example.bogdan.tasklocalstorage.repository.UserTokenRepository;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginViewModel extends AndroidViewModel {


    private static final String TAG = LoginViewModel.class.getCanonicalName();

    private UserTokenRepository mUserTokenRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        mUserTokenRepository = UserTokenRepository.getInstance();

        mUserTokenRepository.init(application);
    }

    public boolean login(String username, String password){

        Retrofit retrofit = ServiceAPI.getInstance();

        UserApplicationResource api = retrofit.create(UserApplicationResource.class);

        UserApplication user = new UserApplication(username, password);

        Call<ResponseBody> call = api.login(user);

        try {

           Response<ResponseBody> response = call.execute();

            Log.d(TAG, "login succeeded");
            Headers headers = response.headers();

            String token = headers.get("Authorization");

            if(token!=null){
                UserToken userToken = new UserToken(user.getUsername(), token);
                mUserTokenRepository.insert(userToken);

                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                Log.d(TAG, "login succeeded");
//                Headers headers = response.headers();
//
//                String token = headers.get("Authorization");
//                UserToken userToken = new UserToken(user.getUsername(), token);
//
//                mUserTokenRepository.insert(userToken);
//
////                if(userToken !=null){
////                    return true;
////                }
////
////                return false;
//
//
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                Log.e(TAG, "login failed", t);
////                return false;
//            }
//        });

    }

}
