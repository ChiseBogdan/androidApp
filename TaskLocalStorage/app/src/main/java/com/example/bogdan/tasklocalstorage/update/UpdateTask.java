package com.example.bogdan.tasklocalstorage.update;

import android.support.annotation.NonNull;

import com.example.bogdan.tasklocalstorage.api.PingResource;
import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.repository.TaskRepository;
import com.example.bogdan.tasklocalstorage.repository.UserTokenRepository;

import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTask {

    private TaskExecutor taskExecutor = new TaskExecutor();
    private TaskRepository taskRepository = TaskRepository.getInstance();
    private UserTokenRepository userTokenReposoitory = UserTokenRepository.getInstance();


    public void addTasksRemote(List<Task> tasksToBeAdded) {

        PingResource ping =  TaskRepository.getPingApi();
        Call<ResponseBody> call = ping.checkConnection();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200){
                    if(tasksToBeAdded!= null){
                        for(Task task: tasksToBeAdded){
                            taskExecutor.execute(new TaskRemoteUpdate(task));
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    class TaskExecutor implements Executor {
        @Override
        public void execute(@NonNull Runnable runnable) {
            Thread t =  new Thread(runnable);
            t.start();
        }
    }

    class TaskRemoteUpdate implements Runnable{

        private Task task;

        public TaskRemoteUpdate(Task task){
            this.task = task;

        }
        @Override
        public void run() {
            addRemote(task);
        }
    }

    private synchronized void addRemote(Task task){

        taskRepository.insertRemote(task, userTokenReposoitory.getLocalUserToken().getToken());
        TaskRepository.removeTask(task);

    }
}
