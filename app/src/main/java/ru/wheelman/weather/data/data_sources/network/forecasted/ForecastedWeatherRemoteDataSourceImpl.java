package ru.wheelman.weather.data.data_sources.network.forecasted;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import ru.wheelman.weather.data.data_sources.network.ForecastMapper;
import ru.wheelman.weather.data.data_sources.network.IOpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.UnitsMapper;
import ru.wheelman.weather.data.data_sources.network.forecasted.model.ForecastedWeatherXML;
import ru.wheelman.weather.di.qualifiers.ApiKeyQualifier;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.FiveDayForecast;
import ru.wheelman.weather.domain.entities.Units;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;

@ApplicationScope
public class ForecastedWeatherRemoteDataSourceImpl implements ForecastedWeatherRemoteDataSource {

    @Inject
    PreferenceHelper preferenceHelper;

    @Inject
    @ApiKeyQualifier
    String apiKey;

    private IOpenWeatherAPI openWeatherAPI;

    @Inject
    public ForecastedWeatherRemoteDataSourceImpl(IOpenWeatherAPI openWeatherAPI) {
        this.openWeatherAPI = openWeatherAPI;
    }

    @Override
    public FiveDayForecast requestFiveDayForecastByCityId() {
        Units units = preferenceHelper.getUnits();
        String mUnits = UnitsMapper.mapUnits(units);

        Call<ForecastedWeatherXML> forecastedWeatherXMLCall = openWeatherAPI.loadForecastedWeatherData(
                preferenceHelper.getLatestCityId(),
                mUnits,
                apiKey,
                OpenWeatherAPI.MODE_XML);

        try {
            Response<ForecastedWeatherXML> response = forecastedWeatherXMLCall.execute();

            if (response.isSuccessful()) {
                return new ForecastMapper().map(response.body());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public FiveDayForecast requestFiveDayForecastByCoordinates() {
        String mUnits = UnitsMapper.mapUnits(preferenceHelper.getUnits());

        Call<ForecastedWeatherXML> forecastedWeatherXMLCall = openWeatherAPI.loadForecastedWeatherDataByCoordinates(
                preferenceHelper.getLatitude(),
                preferenceHelper.getLongitude(),
                mUnits,
                apiKey,
                OpenWeatherAPI.MODE_XML);

        try {
            Response<ForecastedWeatherXML> response = forecastedWeatherXMLCall.execute();

            if (response.isSuccessful()) {
                return new ForecastMapper().map(response.body());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
