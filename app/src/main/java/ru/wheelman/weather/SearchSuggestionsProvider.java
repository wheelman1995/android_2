package ru.wheelman.weather;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchSuggestionsProvider extends ContentProvider {

    private static final String TAG = SearchSuggestionsProvider.class.getSimpleName();
    public static final int CURRENT_LOCATION_SUGGESTION_ID = 1;
    //    public static final String CURRENT_LOCATION_SUGGESTION_INTENT_DATA = "current_location_suggestion_id";
    public static final String CURRENT_LOCATION_SUGGESTION_COLUMN_TEXT_1 = "Use your location";

    private static boolean currentLocationSuggestionEnabled = true;
    private String proj;

    public static void setCurrentLocationSuggestionEnabled(boolean currentLocationSuggestionEnabled) {
        SearchSuggestionsProvider.currentLocationSuggestionEnabled = currentLocationSuggestionEnabled;
    }

    @Override
    public boolean onCreate() {
        proj = String.format(Locale.UK, "%s, %s || ', ' || ifnull(%s, 'N/A') as %s, %s as %s, '%s' as %s", CityListDB.COLUMN_ID, CityListDB.COLUMN_NAME, CityListDB.COLUMN_COUNTRY,
                SearchManager.SUGGEST_COLUMN_TEXT_1, CityListDB.COLUMN_ID, SearchManager.SUGGEST_COLUMN_INTENT_DATA, String.valueOf(R.drawable.blank), SearchManager.SUGGEST_COLUMN_ICON_1);
        return true;
    }


//    uri
//    Always a content Uri, formatted as:
//content://your.authority/optional.suggest.path/SUGGEST_URI_PATH_QUERY
//The default behavior is for system to pass this URI and append it with the query text. For example:
//
//content://your.authority/optional.suggest.path/SUGGEST_URI_PATH_QUERY/puppies

//    projection
//Always null

//    selection
//The value provided in the android:searchSuggestSelection attribute of your searchable configuration file, or null if you have not declared the android:searchSuggestSelection attribute. More about using this to get the query below.

    //selectionArgs
//Contains the search query as the first (and only) element of the array if you have declared the android:searchSuggestSelection attribute in your searchable configuration. If you have not declared android:searchSuggestSelection, then this parameter is null. More about using this to get the query below.

    //sortOrder
//Always null

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//        create a MatrixCursor using the required column names and then add a row for each suggestion using addRow(Object[]). Return the final product from your Content Provider's query() method.


//        The system understands several columns, but only two of them are required:
//
//_ID
//A unique integer row ID for each suggestion. The system requires this in order to present suggestions in a ListView.
//SUGGEST_COLUMN_TEXT_1
//The string that is presented as a suggestion.
//The following columns are all optional (and most are discussed further in the following sections):
//
//SUGGEST_COLUMN_TEXT_2
//A string. If your Cursor includes this column, then all suggestions are provided in a two-line format. The string in this column is displayed as a second, smaller line of text below the primary suggestion text. It can be null or empty to indicate no secondary text.
//        String query = uri.getLastPathSegment().toLowerCase(); // user's input
//        Log.d(TAG, uri.toString());
//        Log.d(TAG, query);
        SQLiteDatabase db = MainActivity.getCityListDB();
        String selectionArg = selectionArgs[0].concat("%");

        Cursor cursor = db.query(CityListDB.TABLE_NAME, new String[]{proj}, selection, new String[]{selectionArg}, null, null, SearchManager.SUGGEST_COLUMN_TEXT_1);

        if (currentLocationSuggestionEnabled) {
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA, SearchManager.SUGGEST_COLUMN_ICON_1});
            matrixCursor.addRow(new String[]{String.valueOf(CURRENT_LOCATION_SUGGESTION_ID), CURRENT_LOCATION_SUGGESTION_COLUMN_TEXT_1, String.valueOf(CURRENT_LOCATION_SUGGESTION_ID), String.valueOf(R.drawable.current_location_suggestion_icon)});
            return new MergeCursor(new Cursor[]{matrixCursor, cursor});
        }

//        ArrayList<City> cities = CityListDatabase.getInstance().findCitiesBeginningWith(query);
//        for (int i = 0; i < cities.size(); i++) {
//            City currentCity = cities.get(i);
//            String id = String.valueOf(currentCity.getId());
//            matrixCursor.addRow(new String[]{id, String.format(Locale.UK, "%s, %s", currentCity.getName(), currentCity.getCountry()), id, String.valueOf(R.drawable.blank)});
//        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
