package com.example.segev.traveler.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.example.segev.traveler.MyApplication;

@Database(entities = {Post.class}, version = 2,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppLocalDb extends RoomDatabase {

    private static final String LOG_TAG = AppLocalDb.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "posts";

    private static AppLocalDb sInstance;

    public static AppLocalDb getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG,"Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppLocalDb.class,AppLocalDb.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG,"Getting the database instance");
        return sInstance;
    }

    public abstract PostDao postDao();
}