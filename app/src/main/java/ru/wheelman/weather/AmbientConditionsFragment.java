package ru.wheelman.weather;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

public class AmbientConditionsFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor humSensor;
    private Sensor tempSensor;
    private TextView amb_temp_val;
    private TextView amb_hum_val;
    private Units unit;
    private View divider;

    public static AmbientConditionsFragment newInstance() {
        AmbientConditionsFragment f = new AmbientConditionsFragment();

//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ambient_conditions, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        humSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        divider = view.findViewById(R.id.ambient_conditions_divider);


        if (tempSensor != null) {
            initAmbientTemperature(view);

        }
        if (humSensor != null) {
            initAmbientHumidity(view);

        }

        initDivider(view);


        unit = ((MainActivity) getActivity()).getTemperatureUnits();

        return view;
    }

    private void initDivider(View v) {
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout rootLayout = v.findViewById(R.id.cl_amb_conditions);
        constraintSet.clone(rootLayout);

        if (humSensor != null) {
            constraintSet.connect(R.id.ambient_conditions_divider, ConstraintSet.TOP, R.id.tv_amb_hum, ConstraintSet.BOTTOM);
        } else if (tempSensor != null) {
            constraintSet.connect(R.id.ambient_conditions_divider, ConstraintSet.TOP, R.id.tv_amb_temp, ConstraintSet.BOTTOM);
        }

        constraintSet.applyTo(rootLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        if (humSensor != null) {
            sensorManager.registerListener(this, humSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    public void onPause() {
        if (humSensor != null) {
            sensorManager.unregisterListener(this, humSensor);
        }
        if (tempSensor != null) {
            sensorManager.unregisterListener(this, tempSensor);
        }
        super.onPause();
    }

    private void initAmbientTemperature(View v) {
        v.findViewById(R.id.tv_amb_temp).setVisibility(View.VISIBLE);
        amb_temp_val = v.findViewById(R.id.tv_amb_temp_val);
        amb_temp_val.setVisibility(View.VISIBLE);
    }

    private void initAmbientHumidity(View v) {
        v.findViewById(R.id.tv_amb_hum).setVisibility(View.VISIBLE);
        v.findViewById(R.id.humidity_icon).setVisibility(View.VISIBLE);
        amb_hum_val = v.findViewById(R.id.tv_amb_hum_val);
        amb_hum_val.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getStringType()) {
            case "android.sensor.ambient_temperature":
                float t = event.values[0];
                String tempUnits = null;
                switch (unit) {
                    case CELSIUS:
                        tempUnits = getString(R.string.celsius);
                        break;
                    case FAHRENHEIT:
                        t = t * 9 / 5 + 32;
                        tempUnits = getString(R.string.fahrenheit);
                        break;
                }
                amb_temp_val.setText(String.format(Locale.ENGLISH, "%.1f%s", t, tempUnits));
                break;
            case "android.sensor.relative_humidity":
                float humidity = event.values[0];
                amb_hum_val.setText(String.format(Locale.ENGLISH, "%.1f%%", humidity));
                ((HumidityCustomView) getView().findViewById(R.id.humidity_icon)).setDrawable(humidity > 70f ? R.drawable.humid : R.drawable.not_humid);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
