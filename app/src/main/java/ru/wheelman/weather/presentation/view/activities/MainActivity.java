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

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
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
import ru.wheelman.weather.presentation.utils.DisplayMetricsHelper;
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
    private static final float DRAWER_ARROW_DRAWABLE_PROGRESS_BACK_ARROW = 1.0f;
    private static final float DRAWER_ARROW_DRAWABLE_PROGRESS_HAMBURGER = 0.0f;
    @Inject
    DisplayMetricsHelper displayMetricsHelper;
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
    //    private ImageView navDrawerHeaderForeground;
//    private ConstraintLayout navDrawerHeaderLayout;
//    private MenuItem search;
//    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    //    private TabLayout tabLayout;
    private CurrentWeatherFragment currentWeatherFragment;
    private FeedbackFragment feedbackFragment;
    private ForecastedWeatherFragment forecastedWeatherFragment;
    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private DrawerLayout.DrawerListener drawerListener;
    private ActivityMainBinding binding;
    private NavDrawerHeaderBinding navDrawerHeaderBinding;
    private DrawerArrowDrawable drawerArrowDrawable;
    private ObservableInt viewPagerCurrentItem;

    public ObservableInt getViewPagerCurrentItem() {
        return viewPagerCurrentItem;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToothpick();

        initDataBinding();

        initActionBar();

        checkPermissions();
    }

    private void checkPermissions() {
        boolean permissionsGranted = permissionsGranted();

        viewModel.onCreate(permissionsGranted);

        if (!permissionsGranted) {
            if (shouldShowRequestPermissionRationale()) {
                showRationale();
            } else {
                requestPermissions();
            }
        }
    }

    private void initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        navDrawerHeaderBinding = NavDrawerHeaderBinding.bind(binding.navDrawerView.getHeaderView(0));

        initUi(binding);

        initVariables();

        initListeners();

        binding.setState(viewModel.getScreenState());
        binding.setMainActivity(this);
        binding.setViewPager(binding.viewPager);
        navDrawerHeaderBinding.setState(viewModel.getScreenState());
    }

    private void initVariables() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        drawerArrowDrawable = new DrawerArrowDrawable(this);
        viewPagerCurrentItem = new ObservableInt();
    }

    private void initUi(ActivityMainBinding binding) {
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navDrawerView;
        setSupportActionBar(binding.myToolbar);
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(null);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(drawerArrowDrawable);
    }


    private void initToothpick() {
        Scope scope = Toothpick.openScopes(ApplicationScope.class, MainActivityScope.class);
        scope.installModules(new MainActivityModule(this));
        Toothpick.inject(this, scope);
    }

    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return navigationItemSelectedListener;
    }

    public ViewPager.OnPageChangeListener getPageChangeListener() {
        return pageChangeListener;
    }

    public DrawerLayout.DrawerListener getDrawerListener() {
        return drawerListener;
    }

    private void initListeners() {

        viewModel.isInternetConnected().observe(this, internetConnected -> {
            if (internetConnected != null && !internetConnected) {
                Snackbar.make(binding.clActivityMain, R.string.no_internet, Snackbar.LENGTH_LONG).show();
            }
        });

        navigationItemSelectedListener = item -> {
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
        };

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            if (feedbackFragment != null && feedbackFragment.isVisible() || settingsFragment != null && settingsFragment.isVisible()) {
//                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                drawerArrowDrawable.setProgress(DRAWER_ARROW_DRAWABLE_PROGRESS_BACK_ARROW);
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

            drawerArrowDrawable.setProgress(DRAWER_ARROW_DRAWABLE_PROGRESS_HAMBURGER);
            homeReturnsUp = false;
//            getSupportActionBar().setHomeAsUpIndicator(drawerArrowDrawable);

        });

        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "positionOffset: " + positionOffset + " position " + position);

                setPosition(position, positionOffset);
            }

            private void setPosition(int position, float positionOffset) {
                if (position == 1) {
                    drawerArrowDrawable.setProgress(1f);
                } else {
                    drawerArrowDrawable.setProgress(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                homeReturnsUp = position > 0;
                viewPagerCurrentItem.set(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        drawerListener = new DrawerLayout.DrawerListener() {
            private AppCompatImageView navDrawerHeaderForegroundImage = navDrawerHeaderBinding.imageNavDrawerHeaderForeground;

            private ConstraintLayout.LayoutParams foregroundImageLayoutParams = (ConstraintLayout.LayoutParams) navDrawerHeaderForegroundImage.getLayoutParams();

            private int foregroundImageWidth = foregroundImageLayoutParams.width; //in pixels

            private int foregroundImageOriginalY = foregroundImageLayoutParams.editorAbsoluteY; // in pixels
            private int foregroundImageOriginalX = foregroundImageLayoutParams.editorAbsoluteX; // in pixels

            private int foregroundImageEndX = navigationView.getLayoutParams().width / 2 - foregroundImageWidth / 2;
            private int foregroundImageEndY = displayMetricsHelper.dpToPx(20);

            private int h = foregroundImageEndX;
            private int k = (foregroundImageEndX * foregroundImageEndX - 2 * foregroundImageEndX * h + foregroundImageEndY * foregroundImageEndY - foregroundImageOriginalX * foregroundImageOriginalX + 2 * foregroundImageOriginalX * h - foregroundImageOriginalY * foregroundImageOriginalY) / (-2 * foregroundImageOriginalY + 2 * foregroundImageEndY);

            private double r = Math.sqrt(Math.pow(foregroundImageOriginalX - h, 2) + Math.pow(foregroundImageOriginalY - k, 2));

            private int foregroundImageDistanceX = foregroundImageOriginalX - foregroundImageEndX;

            //            int yDist = foregroundImageOriginalY - foregroundImageEndY;
//            int aX = foregroundImageOriginalX - (foregroundImageOriginalX - foregroundImageEndX) * 2;
//            int aY = foregroundImageOriginalY;
//
//            int k = ((-2 * foregroundImageEndX + 2 * aX) * (foregroundImageEndX * foregroundImageEndX + foregroundImageEndY * foregroundImageEndY - foregroundImageOriginalX * foregroundImageOriginalX - foregroundImageOriginalY * foregroundImageOriginalY) - 2 * foregroundImageEndX * aX *aX - 2*foregroundImageEndX*aY * aY + 2*foregroundImageEndX*foregroundImageEndX*foregroundImageEndX+2*foregroundImageEndX*foregroundImageEndY*foregroundImageEndY+2*foregroundImageOriginalX*aX*aX + 2*foregroundImageOriginalX*aY*aY - 2*foregroundImageOriginalX*foregroundImageEndX*foregroundImageEndX-2*foregroundImageOriginalX*foregroundImageEndY*foregroundImageEndY) / (2*foregroundImageOriginalY*2*foregroundImageEndX-2*foregroundImageOriginalY*2*aX-2*foregroundImageEndY*2*foregroundImageEndX+2*foregroundImageEndY*2*aX+2*foregroundImageEndX*2*foregroundImageEndY-2*foregroundImageEndX*2*aY+2*foregroundImageOriginalX*2*aY - 2*foregroundImageOriginalX*2*foregroundImageEndY);
//            int h = (aX*aX + aY*aY - 2*aY*k-foregroundImageEndX*foregroundImageEndX-foregroundImageEndY*foregroundImageEndY+2*foregroundImageEndY*k)/(-2*foregroundImageEndX + 2*aX);
//            int foregroundHeight = foregroundImageLayoutParams.height; //in pixels

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                float targetX = foregroundImageOriginalX - foregroundImageDistanceX * slideOffset;
                double d = 2d * k * 2d * k - 4d * (k * k - r * r + (Math.pow(targetX - h, 2d)));
                float targetY = (float) ((2f * k - Math.sqrt(d)) / 2f);

                navDrawerHeaderForegroundImage.setX(targetX);
                navDrawerHeaderForegroundImage.setY(targetY);

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
        };
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
                .setOnDismissListener(dialog -> requestPermissions());
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.onRequestPermissionsResult(true);

            } else {
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

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        initOptionsMenuListeners(search, searchView);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));

        searchView.setIconifiedByDefault(true);

        return true;
    }

    private void initOptionsMenuListeners(MenuItem search, SearchView searchView) {
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
                    drawerArrowDrawable.setProgress(DRAWER_ARROW_DRAWABLE_PROGRESS_HAMBURGER);
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
        } else if (viewPagerCurrentItem.get() > 0) {
            viewPagerCurrentItem.set(viewPagerCurrentItem.get() - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ");
        viewModel.onStart(permissionsGranted());
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

    public class PagerAdapter extends FragmentPagerAdapter {

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
                case ForecastedWeatherFragment.ID_FOR_VIEW_PAGER:
                    return forecastedWeatherFragment = ForecastedWeatherFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }
}
