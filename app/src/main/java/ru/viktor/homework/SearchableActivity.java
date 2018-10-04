package ru.viktor.homework;

import android.app.ListActivity;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class SearchableActivity extends ListActivity {

    private static final String TAG = "SearchableActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //todo: show a list of available cities to the user

//            setListAdapter(adapter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //todo: update weather data on city selected.
    }

    private void updateWeather(String city) {
        OneTimeWorkRequest updateWeatherWork = new OneTimeWorkRequest.Builder(WeatherUpdateWorker.class)
                .setInputData(new Data.Builder().putString("city", city).build())
                .build();

        WorkManager.getInstance().enqueue(updateWeatherWork);
    }
}
