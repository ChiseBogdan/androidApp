package com.example.bogdan.tasklocalstorage.persistance;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.bogdan.tasklocalstorage.domain.UserApplication;
import com.example.bogdan.tasklocalstorage.domain.UserToken;

@Dao
public interface UserTokenDao {


    @Insert
    void insert(UserToken user);


    @Query("DELETE FROM users")
    void nukeTable();

    @Query("SELECT * FROM users")
    UserToken getLoggedUser();



}
