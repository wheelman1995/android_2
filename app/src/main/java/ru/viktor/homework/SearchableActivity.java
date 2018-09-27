package ru.viktor.homework;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class SearchableActivity extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            //TODO: search
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
        Toast.makeText(this, query + " onNewIntent", Toast.LENGTH_SHORT).show();
        //todo: search
    }
}
