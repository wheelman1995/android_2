package ru.wheelman.weather.presentation.view_model;

import android.content.Intent;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

public interface SearchableActivityViewModel {
    LiveData<Cursor> onActionSearchIntent(Intent intent);

    void onActionViewIntent(Intent intent);

    void onListItemClick(Cursor cursor);

    void onDestroy();

    SearchableActivityViewModelImpl.ScreenState getScreenState();
}
