package ru.wheelman.weather.presentation.view.fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.wheelman.weather.R;

public class SettingsFragmentAdapter extends RecyclerView.Adapter<SettingsFragmentAdapter.ViewHolder> {
    private String[] dataSet;
    private int unitIndex;
    private Callback callback;

    public SettingsFragmentAdapter(String[] dataSet, Callback callback, int unitIndex) {
        this.callback = callback;
        this.dataSet = dataSet;
        this.unitIndex = unitIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RadioButton rb = (RadioButton) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(rb);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (i == unitIndex) {
            viewHolder.rb.setChecked(true);
        }
        viewHolder.rb.setText(dataSet[i]);
        viewHolder.rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                callback.onListItemCheckedChanged(viewHolder.getLayoutPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public interface Callback {
        void onListItemCheckedChanged(int i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RadioButton rb;


        public ViewHolder(@NonNull RadioButton rb) {
            super(rb);
            this.rb = rb;
        }
    }
}
