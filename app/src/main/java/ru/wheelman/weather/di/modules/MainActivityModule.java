package ru.wheelman.weather.di.modules;

import androidx.lifecycle.ViewModelProviders;
import ru.wheelman.weather.presentation.view.activities.MainActivity;
import ru.wheelman.weather.presentation.view_model.MainActivityViewModel;
import ru.wheelman.weather.presentation.view_model.MainActivityViewModelImpl;
import toothpick.config.Module;

public class MainActivityModule extends Module {
    public MainActivityModule(MainActivity mainActivity) {
        bind(MainActivityViewModel.class).toInstance(ViewModelProviders.of(mainActivity).get(MainActivityViewModelImpl.class));
    }
}
