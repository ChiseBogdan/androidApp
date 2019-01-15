package com.example.bogdan.tasklocalstorage.persistance;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.bogdan.tasklocalstorage.domain.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("DELETE FROM task")
    void nukeTable();

    @Query("SELECT * from task ORDER BY id ASC")
    List<Task> getAllTasks();

    @Update
    void updateTasks(Task... tasks);

}
