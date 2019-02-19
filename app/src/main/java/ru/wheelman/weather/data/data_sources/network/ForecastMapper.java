package ru.wheelman.weather.data.data_sources.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.wheelman.weather.data.data_sources.network.forecasted.model.ForecastedWeatherXML;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.domain.entities.Units;

public class ForecastMapper {
    private static final SimpleDateFormat SDF = new SimpleDateFormat(OpenWeatherAPI.XML_RESPONSE_DATE_FORMAT, Locale.UK);

    public FiveDayForecast map(ForecastedWeatherXML forecastedWeatherXML) {

        List<FiveDayForecast.OneDay> days = splitIntoDays(forecastedWeatherXML);


        return new FiveDayForecast()
                .setCityId(forecastedWeatherXML.getLocation().getLocation().getGeobaseid())
                .setCityName(forecastedWeatherXML.getLocation().getCity())
                .setCountry(forecastedWeatherXML.getLocation().getCountry())
                .setLatitude(forecastedWeatherXML.getLocation().getLocation().getLatitude())
                .setLongitude(forecastedWeatherXML.getLocation().getLocation().getLongitude())
                .setDays(days)
                .setUnits(mapUnits(forecastedWeatherXML));

    }

    private Units mapUnits(ForecastedWeatherXML forecastedWeatherXML) {
        String unit = forecastedWeatherXML.getForecast().get(0).getTemperature().getUnit();
        switch (unit) {
            case OpenWeatherAPI.XML_UNIT_CELSIUS:
                return Units.CELSIUS;
            case OpenWeatherAPI.XML_UNIT_FAHRENHEIT:
                return Units.FAHRENHEIT;
            default:
                return null;
        }
    }

    private List<FiveDayForecast.OneDay> splitIntoDays(ForecastedWeatherXML forecastedWeatherXML) {
        try {
            long sunrise = SDF.parse(forecastedWeatherXML.getSun().getRise()).getTime();
            long sunset = SDF.parse(forecastedWeatherXML.getSun().getSet()).getTime();
            long twentyFourHoursInMilliseconds = 86400000L;
            long nextSunrise = sunrise + twentyFourHoursInMilliseconds;

            List<FiveDayForecast.OneDay> days = initDays();
            int indexOfCurrentDay = 0;

            int i = 0;
            while (true) {
                ForecastedWeatherXML.Time dataPiece = forecastedWeatherXML.getForecast().get(i);
                boolean lastPiece = i == forecastedWeatherXML.getForecast().size() - 1;
                boolean firstPiece = i == 0;


                long forecastTime = SDF.parse(dataPiece.getFrom()).getTime();

                if (forecastTime < nextSunrise) {
                    days.get(indexOfCurrentDay).getDataPieces().add(mapDataPiece(dataPiece));

                    if (lastPiece) {
                        concludeDay(days.get(indexOfCurrentDay), sunrise, sunset);
                        break;
                    }

                    i++;
//                    continue;
                } else {
                    if (!firstPiece) {
                        concludeDay(days.get(indexOfCurrentDay), sunrise, sunset);

                        indexOfCurrentDay++;

                        if (indexOfCurrentDay == OpenWeatherAPI.FORECAST_LENGTH_IN_DAYS) {
                            break;
                        }
                    }

                    sunrise += twentyFourHoursInMilliseconds;
                    sunset += twentyFourHoursInMilliseconds;
                    nextSunrise += twentyFourHoursInMilliseconds;
                }
            }

//            for (int i = 0; i < forecastedWeatherXML.getForecast().size(); ) {
//                ForecastedWeatherXML.Time dataPiece = forecastedWeatherXML.getForecast().get(i);
//
//                long forecastTime = SDF.parse(dataPiece.getFrom()).getTime();
//
//                if (forecastTime < nextSunrise) {
//                    days.get(indexOfCurrentDay).getDataPieces().add(mapDataPiece(dataPiece));
//                    i++;
//                } else {
//
//                    if (i != 0) {
//                        concludeDay(days.get(indexOfCurrentDay), sunrise, sunset);
//
//                        indexOfCurrentDay++;
//
//                        if (indexOfCurrentDay == OpenWeatherAPI.FORECAST_LENGTH_IN_DAYS) {
//                            break;
//                        }
//                    }
//
//                    sunrise += twentyFourHoursInMilliseconds;
//                    sunset += twentyFourHoursInMilliseconds;
//                    nextSunrise += twentyFourHoursInMilliseconds;
//                }
//            }
            return days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void concludeDay(FiveDayForecast.OneDay day, long sunrise, long sunset) {

        WorstConditionFinder worstConditionFinder = new WorstConditionFinder(day.getDataPieces());

        day.setSunrise(sunrise)
                .setSunset(sunset)
                .setWorstWeatherConditionDescription(worstConditionFinder.getWorstWeatherConditionDescription())
                .setWorstWeatherIconURL(worstConditionFinder.getWorstWeatherIconURL());
    }

    private ArrayList<FiveDayForecast.OneDay> initDays() {
        return new ArrayList<FiveDayForecast.OneDay>() {{
            for (int i = 0; i < OpenWeatherAPI.FORECAST_LENGTH_IN_DAYS; i++) {
                FiveDayForecast.OneDay day = new FiveDayForecast.OneDay();
                day.setDataPieces(new ArrayList<>());
                add(day);
            }
        }};
    }

    private FiveDayForecast.OneDay.DataPiece mapDataPiece(ForecastedWeatherXML.Time dataPiece) {
        try {
            return new FiveDayForecast.OneDay.DataPiece()
                    .setCloudiness(dataPiece.getClouds().getAll())
                    .setForecastTime(SDF.parse(dataPiece.getFrom()).getTime())
                    .setHumidity(dataPiece.getHumidity().getValue())
                    .setPressure(dataPiece.getPressure().getValue())
                    .setTemperature(dataPiece.getTemperature().getValue())
                    .setWeatherConditionDescription(dataPiece.getSymbol().getWeatherConditionDescription())
                    .setWeatherConditionGroup(dataPiece.getPrecipitation().getWeatherConditionGroup())
                    .setWeatherConditionId(dataPiece.getSymbol().getWeatherConditionId())
                    .setWeatherIconURL(OpenWeatherAPI.WEATHER_ICON_URL_PREFIX + dataPiece.getSymbol().getWeatherConditionIcon() + OpenWeatherAPI.WEATHER_ICON_URL_SUFFIX)
                    .setWindDirection(dataPiece.getWindDirection().getDeg())
                    .setWindSpeed(dataPiece.getWindSpeed().getMps());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
