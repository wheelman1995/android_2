package ru.wheelman.weather.presentation.utils;

import com.squareup.picasso.Picasso;

import androidx.appcompat.widget.AppCompatImageView;

public class PicassoImageLoader implements ImageLoader<AppCompatImageView> {
    @Override
    public void loadImage(String url, AppCompatImageView container) {
        Picasso.get().load(url).into(container);
    }
}
