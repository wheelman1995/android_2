package ru.wheelman.weather;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SearchableActivity extends ListActivity {

    private static final String TAG = "SearchableActivity";

    private ContentLoadingProgressBar progressBar;
    private TextView empty;
    private Task task;
    private ArrayList<City> cityList;
    private int unitIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        initVariables();

        search();
    }

    private void initVariables() {
        progressBar = findViewById(R.id.pb_cities);
        empty = findViewById(android.R.id.empty);
        task = new Task();
        cityList = new ArrayList<>();
        unitIndex = getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());
    }

    private void search() {
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            AsyncTask.Status status = task.getStatus();
            if (status == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }
            task.execute(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        search();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        City selectedCity = cityList.get(position);

        getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, selectedCity.getId()).apply();

        updateWeather(selectedCity.getId());

        finish();
    }

    private void updateWeather(int cityId) {
        OneTimeWorkRequest updateWeatherWork = new OneTimeWorkRequest.Builder(WeatherUpdateWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .setInputData(new Data.Builder()
                        .putInt(Constants.WORK_MANAGER_DATA_CITY_ID, cityId)
                        .putInt(Constants.WORK_MANAGER_DATA_UNITS, unitIndex)
                        .build())
                .addTag(Constants.WORK_MANAGER_WEATHER_ONE_TIME_UPDATE_TAG)
                .build();
        WorkManager.getInstance().beginUniqueWork(Constants.WORK_MANAGER_WEATHER_ONE_TIME_UPDATE_TAG, ExistingWorkPolicy.REPLACE, updateWeatherWork).enqueue();
    }

    private class Task extends AsyncTask<String, Integer, ArrayList<City>> {

        @Override
        protected ArrayList<City> doInBackground(String... strings) {
            cityList.clear();

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader((getResources().openRawResource(R.raw.city_list))));

            try {
                City[] city = gson.fromJson(jsonReader, City[].class);
                int percent = city.length / 100;
                int done = 0;
                for (int i = 0; i < city.length; i++) {
                    if (city[i].getName().toLowerCase().startsWith(strings[0].toLowerCase())) {
                        cityList.add(city[i]);
                    }
                    done = i / percent;
                    if (done * percent == i) {
                        publishProgress(done);
                    }
                }

                jsonReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cityList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<City> cities) {
            progressBar.hide();
            String[] data = new String[cities.size()];
            for (int i = 0; i < cities.size(); i++) {
                data[i] = String.format(Locale.UK, "%s, %s", cities.get(i).getName(), cities.get(i).getCountry());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchableActivity.this, R.layout.activity_searchable_adapter_item, R.id.search_adapter_item, data);
//            adapter.sort(new Comparator<String>() {
//                @Override
//                public int compare(String o1, String o2) {
//                    return Collator.getInstance().compare(o1, o2);
//                }
//            });
            setListAdapter(adapter);
            if (data.length == 0) {
                empty.setText(getString(R.string.no_data_available));
                empty.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }
}
