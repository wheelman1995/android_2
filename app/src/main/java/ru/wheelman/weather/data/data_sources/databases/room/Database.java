package ru.wheelman.weather.data.data_sources.databases.room;

import android.content.Context;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import ru.wheelman.weather.data.data_sources.databases.CurrentWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.databases.ForecastedWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.databases.room.dao.CurrentWeatherDAO;
import ru.wheelman.weather.data.data_sources.databases.room.dao.ForecastedWeatherDAO;
import ru.wheelman.weather.data.data_sources.databases.room.entities.CurrentWeather;
import ru.wheelman.weather.data.data_sources.databases.room.entities.ForecastedWeather;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import toothpick.Scope;
import toothpick.Toothpick;


@androidx.room.Database(entities = {CurrentWeather.class, ForecastedWeather.class}, version = 5, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class Database extends RoomDatabase implements CurrentWeatherLocalDataSource, ForecastedWeatherLocalDataSource {
    private static final String DATABASE_NAME = "weather-data";
    private static volatile Database instance;

    @Inject
    PreferenceHelper preferenceHelper;

    //    private static final Migration migration_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.beginTransaction();
//
//            database.execSQL("alter table WeatherData rename to WeatherDataOld;");
//            database.execSQL("create table WeatherData (id INTEGER primary key, temperature TEXT, dt INTEGER, city TEXT, country TEXT);");
//            database.execSQL("insert into WeatherData (id, temperature, dt, city, country) select id, temperature, dt, city, country from WeatherDataOld;");
//            database.execSQL("drop table WeatherDataOld;");
//
//            database.setTransactionSuccessful();
//            database.endTransaction();
//        }
//    };
//
//    private static final Migration migration_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            database.execSQL("alter table WeatherData add column sunset integer;");
//            database.execSQL("alter table WeatherData add column sunrise integer;");
//
//        }
//    };
//
//    private static final Migration migration_3_4 = new Migration(3, 4) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            database.execSQL("alter table WeatherData add column weather_id text;");
//            database.execSQL("alter table WeatherData add column weather_main text;");
//            database.execSQL("alter table WeatherData add column weather_description text;");
//            database.execSQL("alter table WeatherData add column weather_icon text;");
//
//        }
//    };
//
//    private static final Migration migration_4_5 = new Migration(4, 5) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            database.execSQL("create table ForecastedWeatherData (id integer primary key, json_data text);");
//
//        }
//    };

    public static Database getDatabase(Context context) {
        Database localInstance = instance;

        if (localInstance == null) {
            synchronized (Database.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = instance = Room.databaseBuilder(context, Database.class, DATABASE_NAME)
                            .build();
                }
                Scope scope = Toothpick.openScopes(ApplicationScope.class);
                Toothpick.inject(localInstance, scope);
            }
        }

        return localInstance;
    }

    public static synchronized void destroyInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    @Override
    public void saveCurrentWeatherConditions(CurrentWeatherConditions currentWeatherConditions) {
        int cityId = currentWeatherConditions.getCityId();
        currentWeatherDAO().insert(cityId, currentWeatherConditions);
        saveLatestCityId(cityId);
        preferenceHelper.setCurrentWeatherIsBeingUpdated(false);
    }

    @Override
    public LiveData<CurrentWeatherConditions> getCurrentWeatherConditionsByCityId() {
        return currentWeatherDAO().getDataByCityId(preferenceHelper.getLatestCityId());
    }

    @Override
    public LiveData<FiveDayForecast> getFiveDayForecastByCityId() {
        return forecastedWeatherDAO().getForecastedDataByCityId(preferenceHelper.getLatestCityId());
    }

    @Override
    public void saveFiveDayForecast(FiveDayForecast fiveDayForecast) {
        int cityId = fiveDayForecast.getCityId();
        forecastedWeatherDAO().insert(cityId, fiveDayForecast);
        saveLatestCityId(cityId);
        preferenceHelper.setForecastedWeatherIsBeingUpdated(false);
    }

    private void saveLatestCityId(int latestCityId) {
        preferenceHelper.setLatestCityId(latestCityId);
    }

    protected abstract CurrentWeatherDAO currentWeatherDAO();

    protected abstract ForecastedWeatherDAO forecastedWeatherDAO();
}
