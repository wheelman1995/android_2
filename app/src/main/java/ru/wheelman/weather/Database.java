package ru.wheelman.weather;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {WeatherData.class}, version = 1)
public abstract class Database extends RoomDatabase {
    private static Database INSTANCE;

    public static Database getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "weather-data").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE.close();
        INSTANCE = null;
    }

    public abstract WeatherDataDAO weatherDataDAO();
}
