package ru.wheelman.weather;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private ArrayList<City> cityList;
    private int unitIndex;
    private Intent intent;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        initVariables();

        onNewIntent();
    }

    private void loadWeather() {
        int cityId = Integer.parseInt(intent.getDataString());
        onNewCitySelected(cityId);

    }

    private void initVariables() {
        progressBar = findViewById(R.id.pb_cities);
        empty = findViewById(android.R.id.empty);
        cityList = CityListDatabase.getMatchedCities();
        unitIndex = getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());

        adapter = new ArrayAdapter<String>(this, R.layout.activity_searchable_adapter_item, R.id.search_adapter_item);
        setListAdapter(adapter);
    }

    private void onNewIntent() {
        intent = getIntent();

        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                search();
                break;
            case Intent.ACTION_VIEW:
                loadWeather();
                break;
        }
    }

    private void search() {

        String query = intent.getStringExtra(SearchManager.QUERY);

        CityListDatabase.getInstance().findCitiesBeginningWith(new CityListDatabase.JobProgressListener() {


            @Override
            public <T> void onJobDone(T t) {
//                cityList = (ArrayList<City>) t;

                progressBar.hide();

//                String[] data = new String[cityList.size()];

//                for (int i = 0; i < cityList.size(); i++) {
//                    data[i] = String.format(Locale.UK, "%s, %s", cityList.get(i).getName(), cityList.get(i).getCountry());
//                }


                if (adapter.getCount() == 0) {
                    empty.setText(getString(R.string.no_data_available));
                    empty.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public <T> void onProgressUpdate(int progress) {
                progressBar.setProgress(progress);
                Log.d(TAG, String.valueOf(cityList.size()));

                for (int i = adapter.getCount(); i < cityList.size(); i++) {
                    adapter.add(String.format(Locale.UK, "%s, %s", cityList.get(i).getName(), cityList.get(i).getCountry()));
                }
            }
        }, query);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onNewIntent();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        City selectedCity = cityList.get(position);
        int cityId = selectedCity.getId();
        onNewCitySelected(cityId);
    }

    private void onNewCitySelected(int cityId) {
        getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, cityId).apply();

        updateWeather(cityId);

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

//    private class Task extends AsyncTask<String, Integer, ArrayList<City>> {
//
//        @Override
//        protected ArrayList<City> doInBackground(String... strings) {
//            cityList.clear();
//
//            Gson gson = new Gson();
//            JsonReader jsonReader = new JsonReader(new InputStreamReader((getResources().openRawResource(R.raw.city_list))));
//
//            try {
//                City[] city = gson.fromJson(jsonReader, City[].class);
//                int percent = city.length / 100;
//                int done = 0;
//                for (int i = 0; i < city.length; i++) {
//                    if (city[i].getName().toLowerCase().startsWith(strings[0].toLowerCase())) {
//                        cityList.add(city[i]);
//                    }
//                    done = i / percent;
//                    if (done * percent == i) {
//                        publishProgress(done);
//                    }
//                }
//
//                jsonReader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return cityList;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            progressBar.setProgress(values[0]);
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<City> cities) {
//            progressBar.hide();
//            String[] data = new String[cities.size()];
//            for (int i = 0; i < cities.size(); i++) {
//                data[i] = String.format(Locale.UK, "%s, %s", cities.get(i).getName(), cities.get(i).getCountry());
//            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchableActivity.this, R.layout.activity_searchable_adapter_item, R.id.search_adapter_item, data);
////            adapter.sort(new Comparator<String>() {
////                @Override
////                public int compare(String o1, String o2) {
////                    return Collator.getInstance().compare(o1, o2);
////                }
////            });
//            setListAdapter(adapter);
//            if (data.length == 0) {
//                empty.setText(getString(R.string.no_data_available));
//                empty.setTextColor(getResources().getColor(R.color.red));
//            }
//        }
//    }
}
