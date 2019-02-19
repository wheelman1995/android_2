package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModel;
import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModelImpl;
import toothpick.config.Module;

public class SearchableActivityViewModelModule extends Module {
    public SearchableActivityViewModelModule() {
        bind(SearchableActivityViewModel.class).to(SearchableActivityViewModelImpl.class);
    }
}
