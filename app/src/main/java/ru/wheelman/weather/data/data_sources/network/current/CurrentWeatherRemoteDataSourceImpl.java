package ru.wheelman.weather.data.data_sources.network.current;

import java.io.IOException;
import java.util.Calendar;

import javax.inject.Inject;

import retrofit2.Response;
import ru.wheelman.weather.data.data_sources.network.IOpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.UnitsMapper;
import ru.wheelman.weather.data.data_sources.network.current.model.CurrentWeather;
import ru.wheelman.weather.di.qualifiers.ApiKeyQualifier;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.CurrentWeatherConditions;
import ru.wheelman.weather.domain.entities.Units;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;

@ApplicationScope
public class CurrentWeatherRemoteDataSourceImpl implements CurrentWeatherRemoteDataSource {

    private final IOpenWeatherAPI openWeatherAPI;

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    @ApiKeyQualifier
    String apiKey;

    @Inject
    public CurrentWeatherRemoteDataSourceImpl(IOpenWeatherAPI openWeatherAPI) {
        this.openWeatherAPI = openWeatherAPI;
    }

    @Override
    public CurrentWeatherConditions requestCurrentWeatherConditionsByCityId() {

        Units units = preferenceHelper.getUnits();
        String mUnits = UnitsMapper.mapUnits(units);

        try {
            Response<CurrentWeather> response = openWeatherAPI.loadWeatherData(preferenceHelper.getLatestCityId(), mUnits, apiKey).execute();
            if (response.isSuccessful()) {
                return mapResponse(response, units);
            }
            //todo handle error
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CurrentWeatherConditions requestCurrentWeatherConditionsByCoordinates() {

        Units units = preferenceHelper.getUnits();
        String mUnits = UnitsMapper.mapUnits(units);

        try {
            Response<CurrentWeather> response = openWeatherAPI.loadWeatherDataByCoordinates(
                    preferenceHelper.getLatitude(),
                    preferenceHelper.getLongitude(),
                    mUnits,
                    apiKey)
                    .execute();

            if (response.isSuccessful()) {
                return mapResponse(response, units);
            }
            //todo handle error
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private CurrentWeatherConditions mapResponse(Response<CurrentWeather> response, Units units) {
        CurrentWeather body = response.body();

        return new CurrentWeatherConditions()
                .setCityId(body.getId())
                .setUnits(units)
                .setUpdateTime(Calendar.getInstance().getTimeInMillis())
                .setCityName(body.getName())
                .setCountry(body.getSys().getCountry())
                .setSunrise(body.getSys().getSunrise())
                .setSunset(body.getSys().getSunset())
                .setDataReceivingTime(body.getDt() * 1000L)
                .setTemperature(body.getMain().getTemp())
                .setWeatherConditionId(body.getWeather()[0].getId())
                .setWeatherConditionGroup(body.getWeather()[0].getMain())
                .setWeatherConditionDescription(body.getWeather()[0].getDescription())
                .setWeatherIconURL(OpenWeatherAPI.WEATHER_ICON_URL_PREFIX + body.getWeather()[0].getIcon() + OpenWeatherAPI.WEATHER_ICON_URL_SUFFIX);
    }
}
