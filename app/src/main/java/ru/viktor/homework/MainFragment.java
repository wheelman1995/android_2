package ru.viktor.homework;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();

//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

//         Create the observer which updates the UI.
        Observer<WorkStatus> observer = new Observer<WorkStatus>() {
            @Override
            public void onChanged(@Nullable WorkStatus status) {
                // Update the UI
                if (status != null && status.getState().isFinished()) {
                    String data = status.getOutputData().getString("data");
                    Log.d(TAG, data);
                }
            }
        };

//        Observer<List<WorkStatus>> observer = new Observer<List<WorkStatus>>() {
//            @Override
//            public void onChanged(@Nullable List<WorkStatus> statuses) {
//                // Update the UI
//                for (int i = 0; i < statuses.size(); i++) {
//                    if (statuses.get(i) != null)
//                    Log.d(TAG, statuses.get(i).getOutputData().getString("data"));
//                }
//            }
//        };

        OneTimeWorkRequest weatherUpdateWork = new OneTimeWorkRequest.Builder(WeatherUpdateWorker.class)
                .setInputData(new Data.Builder().putString("city", "pskov").build())
                .build();

//        PeriodicWorkRequest.Builder weatherUpdateWorkBuilder = new PeriodicWorkRequest.Builder(WeatherUpdateWorker.class, 15, TimeUnit.MINUTES).addTag("tag");
//        weatherUpdateWorkBuilder.setInputData(new Data.Builder().putString("city", "pskov").build());
//        PeriodicWorkRequest weatherUpdateWork = weatherUpdateWorkBuilder.build();
        WorkManager.getInstance().enqueue(weatherUpdateWork);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer
        WorkManager.getInstance().getStatusById(weatherUpdateWork.getId())
                .observe(this, observer);
//        WorkManager.getInstance().getStatusesByTag("tag")
//                .observe(this, observer);

//        Intent intent = new Intent(getApplicationContext(), WeatherUpdateService.class);
//        WeatherUpdateService.enqueueWork(this, intent);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
