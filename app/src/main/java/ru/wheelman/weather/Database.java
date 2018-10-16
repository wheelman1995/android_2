package ru.wheelman.weather;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {WeatherData.class}, version = 2, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database INSTANCE;
    public static final String DATABASE_NAME = "weather-data";

    private static final Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.beginTransaction();

            database.execSQL("alter table WeatherData rename to WeatherDataOld;");
            database.execSQL("create table WeatherData (id INTEGER primary key unique not null, temperature TEXT, dt INTEGER NOT NULL, city TEXT, country TEXT);");
            database.execSQL("insert into WeatherData (id, temperature, dt, city, country) select id, temperature, dt, city, country from WeatherDataOld;");
            database.execSQL("drop table WeatherDataOld;");

            database.setTransactionSuccessful();
            database.endTransaction();
        }
    };

    public static Database getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, DATABASE_NAME)
                    .addMigrations(migration_1_2)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE.close();
        INSTANCE = null;
    }

    public abstract WeatherDataDAO weatherDataDAO();
}
