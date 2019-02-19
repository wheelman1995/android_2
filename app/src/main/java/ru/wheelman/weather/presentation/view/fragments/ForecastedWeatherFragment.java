package ru.wheelman.weather.presentation.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import ru.wheelman.weather.databinding.FragmentForecastBinding;
import ru.wheelman.weather.di.modules.ForecastedWeatherFragmentModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.ForecastedWeatherFragmentScope;
import ru.wheelman.weather.di.scopes.ForecastedWeatherViewModelScope;
import ru.wheelman.weather.presentation.data_binding.IBindingAdapters;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModel;
import toothpick.Scope;
import toothpick.Toothpick;


public class ForecastedWeatherFragment extends Fragment {
    public static final int ID_FOR_VIEW_PAGER = 1;
    private static final String TAG = ForecastedWeatherFragment.class.getSimpleName();
    @Inject
    ForecastedWeatherViewModel viewModel;
    @Inject
    IBindingAdapters bindingComponent;
    @Inject
    ForecastedWeatherFragmentAdapter adapter;
    private FragmentForecastBinding binding;
    private DividerItemDecoration dividerItemDecoration;

    public static ForecastedWeatherFragment newInstance() {

        return new ForecastedWeatherFragment();
    }

    public ForecastedWeatherFragmentAdapter getAdapter() {
        return adapter;
    }

    public DividerItemDecoration getDividerItemDecoration() {
        return dividerItemDecoration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initToothpick();

        binding = FragmentForecastBinding.inflate(inflater, container, false, bindingComponent);

        initVariables();
        initListeners();

        binding.setFragment(this);
        binding.setViewModel(viewModel);
        binding.setState(viewModel.getScreenState());

        return binding.getRoot();
    }

    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, ForecastedWeatherViewModelScope.class, ForecastedWeatherFragmentScope.class);
        scope.installModules(new ForecastedWeatherFragmentModule(this));
        Toothpick.inject(this, scope);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.onViewCreated();
    }

    private void initVariables() {
        dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
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
    public void onDestroyView() {
        adapter.onDestroyView();
        Toothpick.closeScope(ForecastedWeatherFragmentScope.class);
        super.onDestroyView();
    }
}
