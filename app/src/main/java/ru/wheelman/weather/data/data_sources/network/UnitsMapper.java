package ru.wheelman.weather.data.data_sources.network;

import ru.wheelman.weather.domain.entities.Units;

public class UnitsMapper {

    public static String mapUnits(Units units) {
        switch (units) {
            case CELSIUS:
                return OpenWeatherAPI.CELSIUS;
            case FAHRENHEIT:
                return OpenWeatherAPI.FAHRENHEIT;
            default:
                return OpenWeatherAPI.CELSIUS;

        }
    }
}
