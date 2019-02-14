package ru.wheelman.weather.presentation.view.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ru.wheelman.weather.R;
import ru.wheelman.weather.databinding.ActivityMainBinding;
import ru.wheelman.weather.databinding.NavDrawerHeaderBinding;
import ru.wheelman.weather.di.modules.MainActivityModule;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.di.scopes.MainActivityScope;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.view.fragments.AboutFragment;
import ru.wheelman.weather.presentation.view.fragments.CurrentWeatherFragment;
import ru.wheelman.weather.presentation.view.fragments.FeedbackFragment;
import ru.wheelman.weather.presentation.view.fragments.ForecastedWeatherFragment;
import ru.wheelman.weather.presentation.view.fragments.SettingsFragment;
import ru.wheelman.weather.presentation.view_model.MainActivityViewModel;
import toothpick.Scope;
import toothpick.Toothpick;

public class MainActivity extends AppCompatActivity {

    static final int NUMBER_OF_PAGES = 2;
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 0;

    @Inject
    MainActivityViewModel viewModel;
    @Inject
    PreferenceHelper preferenceHelper;
    //    private static SQLiteDatabase cityListDB;
    private DrawerLayout drawerLayout;
    private SettingsFragment settingsFragment;
    //    private CurrentWeatherFragment currentWeatherFragment;
    private NavigationView navigationView;
    private boolean homeReturnsUp;
    private AboutFragment aboutFragment;
    private ImageView navDrawerHeaderForeground;
    private ConstraintLayout navDrawerHeaderLayout;
    private MenuItem search;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private CurrentWeatherFragment currentWeatherFragment;
    private FeedbackFragment feedbackFragment;
    private ForecastedWeatherFragment forecastedWeatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToothpick();

        initDataBinding();

        initUi();

        initListeners();

        initActionBar();

        viewModel.onCreate(permissionsGranted());
    }

    private void checkPermissions() {
        boolean permissionsGranted = permissionsGranted();

        viewModel.onStart(permissionsGranted);

        if (!permissionsGranted) {
            if (shouldShowRequestPermissionRationale()) {
                showRationale();
            } else {
                requestPermissions();
            }
        }
    }

    private void initDataBinding() {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setState(viewModel.getScreenState());
        NavDrawerHeaderBinding navDrawerHeaderBinding = NavDrawerHeaderBinding.bind(binding.navDrawerView.getHeaderView(0));
        navDrawerHeaderBinding.setState(viewModel.getScreenState());
    }


    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityScope.class);
        scope.installModules(new MainActivityModule(this));
        Toothpick.inject(this, scope);
    }

//    private void initCityListDatabase() {
//        cityListDB = new CityListDB(this).getReadableDatabase();
//        CityListDatabase.init(getApplication());
//    }

    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(null);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void initUi() {
        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navDrawerHeaderLayout = (ConstraintLayout) navigationView.getHeaderView(0);
        navDrawerHeaderForeground = navDrawerHeaderLayout.findViewById(R.id.image_nav_drawer_header_foreground);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        tabLayout = viewPager.findViewById(R.id.tl_main);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_drawer_settings:
//                    Log.d(TAG, "nav_drawer_settings pressed");
                    showSettingsFragment();
                    break;
                case R.id.nav_drawer_about:
                    showAboutDialog();
                    break;
                case R.id.nav_drawer_feedback:
                    showFeedbackFragment();
                    break;
            }

            if (item.getItemId() != R.id.nav_drawer_weather) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
            }
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            if (feedbackFragment != null && feedbackFragment.isVisible() || settingsFragment != null && settingsFragment.isVisible()) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                homeReturnsUp = true;
                return;
            }

            if (settingsFragment != null) {
                if (!settingsFragment.isVisible()) {
                    navigationView.getMenu().findItem(R.id.nav_drawer_settings).setChecked(false);
                }
            }

            if (feedbackFragment != null) {
                if (!feedbackFragment.isVisible()) {
                    navigationView.getMenu().findItem(R.id.nav_drawer_feedback).setChecked(false);
                }
            }

            homeReturnsUp = false;
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            int dpi = getResources().getDisplayMetrics().densityDpi;
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) navDrawerHeaderForeground.getLayoutParams();

            int foregroundWidth = layoutParams.width; //in pixels
            int foregroundHeight = layoutParams.height; //in pixels

            int originalY = layoutParams.editorAbsoluteY; // in pixels
            int originalX = layoutParams.editorAbsoluteX; // in pixels

            int endX = navigationView.getLayoutParams().width / 2 - foregroundWidth / 2;
            int endY = 20 * (dpi / 160); //20 in dp

            int aX = originalX - (originalX - endX) * 2;
            int aY = originalY;

            //            int k = ((-2 * endX + 2 * aX) * (endX * endX + endY * endY - originalX * originalX - originalY * originalY) - 2 * endX * aX *aX - 2*endX*aY * aY + 2*endX*endX*endX+2*endX*endY*endY+2*originalX*aX*aX + 2*originalX*aY*aY - 2*originalX*endX*endX-2*originalX*endY*endY) / (2*originalY*2*endX-2*originalY*2*aX-2*endY*2*endX+2*endY*2*aX+2*endX*2*endY-2*endX*2*aY+2*originalX*2*aY - 2*originalX*2*endY);
