package ru.wheelman.weather;

import android.app.SearchManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import ru.wheelman.weather.model.AboutFragment;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsChangedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private SettingsFragment settingsFragment;
    private AmbientConditionsFragment ambientConditionsFragment;
    private MainFragment mainFragment;
    private NavigationView navigationView;
    private boolean homeReturnsUp;
    private AboutFragment aboutFragment;
    private ImageView navDrawerHeaderForeground;
    private ConstraintLayout navDrawerHeaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initVariables();

        initListeners();

        initCityListDatabase();

        initActionBar();

        initFragments();

    }

    private void initCityListDatabase() {
        CityListDatabase.init(getApplication());
    }

    private void initFragments() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mainFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mainFragment)
                    .commit();
        }

        if (ambientConditionsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(ambientConditionsFragment)
                    .commit();
        }

        mainFragment = MainFragment.newInstance();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment, null)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_main_with_ambient, mainFragment, null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_main, mainFragment, null)
                    .commit();
        }
    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(null);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void initVariables() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navDrawerHeaderForeground = findViewById(R.id.image_nav_drawer_header_foreground);
        navDrawerHeaderLayout = (ConstraintLayout) navigationView.getHeaderView(0);
    }

    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_drawer_settings:
                    Log.d(TAG, "nav_drawer_settings pressed");
                    showSettingsFragment();
                    break;
                case R.id.nav_drawer_about:
                    showAboutDialog();
                    break;
            }

            if (item.getItemId() != R.id.nav_drawer_weather) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
            }
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (!settingsFragment.isVisible()) {
                    navigationView.getMenu().findItem(R.id.nav_drawer_settings).setChecked(false);
                    homeReturnsUp = false;
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                } else {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                    homeReturnsUp = true;
                }
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                Log.d(TAG, slideOffset + " slideOffset ");
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


    }


    private void showAboutDialog() {
        aboutFragment = AboutFragment.newInstance();
        aboutFragment.setListener(new AboutFragment.Listener() {
            @Override
            public void onDismiss() {
                if (aboutFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(aboutFragment)
                            .commit();
                }
                navigationView.getMenu().findItem(R.id.nav_drawer_about).setChecked(false);
            }
        });
        aboutFragment.show(getSupportFragmentManager(), "AboutFragment");
    }

    private void showSettingsFragment() {
        if (settingsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(settingsFragment)
                    .commit();
            getSupportFragmentManager().popBackStackImmediate("SettingsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        settingsFragment = SettingsFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main, settingsFragment, null)
                .addToBackStack("SettingsFragment")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                search.collapseActionView();
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (homeReturnsUp) {
                    homeReturnsUp = false;
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    onBackPressed();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
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
        getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, unit).apply();
        Log.d(TAG, "onTemperatureUnitsChanged");
        recreateAmbientConditionsFragment();
    }

    private void recreateAmbientConditionsFragment() {
        if (ambientConditionsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(ambientConditionsFragment)
                    .commit();
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment)
                    .commit();
        }
    }

    public Units getTemperatureUnits() {
        return Units.getUnitByIndex(getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex()));
    }
}
