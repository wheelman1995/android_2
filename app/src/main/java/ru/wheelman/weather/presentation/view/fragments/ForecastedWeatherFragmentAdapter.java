package ru.wheelman.weather.presentation.view.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ru.wheelman.weather.R;

public class ForecastedWeatherFragmentAdapter extends RecyclerView.Adapter<ForecastedWeatherFragmentAdapter.ViewHolder> {
    private ArrayList<String> dates;
    private ArrayList<String> weatherConditionDescriptions;
    private ArrayList<String> dayTemperatures;
    private ArrayList<String> nightTemperatures;
    private ArrayList<String> iconURLs;

    public ForecastedWeatherFragmentAdapter(ArrayList<String> dates, ArrayList<String> weatherConditionDescriptions, ArrayList<String> dayTemperatures, ArrayList<String> nightTemperatures, ArrayList<String> iconURLs) {
        this.dates = dates;
        this.weatherConditionDescriptions = weatherConditionDescriptions;
        this.dayTemperatures = dayTemperatures;
        this.nightTemperatures = nightTemperatures;
        this.iconURLs = iconURLs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_fragment_forecast_item, parent, false);
        ViewHolder vh = new ViewHolder(cl);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.setListener((bitmap -> holder.icon.setImageBitmap(bitmap)));
        downloadImageTask.execute(iconURLs.get(position));

        holder.date.setText(dates.get(position));
        holder.weatherConditionDescription.setText(weatherConditionDescriptions.get(position));
        holder.dayTemperature.setText(dayTemperatures.get(position));
        holder.nightTemperature.setText(nightTemperatures.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView weatherConditionDescription;
        TextView dayTemperature;
        TextView nightTemperature;
        ImageView icon;


        public ViewHolder(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.tv_date);
            weatherConditionDescription = view.findViewById(R.id.tv_forecasted_weather_condition_description);
            dayTemperature = view.findViewById(R.id.tv_day_temperature);
            nightTemperature = view.findViewById(R.id.tv_night_temperature);
            icon = view.findViewById(R.id.iv_forecasted_weather_icon);
        }
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private Listener listener;

    void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap bmp = null;
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (listener != null)
            listener.onDownloadImageTaskCompleted(bitmap);
    }

    interface Listener {
        void onDownloadImageTaskCompleted(Bitmap bitmap);
    }
}
