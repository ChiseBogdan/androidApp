package com.example.bogdan.tasklocalstorage.update;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ServiceUpdate extends JobService  {

    List<Task> tasksToBeAdded;

    public ServiceUpdate(){
        tasksToBeAdded = TaskRepository.getAllTasksToBeAdded();
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        UpdateTask updateTask = new UpdateTask();
        updateTask.addTasksRemote(tasksToBeAdded);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
