package ru.wheelman.weather.presentation.utils;

public interface LocationHelper {
    boolean coordinatesAreValid();

    void startListeningToLocationChanges();

    void stopListeningToLocationChanges();
}
