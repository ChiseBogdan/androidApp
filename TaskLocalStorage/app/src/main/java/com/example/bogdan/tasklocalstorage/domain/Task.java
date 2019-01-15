package com.example.bogdan.tasklocalstorage.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String description;
    private int priority;

    public Task(String description, int priority) {
        this.id = id;
        this.description = description;
        this.priority = priority;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
