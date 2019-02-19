package ru.wheelman.weather.presentation.data_mappers;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import androidx.databinding.ObservableList;
import ru.wheelman.weather.R;
import ru.wheelman.weather.di.scopes.ForecastedWeatherViewModelScope;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.domain.interactors.DaySplitter;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModelImpl.AdapterViewModel;

@ForecastedWeatherViewModelScope
public class FiveDayForecastDataMapper implements DataMapper<FiveDayForecast, AdapterViewModel> {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEEE, dd MMM", Locale.UK);
    @Inject
    Context context;
    private FiveDayForecast from;
    private AdapterViewModel to;

    @Inject
    public FiveDayForecastDataMapper() {
        SDF.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public void map(FiveDayForecast from, AdapterViewModel to) {
        this.from = from;
        this.to = to;

        fillDates();
        fillWeatherConditionDescriptions();
        fillWeatherIconURLs();
        fillTemperatures();

    }

    private void fillTemperatures() {

        ObservableList<String> dayTemperatures = to.getDayTemperatures();
        ObservableList<String> nightTemperatures = to.getNightTemperatures();
        List<FiveDayForecast.OneDay> days = from.getDays();
        String degree = context.getString(R.string.degree_symbol);

        for (int i = 0; i < days.size(); i++) {

            DaySplitter splitDay = new DaySplitter(days.get(i));

            Float minDay = splitDay.getMinDayTemperature();
            Float maxDay = splitDay.getMaxDayTemperature();

            if (minDay == null && maxDay == null) {
                dayTemperatures.add(null);
            } else if (Objects.equals(minDay, maxDay)) {
                dayTemperatures.add(context.getString(R.string.forecasted_one_value_temperature_template, minDay, degree));
            } else {
                dayTemperatures.add(context.getString(R.string.forecasted_min_max_temperature_template, minDay, maxDay, degree));
            }

            Float minNight = splitDay.getMinNightTemperature();
            Float maxNight = splitDay.getMaxNightTemperature();

            if (minNight == null && maxNight == null) {
                nightTemperatures.add(null);
            } else if (Objects.equals(minNight, maxNight)) {
                nightTemperatures.add(context.getString(R.string.forecasted_one_value_temperature_template, minNight, degree));
            } else {
                nightTemperatures.add(context.getString(R.string.forecasted_min_max_temperature_template, minNight, maxNight, degree));
            }
        }
    }

    private void fillWeatherIconURLs() {
        ObservableList<String> urls = to.getWeatherIconURLs();
        List<FiveDayForecast.OneDay> days = from.getDays();

        for (int i = 0; i < days.size(); i++) {
            urls.add(days.get(i).getWorstWeatherIconURL());
        }

    }

    private void fillWeatherConditionDescriptions() {
        ObservableList<String> descriptions = to.getWeatherConditionDescriptions();
        List<FiveDayForecast.OneDay> days = from.getDays();

        for (int i = 0; i < days.size(); i++) {
            descriptions.add(days.get(i).getWorstWeatherConditionDescription());
        }

    }

    private void fillDates() {
        ObservableList<String> dates = to.getDates();
        List<FiveDayForecast.OneDay> days = from.getDays();

        dates.add(context.getString(R.string.forecast_fragment_today));

        for (int i = 1; i < days.size(); i++) {
            long forecastTime = days.get(i).getDataPieces().get(0).getForecastTime();
            dates.add(SDF.format(forecastTime));
        }
    }
}
