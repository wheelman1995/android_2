package ru.wheelman.weather.presentation.view.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import ru.wheelman.weather.R;
import ru.wheelman.weather.databinding.ActivitySearchableBinding;
import ru.wheelman.weather.di.modules.SearchableActivityViewModelModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.SearchableActivityScope;
import ru.wheelman.weather.di.scopes.SearchableActivityViewModelScope;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModel;
import ru.wheelman.weather.presentation.view_model.SearchableActivityViewModelImpl;
import toothpick.Scope;
import toothpick.Toothpick;


public class SearchableActivity extends ListActivity {
    private static final String TAG = "SearchableActivity";
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    SearchableActivityViewModel viewModel;
    //    private ContentLoadingProgressBar progressBar;
//    private TextView empty;
    //    private ArrayList<City$$> cityList;
//    private int unitIndex;
    private Intent intent;
    //    private static SQLiteDatabase db;
    //    private ArrayAdapter<String> adapter;
    private CursorAdapter cursorAdapter;
    private Cursor cursor;
    private Observer<Cursor> cursorObserver;
    private LiveData<Cursor> liveCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToothpick();

        ActivitySearchableBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_searchable);
        binding.setState(viewModel.getScreenState());


        initUi();

        mOnNewIntent(getIntent());
    }

    private void initToothpick() {

        if (!Toothpick.isScopeOpen(SearchableActivityViewModelScope.class)) {
            Scope viewModelScope = Toothpick.openScopes(ApplicationScope.class, SearchableActivityViewModelScope.class);
            viewModelScope.installModules(new SearchableActivityViewModelModule());
        }

        Scope scope = Toothpick.openScopes(ApplicationScope.class, SearchableActivityViewModelScope.class, SearchableActivityScope.class);
        Toothpick.inject(this, scope);

        Log.d(TAG, viewModel.toString());
    }


    private void initUi() {
//        progressBar = findViewById(R.id.pb_cities);
//        empty = findViewById(android.R.id.empty);
//        cityList = CityListDatabase.getMatchedCities();
//        unitIndex = getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
//                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());
//        db = MainActivity.getCityListDB();
//        adapter = new ArrayAdapter<>(this, R.layout.activity_searchable_adapter_item, R.id.tv_search_adapter_item);
//        setListAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        mOnNewIntent(intent);
    }

    private void mOnNewIntent(Intent intent) {
        Log.d(TAG, "mOnNewIntent " + intent.getAction());
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                onActionSearchIntent(intent);
                break;
            case Intent.ACTION_VIEW:
                viewModel.onActionViewIntent(intent);
                finish();
                break;
        }
    }

    private void onActionSearchIntent(Intent intent) {
        removeLiveCursorObserverAndCloseCursor();
        liveCursor = viewModel.onActionSearchIntent(intent);

        cursorObserver = cursor -> {
            if (cursorAdapter == null) {
                cursorAdapter = new SimpleCursorAdapter(
                        SearchableActivity.this,
                        R.layout.activity_searchable_adapter_item,
                        cursor,
                        new String[]{SearchableActivityViewModelImpl.COLUMN_RESULT},
                        new int[]{R.id.tv_search_adapter_item},
                        0);
                setListAdapter(cursorAdapter);
            } else {
                cursorAdapter.changeCursor(cursor);
            }
        };

        liveCursor.observeForever(cursorObserver);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) cursorAdapter.getItem(position);
        viewModel.onListItemClick(cursor);
        finish();
    }


    @Override
    protected void onDestroy() {
        viewModel.onDestroy(isFinishing());
        removeLiveCursorObserverAndCloseCursor();
        Toothpick.closeScope(SearchableActivityScope.class);
        super.onDestroy();
    }

    private void removeLiveCursorObserverAndCloseCursor() {
        if (liveCursor != null) {
            Cursor cursor = liveCursor.getValue();
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            liveCursor.removeObserver(cursorObserver);
        }
    }
}


