package ru.wheelman.weather;

import android.app.Application;

import ru.wheelman.weather.di.modules.AppModule;
import ru.wheelman.weather.di.modules.WeatherAppModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import toothpick.Scope;
import toothpick.Toothpick;

public class WeatherApp extends Application {

//    @Inject
//    WeatherUpdateByLocationTrigger weatherUpdateByLocationTrigger;

    @Override
    public void onCreate() {
        super.onCreate();
        initToothpick();


//        initTrigger();
    }

//    private void initTrigger() {
//        weatherUpdateByLocationTrigger.start();
//    }

    private void initToothpick() {
        Scope appScope = Toothpick.openScope(ApplicationScope.class);
        appScope.installModules(new AppModule(this));
        appScope.installModules(new WeatherAppModule(this));
        Toothpick.inject(this, appScope);
    }
}
