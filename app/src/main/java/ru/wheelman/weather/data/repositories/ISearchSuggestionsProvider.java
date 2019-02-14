package ru.wheelman.weather.data.repositories;

public interface ISearchSuggestionsProvider {
    boolean useYourLocationWasSelected(int cityId);

    void setLocationFeatures(boolean enabled);
}
