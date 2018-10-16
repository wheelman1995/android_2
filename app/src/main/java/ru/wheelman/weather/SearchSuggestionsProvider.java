package ru.wheelman.weather;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchSuggestionsProvider extends ContentProvider {

    private static final String TAG = SearchSuggestionsProvider.class.getSimpleName();


    @Override
    public boolean onCreate() {

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
        String query = uri.getLastPathSegment().toLowerCase(); // user's input
        Log.d(TAG, uri.toString());
        Log.d(TAG, query);
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA});
        ArrayList<City> cities = CityListDatabase.getInstance().findCitiesBeginningWith(query);
        for (int i = 0; i < cities.size(); i++) {
            City currentCity = cities.get(i);
            String id = String.valueOf(currentCity.getId());
            matrixCursor.addRow(new String[]{id, String.format(Locale.UK, "%s, %s", currentCity.getName(), currentCity.getCountry()), id});
        }

        return matrixCursor;
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
