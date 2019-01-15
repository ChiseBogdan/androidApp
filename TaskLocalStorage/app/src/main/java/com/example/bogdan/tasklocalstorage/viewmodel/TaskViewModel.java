package com.example.bogdan.tasklocalstorage.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.repository.TaskRepository;
import com.example.bogdan.tasklocalstorage.repository.UserTokenRepository;
import com.example.bogdan.tasklocalstorage.websockets.SpringBootWebSocketClient;
import com.example.bogdan.tasklocalstorage.websockets.StompMessage;
import com.example.bogdan.tasklocalstorage.websockets.StompMessageListener;
import com.example.bogdan.tasklocalstorage.websockets.TopicHandler;

import java.util.Collections;
import java.util.List;


public class TaskViewModel extends AndroidViewModel {


    private TaskRepository mTaskRepository;
    private UserTokenRepository mUserTokenRepository;

    private MutableLiveData<List<Task>> mAllTasks = new MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TaskViewModel(@NonNull Application application) {
        super(application);

        // reposotiry initalization
        repositoryInitialization(application);

        initialPopulation();
//        mTaskRepository.getmAllTaskRemote(mUserTokenRepository.getUserTokenOfLoggedUser());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void repositoryInitialization(Application application) {

        mTaskRepository = TaskRepository.getInstance();

        mTaskRepository.init(application);

        mUserTokenRepository = UserTokenRepository.getInstance();

        mUserTokenRepository.init(application);
    }

    public void startListeningOnSockets(){
        SpringBootWebSocketClient client = new SpringBootWebSocketClient();
        TopicHandler handler = client.subscribe("/topics/event");
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {

                nukeTasksTable();
                initialPopulation();

            }
        });
        client.connect("ws://172.30.116.55:8080/my-ws/websocket");
    }

    private void nukeTasksTable(){
        mTaskRepository.nukeTasksTable();
    }


    private void initialPopulation() {

        List<Task> allRemoteTasks =  mTaskRepository.getAllRemoteTasks(mUserTokenRepository.getLocalUserToken().getToken());

        if(allRemoteTasks !=null){

            initialLocalAdd(allRemoteTasks);

            updateUIOnBackgroundThread(allRemoteTasks);
        }

    }

    private void initialLocalAdd(List<Task> allRemoteTasks) {

        for(Task task: allRemoteTasks){
            mTaskRepository.insertLocal(task);
        }
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public void shuffleTasks(){
        List<Task> currentTasks = mAllTasks.getValue();
        Collections.shuffle(currentTasks);
        updateUIOnMainThread(currentTasks);

    }

    public void insert(Task task) {

        mTaskRepository.insertLocal(task);
        mTaskRepository.insertRemote(task, mUserTokenRepository.getLocalUserToken().getToken());

        updateUIOnMainThread();

    }

    private void updateUIOnMainThread(List<Task> tasks) {

        mAllTasks.setValue(tasks);
    }

    private void updateUIOnMainThread() {

        List<Task> acutalTasks = mTaskRepository.getAllLocalTasks();
        mAllTasks.setValue(acutalTasks);
    }

    private void updateUIOnBackgroundThread() {

        List<Task> acutalTasks = mTaskRepository.getAllLocalTasks();
        mAllTasks.postValue(acutalTasks);
    }

    private void updateUIOnBackgroundThread(List<Task> tasks) {

        mAllTasks.postValue(tasks);
    }

}
