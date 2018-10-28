package ru.wheelman.weather;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SearchableActivity extends ListActivity {

    public static final String ACTION_FINISH = "ru.wheelman.weather.action.finish";

    private static final String TAG = "SearchableActivity";

    private ContentLoadingProgressBar progressBar;
    private TextView empty;
    //    private ArrayList<City> cityList;
    private int unitIndex;
    private Intent intent;
    //    private ArrayAdapter<String> adapter;
    private CursorAdapter cursorAdapter;
    private Cursor cursor;
//    private static SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        initVariables();

        onNewIntent();
    }

    private void loadWeather() {
        int cityId = Integer.parseInt(intent.getDataString());
        onNewCitySelected(cityId);
    }

    private void initVariables() {
        progressBar = findViewById(R.id.pb_cities);
        empty = findViewById(android.R.id.empty);
//        cityList = CityListDatabase.getMatchedCities();
        unitIndex = getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex());
//        db = MainActivity.getCityListDB();
//        adapter = new ArrayAdapter<>(this, R.layout.activity_searchable_adapter_item, R.id.tv_search_adapter_item);
//        setListAdapter(adapter);
    }

    private void onNewIntent() {
        intent = getIntent();

        closeCursor();

        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                search();
                break;
            case Intent.ACTION_VIEW:
                loadWeather();
                break;
        }
    }

    private void search() {

        String query = intent.getStringExtra(SearchManager.QUERY);

        DatabaseTask databaseTask = new DatabaseTask();
        databaseTask.setListener(cursor -> {
            progressBar.hide();
            if (cursor.getCount() < 1) {
                empty.setText(getString(R.string.no_data_available));
                empty.setTextColor(getResources().getColor(R.color.red));
                cursor.close();
                return;
            }
            this.cursor = cursor;
            if (cursorAdapter == null) {
                cursorAdapter = new SimpleCursorAdapter(SearchableActivity.this, R.layout.activity_searchable_adapter_item, cursor, new String[]{"result"}, new int[]{R.id.tv_search_adapter_item}, 0);
                setListAdapter(cursorAdapter);
            } else {
                cursorAdapter.changeCursor(cursor);
            }
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

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onNewIntent();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        cursor.moveToPosition(position);

//        City selectedCity = cityList.get(position);
        int cityId = cursor.getInt(cursor.getColumnIndex(CityListDB.COLUMN_ID));
        onNewCitySelected(cityId);
    }

    private void onNewCitySelected(int cityId) {
        getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERENCES_CURRENT_CITY_ID, cityId).apply();

        Intent intent = new Intent(ACTION_FINISH);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        closeCursor();

        finish();
    }

    @Override
    protected void onDestroy() {
        closeCursor();
        super.onDestroy();
    }

    private void closeCursor() {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }
}

class DatabaseTask extends AsyncTask<String, Void, Cursor> {

    private static String projection = "_id, name || ', ' || ifnull(country, 'N/A') as result";
    private static String selection = "result like ?";
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected Cursor doInBackground(String... strings) {
        String selectionArg = strings[0].concat("%");
        SQLiteDatabase db = MainActivity.getCityListDB();
        return db.query(CityListDB.TABLE_NAME, new String[]{projection}, selection, new String[]{selectionArg}, null, null, "result");
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
