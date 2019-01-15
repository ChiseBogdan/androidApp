package com.example.bogdan.tasklocalstorage.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "users")
public class UserToken {

    @PrimaryKey
    @NonNull
    private String username;

    private String token;

    public UserToken(@NonNull String username, String token) {
        this.username = username;
        this.token = token;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
