package ru.viktor.homework;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class SettingsFragment extends Fragment implements SettingsRecyclerViewAdapter.Callback {


    private int unitIndex;
    private RecyclerView tempSettingRV;
    private SettingsRecyclerViewAdapter adapter;
    private SettingsChangedListener settingsChangedListener;

    public static SettingsFragment newInstance(SettingsChangedListener settingsChangedListener) {
        SettingsFragment f = new SettingsFragment();
        f.settingsChangedListener = settingsChangedListener;

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
        settingsChangedListener.onTemperatureUnitsChanged(i);
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

        tempSettingRV = view.findViewById(R.id.rv_temp_setting);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        tempSettingRV.setHasFixedSize(true);

        tempSettingRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapter = new SettingsRecyclerViewAdapter(getResources().getStringArray(R.array.temp_setting_values), this);
        tempSettingRV.setAdapter(adapter);
        tempSettingRV.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

//        String[] units = getResources().getStringArray(R.array.temp_setting_values);
//        for (int i = 0; i < units.length; i++) {
//            if (units[i].equals())
//        }
        unitIndex = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(MainActivity.SHARED_PREFERENCES_TEMPERATURE_UNIT_KEY, 0);


        return view;
    }

    protected enum Units {
        CELSIUS(0), FAHRENHEIT(1);

        private int unitIndex;

        Units(int unitIndex) {
            this.unitIndex = unitIndex;
        }

        public static Units getUnitByIndex(int unitIndex) {
            for (int i = 0; i < Units.values().length; i++) {
                if (Units.values()[i].unitIndex == unitIndex)
                    return Units.values()[i];
            }
            return null;
        }

        public int getUnitIndex() {
            return unitIndex;
        }
    }


    public interface SettingsChangedListener {
        void onTemperatureUnitsChanged(int unit);
    }

}
