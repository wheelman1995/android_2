package ru.wheelman.weather;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {WeatherData.class, ForecastedWeatherData.class}, version = 5, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database INSTANCE;
    public static final String DATABASE_NAME = "weather-data";

    private static final Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.beginTransaction();

            database.execSQL("alter table WeatherData rename to WeatherDataOld;");
            database.execSQL("create table WeatherData (id INTEGER primary key unique not null, temperature TEXT, dt INTEGER, city TEXT, country TEXT);");
            database.execSQL("insert into WeatherData (id, temperature, dt, city, country) select id, temperature, dt, city, country from WeatherDataOld;");
            database.execSQL("drop table WeatherDataOld;");

            database.setTransactionSuccessful();
            database.endTransaction();
        }
    };

    private static final Migration migration_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("alter table WeatherData add column sunset integer;");
            database.execSQL("alter table WeatherData add column sunrise integer;");
            database.execSQL("create index WeatherData_id_index on WeatherData (id);");

        }
    };

    private static final Migration migration_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table WeatherData add column weather_id text;");
            database.execSQL("alter table WeatherData add column weather_main text;");
            database.execSQL("alter table WeatherData add column weather_description text;");
            database.execSQL("alter table WeatherData add column weather_icon text;");

        }
    };

    private static final Migration migration_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("create table ForecastedWeatherData (id integer primary key not null, json_data text);");
            database.execSQL("create index ForecastedWeatherData_id_index on ForecastedWeatherData (id);");

        }
    };

    public static Database getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, DATABASE_NAME)
                    .addMigrations(migration_1_2, migration_2_3, migration_3_4, migration_4_5)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    public abstract WeatherDataDAO weatherDataDAO();

    public abstract ForecastedWeatherDataDAO forecastedWeatherDataDAO();
}
