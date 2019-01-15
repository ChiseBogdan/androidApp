package com.example.bogdan.tasklocalstorage.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.bogdan.tasklocalstorage.api.PingResource;
import com.example.bogdan.tasklocalstorage.api.TaskResource;
import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.persistance.DatabaseCreator;
import com.example.bogdan.tasklocalstorage.persistance.TaskDao;
import com.example.bogdan.tasklocalstorage.update.ServiceUpdate;
import com.example.bogdan.tasklocalstorage.viewmodel.ServiceAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskRepository {

    private static final String TAG = TaskRepository.class.getCanonicalName();

    private Retrofit retrofit;

    private static PingResource pingApi = null;
    private static TaskResource taskResourceAPI;
    private String currentEtag;

    private static List<Task> tasksToBeAdded;


    private ServiceUpdate serviceUpdate;


    private static TaskDao mTaskDao;
    private static Application application = null;

    private static TaskRepository INSTANCE = null;

    public static TaskRepository getInstance(){

        if (INSTANCE == null){

            INSTANCE = new TaskRepository();
        }

        return INSTANCE;
    }

    public static PingResource getPingApi(){

        if(pingApi == null){

            Retrofit retrofit = ServiceAPI.getInstance();
            pingApi = retrofit.create(PingResource.class);

        }
        return pingApi;

    }

    public static TaskResource getTaskResourceAPI(){

        if(taskResourceAPI == null){

            Retrofit retrofit = ServiceAPI.getInstance();
            taskResourceAPI = retrofit.create(TaskResource.class);

        }
        return taskResourceAPI;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init(Application application){
        if(this.application == null){

            this.application = application;
            DatabaseCreator db = DatabaseCreator.getDatabase(application);
            mTaskDao = db.taskDao();

            serviceUpdate = new ServiceUpdate();
            tasksToBeAdded = new ArrayList<>();
        }

        retrofit = ServiceAPI.getInstance();

        pingApi = getPingApi();
        taskResourceAPI = getTaskResourceAPI();

    }

    public static synchronized List<Task> getAllTasksToBeAdded(){
        return tasksToBeAdded;
    }

    public static synchronized void removeTask(Task task){
        tasksToBeAdded.remove(task);
    }

    public List<Task> getAllLocalTasks(){

        List<Task> allLocalTasks = null;

        AsyncTask<Void, Void, List<Task>> getAllLocalTasksAsync = new GetAllLocalTasksAsync(mTaskDao);
        try {
            allLocalTasks = getAllLocalTasksAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return allLocalTasks;

    }

    public static boolean checkServerConnection(){

        int code=0;
        boolean res = false;

        try {
            code = pingApi.checkConnection().execute().code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(code == 200){
            res  =  true;
        }

        return res;
//        String address = "10.1.32.194:8080";
//        int port = 53, timeoutMs = 1000;
//
//        try {
//            Socket sock = new Socket();
//            SocketAddress sockaddr = new InetSocketAddress(address, port);
//
//            sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
//            sock.close();
//
//            return true;
//
//        } catch (IOException e) { return false; }
    }

    public void nukeTasksTable(){
        new NukeTasksTable(mTaskDao).execute();
    }

    private class NukeTasksTable extends AsyncTask<Void, Void, Void>{

        private TaskDao taskDao;

        NukeTasksTable(TaskDao taskDao){
            this.taskDao = taskDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            taskDao.nukeTable();

            return null;
        }
    }

    public void insertRemote(Task task, String userToken){

        new InsertRemoteWhenOnlineAsync(userToken).execute(task);
    }

    private static class InsertRemoteWhenOnlineAsync extends AsyncTask<Task, Void, Void> {

        private String userToken;

        InsertRemoteWhenOnlineAsync(String userToken){
            this.userToken = userToken;
        }

        @Override
        protected Void doInBackground(Task... tasks) {

            if (checkServerConnection()) {

                TaskResource api = getTaskResourceAPI();

                Call<ResponseBody> call = api.insertTask(userToken, tasks[0]);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.d(TAG, "insert succeeded");
                        Headers headers = response.headers();

                        System.out.println(headers);

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Log.e(TAG, "insert failed", t);
                    }
                });


            }
            else {
                tasksToBeAdded.add(tasks[0]);
            }
            return null;
        }
    }

    public List<Task> getAllRemoteTasks(String userToken){

        List<Task> allTasks = null;

        AsyncTask<Void, Void, List<Task>> getAllRemoteTasks = new GetAllTasksRemote(userToken);

        try {
            allTasks =  getAllRemoteTasks.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return allTasks;
    }

    private class GetAllTasksRemote extends AsyncTask<Void, Void, List<Task>>{

        private String userToken;

        GetAllTasksRemote(String userToken){
            this.userToken = userToken;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {

            Call<List<Task>> GETallTasksFromRemote = taskResourceAPI.getAllTasks(userToken, getCurrentEtag());

            try {
                Response<List<Task>> response =  GETallTasksFromRemote.execute();

                int statusCode = response.code();

                Headers headers =  response.headers();

                if(statusCode == 200){

                    List<Task> mResponseAllTasks = response.body();

                    String ETag = headers.get("ETag");

                    setCurrentEtag(ETag);

                    return mResponseAllTasks;
                }




            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

//    private boolean findTaskInListOfTasks(List<Task> listOfTasks, Task task){
//        for(Task actualTask: listOfTasks){
//            if(actualTask.getId() == task.getId()){
//                return true;
//            }
//        }
//        return false;
//    }

//    private void syncLocalWithRemote(List<Task> mResponseAllTasks) {
//
//        AsyncTask<Void, Void, List<Task>> getAllLocalTasks = new GetAllLocalTasksAsync(mTaskDao);
//
//        try {
//            List<Task> allLocalTasks = getAllLocalTasks.execute().get();
//            for(Task task: allLocalTasks){
//                if(findTaskInListOfTasks(task) == false){
//                    insert(task);
//                }
//            }
//
//
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public void insertLocal(Task task){

        new insertAsyncTask(mTaskDao).execute(task);
    }

    private static class GetAllLocalTasksAsync extends AsyncTask<Void, Void, List<Task>> {

        private TaskDao mTaskDao;

        GetAllLocalTasksAsync(TaskDao dao) {
            mTaskDao = dao;
        }

        @Override
        protected List<Task> doInBackground(Void... params) {
            List<Task> allLocalTasks = mTaskDao.getAllTasks();
            return allLocalTasks;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao mAsyncTaskDao;

        insertAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public String getCurrentEtag() {
        return currentEtag;
    }

    public void setCurrentEtag(String currentEtag) {
        this.currentEtag = currentEtag;
    }



}
