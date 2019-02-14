package ru.wheelman.weather.data.data_sources.databases.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CityListDB extends SQLiteOpenHelper {
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_LATITUDE = "coordlat";
    public static final String COLUMN_LONGITUDE = "coordlon";
    public static final int VERSION = 1;
    public static final String DB_NAME = "city_list.db";
    public static final String TABLE_NAME = "city_list";
    private static final String TAG = CityListDB.class.getSimpleName();
    private static File DB_FILE;
    private Context context;

    public CityListDB(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        DB_FILE = new File(/*String.format(Locale.UK, "/data/data/%s/databases/%s", context.getPackageName()*/context.getDir("databases", Context.MODE_PRIVATE), DB_NAME);
        Log.d(TAG, DB_FILE.getAbsolutePath());
        try {
            if (DB_FILE.createNewFile()) {
                createDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DB_FILE.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    private void createDatabase() {
        Log.d(TAG, "creating a db");

        try (InputStream is = context.getAssets().open(DB_NAME);
             BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);
             FileOutputStream fos = new FileOutputStream(DB_FILE, false)) {

            byte[] byteBuff = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = bis.read(byteBuff)) > 0) {
                fos.write(byteBuff, 0, bytesRead);
            }
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
