package ru.wheelman.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class ForecastFragment extends Fragment {
    public static final int ID_FOR_VIEW_PAGER = 1;
    private static final String TAG = ForecastFragment.class.getSimpleName();
    private WeatherViewModel weatherViewModel;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private int cityId;
    private Observer<ForecastedWeatherData> weatherDataObserver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BroadcastReceiver dataUpdateStatusReceiver;
    private LiveData<ForecastedWeatherData> liveForecastedWeatherData;

    private RecyclerView recyclerView;
    private ForecastRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<String> dates;
    private ArrayList<String> weatherConditionDescriptions;
    private ArrayList<String> maxDayTemperatures;
    private ArrayList<String> minNightTemperatures;
    private ArrayList<String> iconURLs;

    public static Fragment newInstance() {

        return new ForecastFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        initVariables(view);

        initListeners();

        subscribeForDataUpdates();

        return view;
    }

    private void initListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).getMainFragment().refreshManually();
            }
        });

        dataUpdateStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataUpdateStatusReceiver, new IntentFilter(WeatherUpdateWorker.ACTION_NEW_DATA_RECEIVED));

        weatherDataObserver = new Observer<ForecastedWeatherData>() {

            private SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM", Locale.UK);
            private Gson gson = new Gson();

            @Override
            public void onChanged(ForecastedWeatherData forecastedWeatherData) {
                if (forecastedWeatherData != null) {

                    FiveDayForecast fiveDayForecast = gson.fromJson(forecastedWeatherData.getJsonData(), FiveDayForecast.class);

                    clearLists();

                    fillDates(fiveDayForecast);

                    fillWeatherConditionDescriptions(fiveDayForecast);

                    fillMaxDayTemperatures(fiveDayForecast);

                    fillMinNightTemperatures(fiveDayForecast);

                    fillIconURLs(fiveDayForecast);

                    if (recyclerViewAdapter == null)
                        recyclerViewAdapter = new ForecastRecyclerViewAdapter(dates, weatherConditionDescriptions, maxDayTemperatures, minNightTemperatures, iconURLs);
                    else
                        recyclerViewAdapter.notifyDataSetChanged();

                    if (recyclerView.getAdapter() == null)
                        recyclerView.setAdapter(recyclerViewAdapter);

                }
            }

            private void fillIconURLs(FiveDayForecast fiveDayForecast) {
                for (int i = 0; i < fiveDayForecast.getIcons().length; i++) {
                    iconURLs.add(String.format(Locale.UK, "%s%s.png", Constants.WEATHER_ICON_URL_BASE, fiveDayForecast.getIcons()[i]));
                }
            }

            private void fillMinNightTemperatures(FiveDayForecast fiveDayForecast) {
                for (int i = 0; i < fiveDayForecast.getMinNightTemperatures().length; i++) {
                    minNightTemperatures.add(String.format(Locale.UK, "%.1f%s", fiveDayForecast.getMinNightTemperatures()[i], getString(R.string.degree_symbol)));
                }
            }

            private void fillMaxDayTemperatures(FiveDayForecast fiveDayForecast) {
                for (int i = 0; i < fiveDayForecast.getMaxDayTemperatures().length; i++) {
                    maxDayTemperatures.add(String.format(Locale.UK, "%.1f%s", fiveDayForecast.getMaxDayTemperatures()[i], getString(R.string.degree_symbol)));
                }
            }

            private void fillWeatherConditionDescriptions(FiveDayForecast fiveDayForecast) {
                weatherConditionDescriptions.addAll(Arrays.asList(fiveDayForecast.getWeatherConditionDescriptions()));
            }

            private void fillDates(FiveDayForecast fiveDayForecast) {
                sdf.setTimeZone(TimeZone.getDefault());
                dates.add("Today");
                for (int i = 1; i < fiveDayForecast.getDates().length; i++) {
                    Date date = new Date(fiveDayForecast.getDates()[i] * 1000L);
                    dates.add(sdf.format(date));
                }
            }

            private void clearLists() {
                dates.clear();
                weatherConditionDescriptions.clear();
                maxDayTemperatures.clear();
                minNightTemperatures.clear();
                iconURLs.clear();
            }
        };

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case Constants.SHARED_PREFERENCES_CURRENT_CITY_ID:
                        cityId = sharedPreferences.getInt(key, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
                        onNewCitySelected();
                        break;
                }
            }
        };

        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private void onNewCitySelected() {
        subscribeForDataUpdates();
    }

    private void subscribeForDataUpdates() {
        if (liveForecastedWeatherData != null) {
            if (liveForecastedWeatherData.hasObservers()) {
                liveForecastedWeatherData.removeObservers(this);
            }
        }
        liveForecastedWeatherData = weatherViewModel.getForecastWeatherData(cityId);
        liveForecastedWeatherData.observe(this, weatherDataObserver);
    }

    private void initVariables(View view) {

        cityId = getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, SearchSuggestionsProvider.CURRENT_LOCATION_SUGGESTION_ID);
        recyclerView = view.findViewById(R.id.rv_fragment_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.srl_fragment_forecast);


        weatherConditionDescriptions = new ArrayList<>();
        dates = new ArrayList<>();
        maxDayTemperatures = new ArrayList<>();
        minNightTemperatures = new ArrayList<>();
        iconURLs = new ArrayList<>();
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataUpdateStatusReceiver);
        getActivity().getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onDetach();
    }
}
