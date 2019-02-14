package ru.wheelman.weather.di.modules;

import ru.wheelman.weather.presentation.view.activities.SearchableActivity;
import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModel;
import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModelImpl;
import toothpick.config.Module;

public class SearchableActivityModule extends Module {
    public SearchableActivityModule(SearchableActivity searchableActivity) {
        bind(SearchableActivityViewModel.class).to(SearchableActivityViewModelImpl.class);
    }
}
