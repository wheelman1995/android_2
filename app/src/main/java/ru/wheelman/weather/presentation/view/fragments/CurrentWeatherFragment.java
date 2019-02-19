package ru.wheelman.weather.presentation.view.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.wheelman.weather.R;
import ru.wheelman.weather.databinding.FragmentCurrentWeatherBinding;
import ru.wheelman.weather.di.modules.CurrentWeatherFragmentModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.CurrentWeatherFragmentScope;
import ru.wheelman.weather.presentation.data_binding.IBindingAdapters;
import ru.wheelman.weather.presentation.view_model.CurrentWeatherViewModel;
import toothpick.Scope;
import toothpick.Toothpick;

public class CurrentWeatherFragment extends Fragment {

    public static final int ID_FOR_VIEW_PAGER = 0;
    private static final String TAG = CurrentWeatherFragment.class.getSimpleName();

    @Inject
    CurrentWeatherViewModel currentWeatherViewModel;
    @Inject
    IBindingAdapters bindingComponent;
    private AmbientConditionsFragment ambientConditionsFragment;
    private FragmentCurrentWeatherBinding binding;
    //    private SwipeRefreshLayout swipeRefreshLayout;

    public static CurrentWeatherFragment newInstance() {
        CurrentWeatherFragment f = new CurrentWeatherFragment();
        return f;
    }

    private void initAmbientConditionsFragment() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment, null)
                    .commit();
        }
    }

    public void onUnitsChanged() {
        Log.d(TAG, "onUnitsChanged");
        recreateAmbientConditionsFragment();
    }

    private void recreateAmbientConditionsFragment() {
        if (ambientConditionsFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(ambientConditionsFragment)
                    .commit();
            ambientConditionsFragment = AmbientConditionsFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ambient, ambientConditionsFragment)
                    .commit();
        }
    }

    private void initListeners() {

        //crop either image start and end equally, or only the bottom
//
//        mainFragmentBackground.addOnLayoutChangeListener(onLayoutChangeListener);

//        weatherDataObserver = new Observer<WeatherData>() {
//            @Override
//            public void onChanged(WeatherData weatherData) {
//                Log.d(TAG, "onChanged weatherDataObserver");
//                ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
//                if (weatherData == null) {
//                    actionBar.setTitle(getString(R.string.waiting_for_data));
//                    dataReceivingTimeValue.setText(getString(R.string.not_applicable));
//                    currentTemperature.setText(getString(R.string.not_applicable));
//                    weatherIcon.setImageAlpha(0);
//                    weatherDescription.setText(R.string.not_applicable);
//                }
//                if (weatherData != null) {
//
//                    String icon = weatherData.getWeatherIcon();
//                    long sunset = weatherData.getSunset();
//                    long sunrise = weatherData.getSunrise();
//
//                    ((MainActivity) getActivity()).setNavDrawerHeaderContent(sunset, sunrise);
//
//                    Date date = new Date(weatherData.getDt() * 1000L);
//                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM, HH:mm", Locale.UK);
//                    sdf.setTimeZone(TimeZone.getDefault());
//
//
//                    dataReceivingTimeValue.setText(sdf.format(date));
//
//                    currentTemperature.setText(weatherData.getTemperature());
//
//                    actionBar.setTitle(String.format(Locale.UK, "%s, %s", weatherData.getCity(), weatherData.getCountry()));
//
//                    weatherIcon.setImageAlpha(255);
//                    Picasso.get().load(String.format(Locale.UK, "%s%s.png", Constants.WEATHER_ICON_URL_BASE, icon)).into(weatherIcon);
//
//                    setBackgroundImage(icon);
//
//                    weatherDescription.setText(weatherData.getWeatherDescription());
//
//                    boolean day = icon.endsWith("d");
//                    int textColorId = day ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.white);
//                    setTextColor(textColorId);
//                    int textShadowColorId = day ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black);
//                    setTextShadowColor(textShadowColorId);
//                }
//            }
//
//            private void setBackgroundImage(String icon) {
//                mainFragmentBackground.setImageURI(Uri.parse(String.format(Locale.UK, "android.resource://%s/drawable/b%s", getActivity().getPackageName(), icon)));
//
//                Matrix matrix = new Matrix(mainFragmentBackground.getImageMatrix());
//
//                float scale;
//                float viewWidth = mainFragmentBackground.getWidth() - mainFragmentBackground.getPaddingLeft() - mainFragmentBackground.getPaddingRight();
//                float viewHeight = mainFragmentBackground.getHeight() - mainFragmentBackground.getPaddingTop() - mainFragmentBackground.getPaddingBottom();
//                float drawableWidth = mainFragmentBackground.getDrawable().getIntrinsicWidth();
//                float drawableHeight = mainFragmentBackground.getDrawable().getIntrinsicHeight();
//
//                //crop either image start and end equally, or only the bottom
//                if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
//                    scale = viewHeight / drawableHeight;
//                    float dx = -(drawableWidth * scale - viewWidth) / 2f;
//                    matrix.postTranslate(dx, 0f);
//                } else {
//                    scale = viewWidth / drawableWidth;
//                }
//                //
//                matrix.setScale(scale, scale);
//
//                mainFragmentBackground.setImageMatrix(matrix);
//            }
//
//            private void setTextColor(int colorId) {
//
//                dataReceivingTime.setTextColor(colorId);
//                currentTemperature.setTextColor(colorId);
//                weatherDescription.setTextColor(colorId);
//                dataReceivingTimeValue.setTextColor(colorId);
//
//            }
//
//            private void setTextShadowColor(int colorId) {
//
//                dataReceivingTime.setShadowLayer(10f, 0f, 0f, colorId);
//                currentTemperature.setShadowLayer(10f, 0f, 0f, colorId);
//                weatherDescription.setShadowLayer(10f, 0f, 0f, colorId);
//                dataReceivingTimeValue.setShadowLayer(10f, 0f, 0f, colorId);
//
//            }
//        };

//        swipeRefreshLayout.setOnRefreshListener(() -> currentWeatherViewModel.onRefreshSwipeRefreshLayout());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        initToothpick();

        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false, bindingComponent);

        initListeners();

        binding.setFragment(this);
        binding.setState(currentWeatherViewModel.getScreenState());
        binding.setViewModel(currentWeatherViewModel);


        initAmbientConditionsFragment();

        return binding.getRoot();
    }

    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, CurrentWeatherFragmentScope.class);
        scope.installModules(new CurrentWeatherFragmentModule(this));
        Toothpick.inject(this, scope);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        currentWeatherViewModel.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        currentWeatherViewModel.onStop();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment: ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        currentWeatherViewModel.onViewCreated();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroyView() {
        Toothpick.closeScope(CurrentWeatherFragmentScope.class);
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }
}
