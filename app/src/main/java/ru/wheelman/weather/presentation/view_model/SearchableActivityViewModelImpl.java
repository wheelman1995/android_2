package ru.wheelman.weather.presentation.view_model;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ru.wheelman.weather.BR;
import ru.wheelman.weather.R;
import ru.wheelman.weather.data.data_sources.databases.sqlite.CityListDB;
import ru.wheelman.weather.di.scopes.SearchableActivityViewModelScope;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Toothpick;

@SearchableActivityViewModelScope
public class SearchableActivityViewModelImpl implements SearchableActivityViewModel {

    public static final String COLUMN_RESULT = "result";
    private static final String TAG = SearchableActivityViewModelImpl.class.getSimpleName();
    @Inject
    static SQLiteDatabase cityListDB;
    @Inject
    Context context;
    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    UpdateMethodSelector updateMethodSelector;

    private ScreenState screenState;

    @Inject
    public SearchableActivityViewModelImpl() {
        screenState = new ScreenState();
    }

    @Override
    public void onListItemClick(Cursor cursor) {
        int cityId = cursor.getInt(cursor.getColumnIndex(CityListDB.COLUMN_ID));
        cursor.close();
        onNewCitySelected(cityId);
    }

    private void onNewCitySelected(int cityId) {
        Log.d(TAG, "onNewCitySelected");
        updateMethodSelector.selectAndUpdate(cityId);
    }

    @Override
    public LiveData<Cursor> onActionSearchIntent(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);

        screenState.setSearchStatus(context.getString(R.string.searching_for_cities));
        screenState.setSearchStatusTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));

        DatabaseTask databaseTask = new DatabaseTask();
        MutableLiveData<Cursor> liveCursor = new MutableLiveData<>();
        databaseTask.setListener(cursor -> {
            screenState.setSearchFinished(true);
//            progressBar.hide();
            if (cursor.getCount() == 0) {
                screenState.setSearchStatus(context.getString(R.string.nothing_found));
                screenState.setSearchStatusTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
//                screenState.setEmptyText(context.getString(R.string.nothing_found));
//                screenState.setTextColor(getResources().getColor(R.color.red));
                cursor.close();
                return;
            }

            screenState.setSearchStatus(null);
            liveCursor.setValue(cursor);
        });
        databaseTask.execute(query);

//        CityListDatabase.getInstance().findCitiesBeginningWith(new CityListDatabase.JobProgressListener() {
//
//
//            @Override
//            public <T> void onJobDone(T t) {
//                progressBar.hide();
//
//                if (adapter.getCount() == 0) {
//                    empty.setText(getString(R.string.nothing_found));
//                    empty.setTextColor(getResources().getColor(R.color.red));
//                }
//            }
//
//            @Override
//            public <T> void onProgressUpdate(int progress) {
//                progressBar.setProgress(progress);
////                Log.d(TAG, String.valueOf(cityList.size()));
//
//                for (int i = adapter.getCount(); i < cityList.size(); i++) {
//                    adapter.add(String.format(Locale.UK, "%s, %s", cityList.get(i).getName(), cityList.get(i).getCountry()));
//                }
//            }
//        }, query);
        return liveCursor;
    }

    @Override
    public void onActionViewIntent(Intent intent) {
        int cityId = Integer.parseInt(intent.getDataString());
        onNewCitySelected(cityId);
    }

    @Override
    public void onDestroy(boolean finishing) {
        if (finishing) {
            Log.d(TAG, "onDestroy is finishing");
            Toothpick.closeScope(SearchableActivityViewModelScope.class);
        }
    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    private static class DatabaseTask extends AsyncTask<String, Void, Cursor> {

        private static String projection = "_id, name || ', ' || ifnull(country, 'N/A') as " + COLUMN_RESULT;
        private static String selection = COLUMN_RESULT + " like ?";
        private Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        protected Cursor doInBackground(String... strings) {
            String selectionArg = strings[0].concat("%");
            return cityListDB.query(CityListDB.TABLE_NAME, new String[]{projection}, selection, new String[]{selectionArg}, null, null, COLUMN_RESULT);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (listener != null)
                listener.onJobDone(cursor);
        }

        interface Listener {
            void onJobDone(Cursor cursor);
        }
    }

    public static class ScreenState extends BaseObservable {
        private boolean searchFinished;
        private String searchStatus;
        private int searchStatusTextColor;

        public ScreenState() {

        }

        @Bindable
        public int getSearchStatusTextColor() {
            return searchStatusTextColor;
        }

        public void setSearchStatusTextColor(int searchStatusTextColor) {
            this.searchStatusTextColor = searchStatusTextColor;
            notifyPropertyChanged(BR.searchStatusTextColor);
        }

        @Bindable
        public String getSearchStatus() {
            return searchStatus;
        }

        public void setSearchStatus(String searchStatus) {
            this.searchStatus = searchStatus;
            notifyPropertyChanged(BR.searchStatus);
        }

        @Bindable
        public boolean isSearchFinished() {
            return searchFinished;
        }

        public void setSearchFinished(boolean searchFinished) {
            this.searchFinished = searchFinished;
            notifyPropertyChanged(BR.searchFinished);
        }
    }
}
