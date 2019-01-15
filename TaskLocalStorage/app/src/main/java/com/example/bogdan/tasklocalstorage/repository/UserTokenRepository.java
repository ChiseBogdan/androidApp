package com.example.bogdan.tasklocalstorage.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.bogdan.tasklocalstorage.domain.UserToken;
import com.example.bogdan.tasklocalstorage.persistance.DatabaseCreator;
import com.example.bogdan.tasklocalstorage.persistance.UserTokenDao;

public class UserTokenRepository {

    private static UserTokenDao mUserTokenDao;

    private static Application application = null;

    private static UserTokenRepository INSTANCE = null;

    private UserTokenRepository(){}

    private UserToken localUserToken;


    public static UserTokenRepository getInstance(){

        if (INSTANCE == null){
            INSTANCE = new UserTokenRepository();

        }

        return INSTANCE;
    }

    public void init(Application application){
        if(this.application == null){
            this.application = application;
            DatabaseCreator db = DatabaseCreator.getDatabase(application);
            mUserTokenDao = db.userTokenDao();
        }
    }

    public UserToken getLoogedUser(){
        UserToken userToken  = mUserTokenDao.getLoggedUser();
        return userToken;
    }

    public void insert(UserToken userToken){

        mUserTokenDao.insert(userToken);

        setLocalUserToken(userToken);

//        new UserTokenRepository.insertAsyncUserApplication(mUserTokenDao).execute(userToken);
    }

    private static class InsertAsyncUserApplication extends AsyncTask<UserToken, Void, Void> {

        private UserTokenDao mUserTokenDao;

        InsertAsyncUserApplication(UserTokenDao dao) {
            mUserTokenDao = dao;
        }

        @Override
        protected Void doInBackground(UserToken... userTokens) {
            mUserTokenDao.insert(userTokens[0]);
            return null;
        }
    }

    public UserToken getLocalUserToken() {
        return localUserToken;
    }

    public void setLocalUserToken(UserToken localUserToken) {
        this.localUserToken = localUserToken;
    }
}
