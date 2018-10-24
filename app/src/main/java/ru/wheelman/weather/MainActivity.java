package ru.wheelman.weather;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsChangedListener {

    static final int NUMBER_OF_PAGES = 2;
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private SettingsFragment settingsFragment;

    //    private MainFragment mainFragment;
    private NavigationView navigationView;
    private boolean homeReturnsUp;
    private AboutFragment aboutFragment;
    private ImageView navDrawerHeaderForeground;
    private ImageView navDrawerHeaderBackground;
    private ConstraintLayout navDrawerHeaderLayout;
    private MenuItem search;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter searchableActivityIntentFilter;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout.Tab tabItemToday;
    private TabLayout.Tab tabItemFiveDays;
    private TabLayout tabLayout;
    private MainFragment mainFragment;

    public MainFragment getMainFragment() {
        return mainFragment;
    }

    public void setNavDrawerHeaderContent(long sunset, long sunrise) {
        long currentUTC = Calendar.getInstance().getTimeInMillis();
        sunset *= 1000L;// to milliseconds
        sunrise *= 1000L;// to milliseconds
//            Log.d(TAG, "cur: " + currentUTC + " sunset " + sunset + " sunrise " + sunrise);
        //day
        if (sunrise < currentUTC && currentUTC < sunset) {
//            Log.d(TAG, "day");
            navDrawerHeaderBackground.setImageResource(R.drawable.day);
            navDrawerHeaderForeground.setImageResource(R.drawable.sun);
        } else {
            //night
//            Log.d(TAG, "night");
            navDrawerHeaderBackground.setImageResource(R.drawable.night);
            navDrawerHeaderForeground.setImageResource(R.drawable.crescent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();

        initListeners();

        initCityListDatabase();

        initActionBar();
    }

    private void initCityListDatabase() {
        CityListDatabase.init(getApplication());
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
        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navDrawerHeaderLayout = (ConstraintLayout) navigationView.getHeaderView(0);
        navDrawerHeaderForeground = navDrawerHeaderLayout.findViewById(R.id.image_nav_drawer_header_foreground);
        searchableActivityIntentFilter = new IntentFilter(SearchableActivity.ACTION_FINISH);
        navDrawerHeaderBackground = navDrawerHeaderLayout.findViewById(R.id.image_nav_drawer_header_background);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        tabLayout = viewPager.findViewById(R.id.tl_main);
        tabItemToday = tabLayout.getTabAt(MainFragment.ID_FOR_VIEW_PAGER);
        tabItemFiveDays = tabLayout.getTabAt(ForecastFragment.ID_FOR_VIEW_PAGER);
        viewPager.setAdapter(pagerAdapter);
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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                search.collapseActionView();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, searchableActivityIntentFilter);

//        tabItemToday.
//        tabItemToday.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager.setCurrentItem(MainFragment.ID_FOR_VIEW_PAGER, true);
//            }
//        });
//        tabItemFiveDays.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewPager.setCurrentItem(ForecastFragment.ID_FOR_VIEW_PAGER, true);
//            }
//        });
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

        search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        initOptionsMenuListeners(searchView);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    search.collapseActionView();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
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
    }


    public Units getTemperatureUnits() {
        return Units.getUnitByIndex(getSharedPreferences(Constants.MAIN_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(Constants.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, Units.CELSIUS.getUnitIndex()));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case MainFragment.ID_FOR_VIEW_PAGER:
                    return getString(R.string.tab_today_title);
                case ForecastFragment.ID_FOR_VIEW_PAGER:
                    return getString(R.string.tab_five_days);
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MainFragment.ID_FOR_VIEW_PAGER:
                    return mainFragment = MainFragment.newInstance();
                case ForecastFragment.ID_FOR_VIEW_PAGER:
                    return ForecastFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
    }
}
