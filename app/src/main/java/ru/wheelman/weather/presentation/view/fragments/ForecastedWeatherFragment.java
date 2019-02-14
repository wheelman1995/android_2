package ru.wheelman.weather.presentation.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import ru.wheelman.weather.R;
import ru.wheelman.weather.databinding.FragmentForecastBinding;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModel;


public class ForecastedWeatherFragment extends Fragment {
    public static final int ID_FOR_VIEW_PAGER = 1;
    private static final String TAG = ForecastedWeatherFragment.class.getSimpleName();
    @Inject
    ForecastedWeatherViewModel viewModel;

    private RecyclerView recyclerView;
    private ForecastedWeatherFragmentAdapter recyclerViewAdapter;

    private ArrayList<String> dates;
    private ArrayList<String> weatherConditionDescriptions;
    private ArrayList<String> dayTemperatures;
    private ArrayList<String> nightTemperatures;
    private ArrayList<String> iconURLs;

    public static ForecastedWeatherFragment newInstance() {

        return new ForecastedWeatherFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentForecastBinding binding = FragmentForecastBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
        binding.setState(viewModel.getScreenState());

        View root = binding.getRoot();

        initUi(root);

        initListeners();

        return root;
    }

    private void initListeners() {

//        weatherDataObserver = new Observer<ForecastedWeatherData>() {
//
//            private FiveDayForecast fiveDayForecast;
//            private SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM", Locale.UK);
//            private Gson gson = new Gson();
//
//            @Override
//            public void onChanged(ForecastedWeatherData forecastedWeatherData) {
//                if (forecastedWeatherData != null) {
//
//                    fiveDayForecast = gson.fromJson(forecastedWeatherData.getJsonData(), FiveDayForecast.class);
//
//                    clearLists();
//
//                    fillDates();
//
//                    fillWeatherConditionDescriptions();
//
//                    fillDayTemperatures();
//
//                    fillNightTemperatures();
//
//                    fillIconURLs();
//
//                    if (recyclerViewAdapter == null)
//                        recyclerViewAdapter = new ForecastedWeatherFragmentAdapter(dates, weatherConditionDescriptions, dayTemperatures, nightTemperatures, iconURLs);
//                    else
//                        recyclerViewAdapter.notifyDataSetChanged();
//
//                    if (recyclerView.getAdapter() == null)
//                        recyclerView.setAdapter(recyclerViewAdapter);
//
//                }
//            }
//
//            private void fillIconURLs() {
//                for (int i = 0; i < fiveDayForecast.getIcons().length; i++) {
//                    iconURLs.add(String.format(Locale.UK, "%s%s.png", Constants.WEATHER_ICON_URL_BASE, fiveDayForecast.getIcons()[i]));
//                }
//            }
//
//            private void fillNightTemperatures() {
//                for (int i = 0; i < fiveDayForecast.getNightTemperatures().length; i++) {
//                    if (fiveDayForecast.getNightTemperatures()[i][0] == Constants.INVALID_TEMPERATURE) {
//                        nightTemperatures.add("");
//                        continue;
//                    }
//                    if (fiveDayForecast.getNightTemperatures()[i][0] == fiveDayForecast.getNightTemperatures()[i][1]) {
//                        nightTemperatures.add(String.format(Locale.UK, "%.1f%s", fiveDayForecast.getNightTemperatures()[i][0], getString(R.string.degree_symbol)));
//                        continue;
//                    }
//                    nightTemperatures.add(String.format(Locale.UK, "%.1f/%.1f%s", fiveDayForecast.getNightTemperatures()[i][0], fiveDayForecast.getNightTemperatures()[i][1], getString(R.string.degree_symbol)));
//                }
//            }
//
//            private void fillDayTemperatures() {
//                for (int i = 0; i < fiveDayForecast.getDayTemperatures().length; i++) {
//                    Log.d(TAG, "0 " + String.valueOf(fiveDayForecast.getDayTemperatures()[i][0]));
//                    Log.d(TAG, "1 " + String.valueOf(fiveDayForecast.getDayTemperatures()[i][1]));
//                    if (fiveDayForecast.getDayTemperatures()[i][0] == Constants.INVALID_TEMPERATURE) {
//                        dayTemperatures.add("");
//                        continue;
//                    }
//                    if (fiveDayForecast.getDayTemperatures()[i][0] == fiveDayForecast.getDayTemperatures()[i][1]) {
//                        dayTemperatures.add(String.format(Locale.UK, "%.1f%s", fiveDayForecast.getDayTemperatures()[i][0], getString(R.string.degree_symbol)));
//                        continue;
//                    }
//                    dayTemperatures.add(String.format(Locale.UK, "%.1f/%.1f%s", fiveDayForecast.getDayTemperatures()[i][0], fiveDayForecast.getDayTemperatures()[i][1], getString(R.string.degree_symbol)));
//                }
//            }
//
//            private void fillWeatherConditionDescriptions() {
//                weatherConditionDescriptions.addAll(Arrays.asList(fiveDayForecast.getWeatherConditionDescriptions()));
//            }
//
//            private void fillDates() {
//                sdf.setTimeZone(TimeZone.getDefault());
//                dates.add(getString(R.string.forecast_fragment_today));
//                for (int i = 1; i < fiveDayForecast.getDates().length; i++) {
//                    Date date = new Date(fiveDayForecast.getDates()[i] * 1000L);
//                    dates.add(sdf.format(date));
//                }
//            }
//
//            private void clearLists() {
//                dates.clear();
//                weatherConditionDescriptions.clear();
//                dayTemperatures.clear();
//                nightTemperatures.clear();
//                iconURLs.clear();
//            }
//        };

    }

    private void initUi(View view) {
        recyclerView = view.findViewById(R.id.rv_fragment_forecast);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        weatherConditionDescriptions = new ArrayList<>();
        dates = new ArrayList<>();
        dayTemperatures = new ArrayList<>();
        nightTemperatures = new ArrayList<>();
        iconURLs = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onStop() {
        viewModel.onStop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
