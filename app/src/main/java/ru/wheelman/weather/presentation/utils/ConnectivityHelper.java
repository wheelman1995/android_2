package ru.wheelman.weather.presentation.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

import ru.wheelman.weather.di.scopes.ApplicationScope;

@ApplicationScope
public class ConnectivityHelper implements IConnectivityHelper {

    private Context context;
    private ConnectivityManager connectivityManager;

    @Inject
    public ConnectivityHelper(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean isInternetConnected() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
