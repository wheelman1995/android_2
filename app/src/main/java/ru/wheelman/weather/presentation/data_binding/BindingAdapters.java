package ru.wheelman.weather.presentation.data_binding;

import android.graphics.Matrix;
import android.net.Uri;

import javax.inject.Inject;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;
import ru.wheelman.weather.di.scopes.ApplicationScope;
import ru.wheelman.weather.presentation.utils.ImageLoader;
import ru.wheelman.weather.presentation.utils.PicassoImageLoader;

@ApplicationScope
public class BindingAdapters implements IBindingAdapters {
    private ImageLoader<AppCompatImageView> imageLoader;

    @Inject
    public BindingAdapters() {
        imageLoader = new PicassoImageLoader();
    }

    @BindingAdapter(value = "app:imageURIwithBottomCrop")
    public void setImageURIwithBottomCrop(AppCompatImageView imageView, Uri uri) {
        if (uri != null) {
            imageView.setImageURI(uri);

            if (imageView.getDrawable() != null) {

                Matrix matrix = new Matrix(imageView.getImageMatrix());

                float scale;
                float viewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                float viewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
                float drawableWidth = imageView.getDrawable().getIntrinsicWidth();
                float drawableHeight = imageView.getDrawable().getIntrinsicHeight();

                //crop either image start and end equally, or only the bottom
                if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
                    scale = viewHeight / drawableHeight;
                    float dx = -(drawableWidth * scale - viewWidth) / 2f;
                    matrix.postTranslate(dx, 0f);
                } else {
                    scale = viewWidth / drawableWidth;
                }
                //
                matrix.setScale(scale, scale);

                imageView.setImageMatrix(matrix);
            }
        }
    }

    @BindingAdapter(value = "app:imageURL")
    public void loadImage(AppCompatImageView imageView, String url) {
        imageLoader.loadImage(url, imageView);
    }

    @Override
    public BindingAdapters getBindingAdapters() {
        return this;
    }
}
