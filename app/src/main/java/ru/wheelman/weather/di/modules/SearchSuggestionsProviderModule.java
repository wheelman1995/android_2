package ru.wheelman.weather.di.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ru.wheelman.weather.data.data_sources.databases.sqlite.CityListDB;
import ru.wheelman.weather.data.repositories.ISearchSuggestionsProvider;
import toothpick.config.Module;

public class SearchSuggestionsProviderModule extends Module {
    public SearchSuggestionsProviderModule(ISearchSuggestionsProvider iSearchSuggestionsProvider, Context context) {
        bind(SQLiteDatabase.class).toInstance(new CityListDB(context).getReadableDatabase());
        bind(ISearchSuggestionsProvider.class).toInstance(iSearchSuggestionsProvider);
    }
}
