package ru.wheelman.weather.presentation.data_mappers;

public interface DataMapper<F, T> {
    void map(F from, T to);
}
