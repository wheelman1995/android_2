package ru.wheelman.weather.presentation.view_model;

import android.util.Log;

import javax.inject.Inject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
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
    ISearchSuggestionsProvider suggestionsProvider;
    //    @Inject
//    WeatherUpdateTrigger weatherUpdateTrigger;
    @Inject
    ScreenState screenState;
    @Inject
    PreferenceHelper preferenceHelper;
    private boolean activityWasRecreated;
    private boolean weatherUpdateAfterProcessDeathPerformed;

    public MainActivityViewModelImpl() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityViewModelScope.class);
        scope.installModules(new MainActivityViewModelModule());
        Toothpick.inject(this, scope);
    }

    @Override
    public ScreenState getScreenState() {
        return screenState;
    }

    @Override
    protected void onCleared() {
        Toothpick.closeScope(MainActivityViewModelScope.class);
        super.onCleared();
    }

    @Override
    public void onRequestPermissionsResult(boolean permissionsGranted) {
        Log.d(TAG, "onRequestPermissionsResult " + permissionsGranted);

        suggestionsProvider.setLocationFeatures(permissionsGranted);

        if (!weatherUpdateAfterProcessDeathPerformed) {
            updateWeatherAfterProcessDeath(permissionsGranted);
        } else {
            if (!permissionsGranted) {
                updateMethodSelector.permissionsRevoked();
            }
        }

    }

    @Override
    public void onStop() {
//        weatherUpdateTrigger.stop();
    }

    @Override
    public void onStart(boolean permissionsGranted) {
//        weatherUpdateTrigger.start();
        Log.d(TAG, "onStart permissionsGranted " + permissionsGranted);
        suggestionsProvider.setLocationFeatures(permissionsGranted);
    }

    @Override
    public void onCreate(boolean permissionsGranted) {
        if (permissionsGranted) {
            if (!weatherUpdateAfterProcessDeathPerformed) {
                updateWeatherAfterProcessDeath(true);
            }
        }
    }

    private void updateWeatherAfterProcessDeath(boolean permissionsGranted) {
        if (preferenceHelper.isFirstStart()) {
            updateMethodSelector.onAppFirstStart(permissionsGranted);
        } else {
            updateMethodSelector.onAppNormalStart(permissionsGranted);
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
