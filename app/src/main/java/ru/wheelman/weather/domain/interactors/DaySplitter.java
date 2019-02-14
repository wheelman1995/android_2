package ru.wheelman.weather.domain.interactors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.wheelman.weather.domain.entities.FiveDayForecast;

public class DaySplitter {
    private final FiveDayForecast.OneDay day;
    private List<FiveDayForecast.OneDay.DataPiece> dayDataPieces;
    private List<FiveDayForecast.OneDay.DataPiece> nightDataPieces;
    private float minDayTemperature;
    private float minNightTemperature;
    private float maxDayTemperature;
    private float maxNightTemperature;

    public DaySplitter(FiveDayForecast.OneDay day) {
        this.day = day;

        dayDataPieces = new ArrayList<>();
        nightDataPieces = new ArrayList<>();

        splitDay();

        calculateMinMaxTemperatures();
    }

    public float getMinDayTemperature() {
        return minDayTemperature;
    }

    public float getMinNightTemperature() {
        return minNightTemperature;
    }

    public float getMaxDayTemperature() {
        return maxDayTemperature;
    }

    public float getMaxNightTemperature() {
        return maxNightTemperature;
    }

    public List<FiveDayForecast.OneDay.DataPiece> getDayDataPieces() {
        return dayDataPieces;
    }

    public List<FiveDayForecast.OneDay.DataPiece> getNightDataPieces() {
        return nightDataPieces;
    }

    private void calculateMinMaxTemperatures() {
        minDayTemperature = Collections.min(dayDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
        maxDayTemperature = Collections.max(dayDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();

        minNightTemperature = Collections.min(nightDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
        maxNightTemperature = Collections.min(nightDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
    }

    private void splitDay() {
        long sunrise = day.getSunrise();
        long sunset = day.getSunset();

        for (int i = 0; i < day.getDataPieces().size(); i++) {
            FiveDayForecast.OneDay.DataPiece dataPiece = day.getDataPieces().get(i);
            long forecastTime = dataPiece.getForecastTime();
            if (sunrise <= forecastTime && forecastTime < sunset) {
                dayDataPieces.add(dataPiece);
            } else {
                nightDataPieces.add(dataPiece);
            }
        }
    }


}
