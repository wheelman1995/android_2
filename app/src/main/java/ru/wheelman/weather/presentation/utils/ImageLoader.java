package ru.wheelman.weather.presentation.utils;

public interface ImageLoader<T> {
    void loadImage(String url, T container);
}
