package ru.wheelman.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        tempSettingRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        adapter = new SettingsRecyclerViewAdapter(getResources().getStringArray(R.array.temp_setting_values), this);
        tempSettingRV.setAdapter(adapter);
        tempSettingRV.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

//        String[] units = getResources().getStringArray(R.array.temp_setting_values);
//        for (int i = 0; i < units.length; i++) {
//            if (units[i].equals())
//        }
        unitIndex = ((MainActivity) getActivity()).getTemperatureUnits().getUnitIndex();


        return view;
    }


    public interface SettingsChangedListener {
        void onTemperatureUnitsChanged(int unit);
    }

}
