package ru.wheelman.weather;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder> {
    private String[] dataSet;
    private Callback callback;

    public SettingsRecyclerViewAdapter(String[] dataSet, Callback callback) {
        this.callback = callback;
        this.dataSet = dataSet;
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

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        callback.onViewAttachedToWindow(holder.getLayoutPosition());
    }

    public interface Callback {
        void onListItemCheckedChanged(int i);

        void onViewAttachedToWindow(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RadioButton rb;


        public ViewHolder(@NonNull RadioButton rb) {
            super(rb);
            this.rb = rb;
        }
    }
}
