package com.example.bogdan.tasklocalstorage.persistance;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.domain.UserToken;

@Database(entities = {Task.class, UserToken.class}, version = 1)
public abstract class DatabaseCreator extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract UserTokenDao userTokenDao();

    private static volatile DatabaseCreator INSTANCE = null;

    public static DatabaseCreator getDatabase(final Context context){

        if (INSTANCE == null) {
            synchronized (DatabaseCreator.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseCreator.class, "word_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;

    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TaskDao mTaskDao;
        private final UserTokenDao mUserTokenDao;

        PopulateDbAsync(DatabaseCreator db) {

            mTaskDao = db.taskDao();
            mUserTokenDao = db.userTokenDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mUserTokenDao.nukeTable();
            mTaskDao.nukeTable();
//            mDao.deleteAll();
//            Task task = new Task("LFTC", 1);
//            mDao.insert(task);
//            task = new Task("PM", 2);
//            mDao.insert(task);
            return null;
        }
    }

}
