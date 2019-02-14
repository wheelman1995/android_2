package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.WeatherApp;
import ru.wheelman.weather.data.data_sources.databases.CurrentWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.databases.ForecastedWeatherLocalDataSource;
import ru.wheelman.weather.data.data_sources.databases.room.Database;
import ru.wheelman.weather.data.data_sources.network.OpenWeatherAPI;
import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherRemoteDataSource;
import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherRemoteDataSourceImpl;
import ru.wheelman.weather.data.data_sources.network.current.CurrentWeatherService;
import ru.wheelman.weather.data.data_sources.network.forecasted.ForecastedWeatherRemoteDataSource;
import ru.wheelman.weather.data.data_sources.network.forecasted.ForecastedWeatherRemoteDataSourceImpl;
import ru.wheelman.weather.data.data_sources.network.forecasted.ForecastedWeatherService;
import ru.wheelman.weather.data.repositories.CurrentWeatherRepository;
import ru.wheelman.weather.data.repositories.CurrentWeatherRepositoryImpl;
import ru.wheelman.weather.data.repositories.ForecastedWeatherRepository;
import ru.wheelman.weather.data.repositories.ForecastedWeatherRepositoryImpl;
import ru.wheelman.weather.di.providers.CurrentWeatherServiceProvider;
import ru.wheelman.weather.di.providers.ForecastedWeatherServiceProvider;
import ru.wheelman.weather.domain.interactors.CurrentWeatherInteractor;
import ru.wheelman.weather.domain.interactors.CurrentWeatherInteractorImpl;
import ru.wheelman.weather.domain.interactors.ForecastedWeatherInteractor;
import ru.wheelman.weather.domain.interactors.ForecastedWeatherInteractorImpl;
import ru.wheelman.weather.presentation.utils.LocationHelper;
import ru.wheelman.weather.presentation.utils.LocationHelperImpl;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelectorImpl;
import ru.wheelman.weather.presentation.utils.worker.current.CurrentWeatherUpdateWorkerHelper;
import ru.wheelman.weather.presentation.utils.worker.current.CurrentWeatherUpdateWorkerHelperImpl;
import ru.wheelman.weather.presentation.utils.worker.forecasted.ForecastedWeatherUpdateWorkerHelper;
import ru.wheelman.weather.presentation.utils.worker.forecasted.ForecastedWeatherUpdateWorkerHelperImpl;
import toothpick.config.Module;

public class WeatherAppModule extends Module {

    public WeatherAppModule(WeatherApp weatherApp) {
        Database database = Database.getDatabase(weatherApp);

        bind(Database.class).toInstance(database);

        bind(OpenWeatherAPI.class);

        bind(CurrentWeatherService.class).toProvider(CurrentWeatherServiceProvider.class);
        bind(ForecastedWeatherService.class).toProvider(ForecastedWeatherServiceProvider.class);

        bind(CurrentWeatherRemoteDataSource.class).to(CurrentWeatherRemoteDataSourceImpl.class);
        bind(ForecastedWeatherRemoteDataSource.class).to(ForecastedWeatherRemoteDataSourceImpl.class);

        bind(CurrentWeatherLocalDataSource.class).toInstance(database);
        bind(ForecastedWeatherLocalDataSource.class).toInstance(database);

        bind(CurrentWeatherRepository.class).to(CurrentWeatherRepositoryImpl.class);
        bind(ForecastedWeatherRepository.class).to(ForecastedWeatherRepositoryImpl.class);

        bind(CurrentWeatherInteractor.class).to(CurrentWeatherInteractorImpl.class);
        bind(ForecastedWeatherInteractor.class).to(ForecastedWeatherInteractorImpl.class);

        bind(LocationHelper.class).to(LocationHelperImpl.class);

        bind(CurrentWeatherUpdateWorkerHelper.class).to(CurrentWeatherUpdateWorkerHelperImpl.class);
        bind(ForecastedWeatherUpdateWorkerHelper.class).to(ForecastedWeatherUpdateWorkerHelperImpl.class);

//        bind(WeatherUpdateByLocationTrigger.class).to(WeatherUpdateByLocationTriggerImpl.class);

        bind(UpdateMethodSelector.class).to(UpdateMethodSelectorImpl.class);

    }
}
