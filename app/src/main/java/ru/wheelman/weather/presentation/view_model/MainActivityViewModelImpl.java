package ru.wheelman.weather.presentation.view_model;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import ru.wheelman.weather.BR;
import ru.wheelman.weather.data.repositories.ISearchSuggestionsProvider;
import ru.wheelman.weather.di.modules.MainActivityViewModelModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.MainActivityViewModelScope;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Scope;
import toothpick.Toothpick;


public class MainActivityViewModelImpl extends ViewModel implements MainActivityViewModel {
    private static final String TAG = MainActivityViewModelImpl.class.getSimpleName();
    @Inject
    UpdateMethodSelector updateMethodSelector;
    @Inject
    Context context;
    @Inject
    ISearchSuggestionsProvider suggestionsProvider;
    //    @Inject
//    WeatherUpdateTrigger weatherUpdateTrigger;
    @Inject
    ScreenState screenState;
    @Inject
    PreferenceHelper preferenceHelper;

    private boolean activityWasRecreated;
    private boolean weatherUpdateAfterProcessDeathPerformed;
    private boolean updateByLocationAllowed;

    public MainActivityViewModelImpl() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityViewModelScope.class);
        scope.installModules(new MainActivityViewModelModule());
        Toothpick.inject(this, scope);
    }

    @Override
    public LiveData<Boolean> isInternetConnected() {
        return updateMethodSelector.getInternetConnected();
    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        Toothpick.closeScope(MainActivityViewModelScope.class);
        super.onCleared();
    }

    @Override
    public void onRequestPermissionsResult(boolean permissionsGranted) {
        updateByLocationAllowed = permissionsGranted && locationServicesEnabled();
        Log.d(TAG, "onRequestPermissionsResult updateByLocationAllowed " + updateByLocationAllowed);

        suggestionsProvider.setLocationFeatures(updateByLocationAllowed);

        checkProcessDiedAndUpdate();
    }

    private boolean locationServicesEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onStart(boolean permissionsGranted) {
        if (!permissionsGranted) {
            return;
        }

        updateByLocationAllowed = locationServicesEnabled();
        Log.d(TAG, "onStart update by location allowed " + updateByLocationAllowed);

        suggestionsProvider.setLocationFeatures(updateByLocationAllowed);

        checkProcessDiedAndUpdate();
    }

    private void checkProcessDiedAndUpdate() {
        if (!weatherUpdateAfterProcessDeathPerformed) {
            updateWeatherAfterProcessDeath(updateByLocationAllowed);
        } else {
            if (!updateByLocationAllowed) {
                updateMethodSelector.appRestoredWithoutLocationServices();
            }
        }
    }

    @Override
    public void onCreate(boolean permissionsGranted) {
        if (!permissionsGranted) {
            return;
        }
        updateByLocationAllowed = locationServicesEnabled();
        Log.d(TAG, "onCreate: updateByLocationAllowed: " + updateByLocationAllowed);

        if (!weatherUpdateAfterProcessDeathPerformed && updateByLocationAllowed) {
            updateWeatherAfterProcessDeath(true);
        }
    }

    private void updateWeatherAfterProcessDeath(boolean updateByLocationAllowed) {
        if (preferenceHelper.isFirstStart()) {
            updateMethodSelector.onAppFirstStart(updateByLocationAllowed);
        } else {
            updateMethodSelector.onAppNormalStart(updateByLocationAllowed);
        }
        weatherUpdateAfterProcessDeathPerformed = true;

    }

    @Override
    public void onDestroy(boolean isFinishing) {
        if (!isFinishing) {
            activityWasRecreated = true;
        }
    }

    @MainActivityViewModelScope
    public static class ScreenState extends BaseObservable {
        private String actionBarTitle;
        private int navDrawerHeaderBackgroundDrawableId;
        private int navDrawerHeaderForegroundDrawableId;

        @Inject
        public ScreenState() {
        }

        @Bindable
        public String getActionBarTitle() {
            return actionBarTitle;
        }

        public void setActionBarTitle(String actionBarTitle) {
            this.actionBarTitle = actionBarTitle;
            notifyPropertyChanged(BR.actionBarTitle);
        }

        @Bindable
        public int getNavDrawerHeaderBackgroundDrawableId() {
            return navDrawerHeaderBackgroundDrawableId;
        }

        public void setNavDrawerHeaderBackgroundDrawableId(int navDrawerHeaderBackgroundDrawableId) {
            this.navDrawerHeaderBackgroundDrawableId = navDrawerHeaderBackgroundDrawableId;
            notifyPropertyChanged(BR.navDrawerHeaderBackgroundDrawableId);
        }

        @Bindable
        public int getNavDrawerHeaderForegroundDrawableId() {
            return navDrawerHeaderForegroundDrawableId;
        }

        public void setNavDrawerHeaderForegroundDrawableId(int navDrawerHeaderForegroundDrawableId) {
            this.navDrawerHeaderForegroundDrawableId = navDrawerHeaderForegroundDrawableId;
            notifyPropertyChanged(BR.navDrawerHeaderForegroundDrawableId);
        }
    }
}
