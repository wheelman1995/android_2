package ru.wheelman.weather.presentation.view.fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import ru.wheelman.weather.databinding.RecyclerViewFragmentForecastItemBinding;
import ru.wheelman.weather.di.scopes.ForecastedWeatherFragmentScope;
import ru.wheelman.weather.presentation.data_binding.IBindingAdapters;
import ru.wheelman.weather.presentation.view_model.ForecastedWeatherViewModelImpl.AdapterViewModel;

@ForecastedWeatherFragmentScope
public class ForecastedWeatherFragmentAdapter extends RecyclerView.Adapter<ForecastedWeatherFragmentAdapter.ViewHolder> {

    private static final String TAG = ForecastedWeatherFragmentAdapter.class.getSimpleName();
    private IBindingAdapters bindingComponent;
    private AdapterViewModel adapterViewModel;
    private Disposable disposable;

    @Inject
    public ForecastedWeatherFragmentAdapter(IBindingAdapters bindingComponent, AdapterViewModel adapterViewModel) {
        this.bindingComponent = bindingComponent;
        this.adapterViewModel = adapterViewModel;

        initListeners();
    }

    private void initListeners() {
        disposable = adapterViewModel.getItemCountSubject().subscribe(aBoolean -> {
            Log.d(TAG, "notifyDataSetChanged: ");
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewFragmentForecastItemBinding binding = RecyclerViewFragmentForecastItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false,
                bindingComponent);

        ViewHolder viewHolder = new ViewHolder(binding);

        binding.setAdapter(adapterViewModel);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setPosition(position);
    }


    @Override
    public int getItemCount() {
        return adapterViewModel.getItemCount();
    }

    void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    //todo subscribe to item count changes and notify data set change then.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerViewFragmentForecastItemBinding binding;

        public ViewHolder(RecyclerViewFragmentForecastItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

//class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//    private Listener listener;
//
//    void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    protected Bitmap doInBackground(String... strings) {
//        String url = strings[0];
//        Bitmap bmp = null;
//        InputStream in = null;
//        try {
//            in = new URL(url).openStream();
//            bmp = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return bmp;
//    }
//
//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        if (listener != null)
//            listener.onDownloadImageTaskCompleted(bitmap);
//    }
//
//    interface Listener {
//        void onDownloadImageTaskCompleted(Bitmap bitmap);
//    }
//}
