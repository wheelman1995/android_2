package ru.viktor.homework;

import android.app.SearchManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private SensorManager sensorManager;
    private Sensor humSensor;
    private Sensor tempSensor;
    private TextView amb_temp_val;
    private TextView amb_hum_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(item -> {
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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        humSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
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
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getStringType()) {
            case "android.sensor.ambient_temperature":
                amb_temp_val.setText(String.format(Locale.ENGLISH, "%.1f%s", event.values[0], getString(R.string.celsius)));
                break;
            case "android.sensor.relative_humidity":
                float humidity = event.values[0];
                amb_hum_val.setText(String.format(Locale.ENGLISH, "%.1f%%", humidity));
                ((HumidityCustomView) findViewById(R.id.humidity_icon)).setDrawable(humidity > 70f ? R.drawable.humid : R.drawable.not_humid);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        if (humSensor != null) {
            sensorManager.unregisterListener(this, humSensor);
        }
        if (tempSensor != null) {
            sensorManager.unregisterListener(this, tempSensor);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (humSensor != null) {
            sensorManager.registerListener(this, humSensor, SensorManager.SENSOR_DELAY_NORMAL);
            initAmbientHumidity();
        }
        if (tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
            initAmbientTemperature();
        }
    }

    private void initAmbientTemperature() {
        findViewById(R.id.constraint_ambient).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_amb_temp).setVisibility(View.VISIBLE);
        amb_temp_val = findViewById(R.id.tv_amb_temp_val);
        amb_temp_val.setVisibility(View.VISIBLE);
    }

    private void initAmbientHumidity() {
        findViewById(R.id.constraint_ambient).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_amb_hum).setVisibility(View.VISIBLE);
        findViewById(R.id.humidity_icon).setVisibility(View.VISIBLE);
        amb_hum_val = findViewById(R.id.tv_amb_hum_val);
        amb_hum_val.setVisibility(View.VISIBLE);

    }
}
