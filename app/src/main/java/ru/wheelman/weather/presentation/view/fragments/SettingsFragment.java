package ru.wheelman.weather.presentation.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.wheelman.weather.R;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.domain.entities.Units;
import ru.wheelman.weather.presentation.utils.PreferenceHelper;
import ru.wheelman.weather.presentation.utils.UpdateMethodSelector;
import toothpick.Toothpick;

public class SettingsFragment extends Fragment implements SettingsFragmentAdapter.Callback {

    public static final int UPDATE_INTERVAL_MILLISECONDS = 1000 * 60 * 60; // 60 min;

    @Inject
    PreferenceHelper preferenceHelper;
    @Inject
    UpdateMethodSelector updateMethodSelector;
    private int unitIndex;
    private RecyclerView tempSettingRV;
    private SettingsFragmentAdapter adapter;

    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();

//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);

        return f;
    }

    @Override
    public void onListItemCheckedChanged(int i) {
        for (int j = 0; j < tempSettingRV.getChildCount(); j++) {
            if (i != j) {
                ((RadioButton) tempSettingRV.getChildAt(j)).setChecked(false);
            }
        }
        preferenceHelper.setUnits(Units.getUnitByIndex(i));
        updateMethodSelector.selectAndUpdate();

//        settingsChangedListener.onTemperatureUnitsChanged(i);
    }

    @Override
    public void onViewAttachedToWindow(int position) {
        if (position == unitIndex) {
            ((RadioButton) tempSettingRV.getChildAt(position)).setChecked(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Toothpick.inject(this, Toothpick.openScope(ApplicationScope.class));

        tempSettingRV = view.findViewById(R.id.rv_temp_setting);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        tempSettingRV.setHasFixedSize(true);

        tempSettingRV.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));

        adapter = new SettingsFragmentAdapter(getResources().getStringArray(R.array.temp_setting_values), this);
        tempSettingRV.setAdapter(adapter);
        tempSettingRV.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        unitIndex = preferenceHelper.getUnits().getUnitIndex();

        return view;
    }
}
