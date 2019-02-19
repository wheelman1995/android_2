package ru.wheelman.weather.domain.interactors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.wheelman.weather.domain.entities.FiveDayForecast;

public class DaySplitter {
    private final FiveDayForecast.OneDay day;
    private List<FiveDayForecast.OneDay.DataPiece> dayDataPieces;
    private List<FiveDayForecast.OneDay.DataPiece> nightDataPieces;
    private Float minDayTemperature;
    private Float minNightTemperature;
    private Float maxDayTemperature;
    private Float maxNightTemperature;

    public DaySplitter(FiveDayForecast.OneDay day) {
        this.day = day;

        dayDataPieces = new ArrayList<>();
        nightDataPieces = new ArrayList<>();

        splitDay();

        calculateMinMaxTemperatures();
    }


    /**
     * @return minimal day temperature or <code>null</code>, if there is no data up to the sunset.
     */
    public Float getMinDayTemperature() {
        return minDayTemperature;
    }

    /**
     * @return minimal night temperature or <code>null</code>, if there is no data up to the sunrise.
     */

    public Float getMinNightTemperature() {
        return minNightTemperature;
    }

    /**
     * @return maximal day temperature or <code>null</code>, if there is no data up to the sunset.
     */

    public Float getMaxDayTemperature() {
        return maxDayTemperature;
    }

    /**
     * @return maximal night temperature or <code>null</code>, if there is no data up to the sunrise.
     */

    public Float getMaxNightTemperature() {
        return maxNightTemperature;
    }

    public List<FiveDayForecast.OneDay.DataPiece> getDayDataPieces() {
        return dayDataPieces;
    }

    public List<FiveDayForecast.OneDay.DataPiece> getNightDataPieces() {
        return nightDataPieces;
    }

    private void calculateMinMaxTemperatures() {
        if (!dayDataPieces.isEmpty()) {
            minDayTemperature = Collections.min(dayDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
            maxDayTemperature = Collections.max(dayDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
        }

        if (!nightDataPieces.isEmpty()) {
            minNightTemperature = Collections.min(nightDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
            maxNightTemperature = Collections.max(nightDataPieces, (o1, o2) -> Float.compare(o1.getTemperature(), o2.getTemperature())).getTemperature();
        }
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
