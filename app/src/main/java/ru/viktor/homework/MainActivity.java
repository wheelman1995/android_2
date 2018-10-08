package ru.viktor.homework;

import android.app.SearchManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsChangedListener {

    public static final String SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY = "temp_units";
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private SettingsFragment settingsFragment;
    private AmbientConditionsFragment ambientConditionsFragment;
    private MainFragment mainFragment;
    private NavigationView navigationView;

    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_drawer_settings:
                    Log.d(TAG, "nav_drawer_settings pressed");
                    if (settingsFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(settingsFragment)
                                .commit();
                        getSupportFragmentManager().popBackStackImmediate("settingsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    settingsFragment = SettingsFragment.newInstance(this);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_main, settingsFragment, null)
                            .addToBackStack("settingsFragment")
                            .commit();
                    break;
            }

            if (item.getItemId() != R.id.nav_drawer_weather) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
            }
            return true;
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(null);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment, null)
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (!settingsFragment.isVisible()) {
                    navigationView.getMenu().findItem(R.id.nav_drawer_settings).setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onTemperatureUnitsChanged(int unit) {
        getPreferences(MODE_PRIVATE).edit().putInt(SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, unit).apply();
        if (ambientConditionsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(ambientConditionsFragment)
                    .commit();
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment, null)
                    .commit();
        }
    }

    public SettingsFragment.Units getTemperatureUnits() {
        return SettingsFragment.Units.getUnitByIndex(getPreferences(Context.MODE_PRIVATE).getInt(SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, 0));
    }
}