//            int h = (aX*aX + aY*aY - 2*aY*k-endX*endX-endY*endY+2*endY*k)/(-2*endX + 2*aX);
            int h = endX;
            int k = (endX * endX - 2 * endX * h + endY * endY - originalX * originalX + 2 * originalX * h - originalY * originalY) / (-2 * originalY + 2 * endY);

            double r = Math.sqrt(Math.pow(originalX - h, 2) + Math.pow(originalY - k, 2));

            int xDist = originalX - endX;
            int yDist = originalY - endY;


            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                float targetX = originalX - xDist * slideOffset;
                double d = 2d * k * 2d * k - 4d * (k * k - r * r + (Math.pow(targetX - h, 2d)));
                float targetY = (float) ((2f * k - Math.sqrt(d)) / 2f);

                navDrawerHeaderForeground.setX(targetX);
                navDrawerHeaderForeground.setY(targetY);
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private boolean permissionsGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private void showRationale() {
        Log.d(TAG, "showRationale");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.rationale_title))
                .setIcon(R.drawable.rationale_icon)
                .setMessage(R.string.location_permission_rationale)
                .setPositiveButton(R.string.location_permission_rationale_positive_button, null)
                .setOnDismissListener(dialog -> {
                    requestPermissions();
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        //        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                SearchSuggestionsProvider.setCurrentLocationSuggestionEnabled(true);
                viewModel.onRequestPermissionsResult(true);

            } else {
//                Log.d(TAG, "PERMISSION_NOT_GRANTED onRequestPermissionsResult");
//                SearchSuggestionsProvider.setCurrentLocationSuggestionEnabled(false);
                viewModel.onRequestPermissionsResult(false);
                if (!shouldShowRequestPermissionRationale()) {
                    showPermissionDeniedWarning();
                }
            }
        }
    }

    private void showPermissionDeniedWarning() {
        Log.d(TAG, "showPermissionDeniedWarning");
        //show you will not be able to automatically receive weather for your location until you manually grant the location permission!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_denied_warning_title))
                .setIcon(R.drawable.location_permission_denied_warning_icon)
                .setMessage(R.string.location_permission_denied_warning)
                .setPositiveButton(R.string.location_permission_warning_positive_button, null)
                .show();
    }

    private void showFeedbackFragment() {
        if (feedbackFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(feedbackFragment)
                    .commit();
            getSupportFragmentManager().popBackStackImmediate("FeedbackFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        feedbackFragment = FeedbackFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main, feedbackFragment, null)
                .addToBackStack("FeedbackFragment")
                .commit();
    }


    private void showAboutDialog() {
        aboutFragment = AboutFragment.newInstance();
        aboutFragment.setListener(() -> {
            if (aboutFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(aboutFragment)
                        .commit();
            }
            navigationView.getMenu().findItem(R.id.nav_drawer_about).setChecked(false);
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
        settingsFragment = SettingsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main, settingsFragment, null)
                .addToBackStack("SettingsFragment")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        initOptionsMenuListeners(searchView);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));

        searchView.setIconifiedByDefault(true);

        return true;
    }

    private void initOptionsMenuListeners(SearchView searchView) {
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

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                search.collapseActionView();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        });
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
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ");
        checkPermissions();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop ");
        viewModel.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "isFinishing " + isFinishing());
        Toothpick.closeScope(MainActivityScope.class);
        viewModel.onDestroy(isFinishing());
        super.onDestroy();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case CurrentWeatherFragment.ID_FOR_VIEW_PAGER:
                    return getString(R.string.tab_today_title);
                case ForecastedWeatherFragment.ID_FOR_VIEW_PAGER:
                    return getString(R.string.tab_five_days);
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CurrentWeatherFragment.ID_FOR_VIEW_PAGER:
                    return currentWeatherFragment = CurrentWeatherFragment.newInstance();
//                case ForecastedWeatherFragment.ID_FOR_VIEW_PAGER:
//                    return forecastedWeatherFragment = ForecastedWeatherFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1/*NUMBER_OF_PAGES*/;
        }
    }
}
