package ru.wheelman.weather;

import android.app.Application;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CityListDatabase {
    private static final String FIND_CITIES_BEGINNING_WITH = "find cities by name";
    private static final CityListDatabase ourInstance = new CityListDatabase();
    private static Application app;
    private static boolean initialized;
    private static City[] cities;
    private static ArrayList<City> matchedCities;

    private CityListDatabase() {
        matchedCities = new ArrayList<>();
    }

    static ArrayList<City> getMatchedCities() {
        return matchedCities;
    }

    static void init(Application application) {
        app = application;
    }

    public static CityListDatabase getInstance() {
        return ourInstance;
    }

    private static void initialize() {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader((app.getResources().openRawResource(R.raw.city_list))));

        try {
            cities = gson.fromJson(jsonReader, City[].class);
            jsonReader.close();
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void findCitiesBeginningWith(JobProgressListener jobProgressListener, String query) {

        DatabaseTask databaseTask = new DatabaseTask();
        DatabaseTask.setJobProgressListener(jobProgressListener);
        databaseTask.execute(FIND_CITIES_BEGINNING_WITH, query);
    }

    ArrayList<City> findCitiesBeginningWith(String query) {
        if (!initialized) {
            initialize();
        }
        ArrayList<City> cityArrayList = new ArrayList<>();

        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getName().toLowerCase().startsWith(query.toLowerCase())) {
                cityArrayList.add(cities[i]);
            }
        }
        return cityArrayList;
    }

    public interface JobProgressListener {
        <T> void onJobDone(T t);

        <T> void onProgressUpdate(int progress);
    }

    private static class DatabaseTask extends AsyncTask<String, Integer, Object> {

        private static JobProgressListener jobProgressListener;

        static void setJobProgressListener(JobProgressListener jobProgressListener) {
            DatabaseTask.jobProgressListener = jobProgressListener;
        }

        @Override
        protected Object doInBackground(String... strings) {

            Object result = null;

            if (!initialized) {
                CityListDatabase.initialize();
            }

            switch (strings[0]) {
                case FIND_CITIES_BEGINNING_WITH:
                    matchedCities.clear();
                    String query = strings[1];
                    int percent = cities.length / 100;
                    int doneInPercents;
                    for (int i = 0; i < cities.length; i++) {
                        doneInPercents = i / percent;
                        if (cities[i].getName().toLowerCase().startsWith(query.toLowerCase())) {
                            matchedCities.add(cities[i]);
                            publishProgress(doneInPercents);
                            continue;
                        }
                        if (doneInPercents * percent == i) {
                            publishProgress(doneInPercents);
                        }
                    }
                    result = matchedCities;
                    break;
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            jobProgressListener.onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            jobProgressListener.onJobDone(o);
        }


    }
}
