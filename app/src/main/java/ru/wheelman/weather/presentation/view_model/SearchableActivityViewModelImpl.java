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
import ru.wheelman.weather.data.data_sources.databases.sqlite.CityListDB;
import ru.wheelman.weather.di.scopes.SearchableActivityScope;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;

@SearchableActivityScope
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

    public SearchableActivityViewModelImpl() {
        screenState = new ScreenState(false, false);
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

        DatabaseTask databaseTask = new DatabaseTask();
        MutableLiveData<Cursor> liveCursor = new MutableLiveData<>();
        databaseTask.setListener(cursor -> {
            screenState.setSearchFinished(true);
//            progressBar.hide();
            if (cursor.getCount() == 0) {
                screenState.setSearchResultEmpty(true);
//                screenState.setEmptyText(context.getString(R.string.no_data_available));
//                screenState.setTextColor(getResources().getColor(R.color.red));
                cursor.close();
                return;
            }
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
//                    empty.setText(getString(R.string.no_data_available));
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
    public void onDestroy() {

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
        private boolean searchResultEmpty;

        public ScreenState(boolean searchFinished, boolean searchResultEmpty) {
            this.searchFinished = searchFinished;
            this.searchResultEmpty = searchResultEmpty;
        }

        @Bindable
        public boolean isSearchResultEmpty() {
            return searchResultEmpty;
        }

        public void setSearchResultEmpty(boolean searchResultEmpty) {
            this.searchResultEmpty = searchResultEmpty;
            notifyPropertyChanged(BR.searchResultEmpty);
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
