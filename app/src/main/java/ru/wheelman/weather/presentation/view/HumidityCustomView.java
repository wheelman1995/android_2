package ru.wheelman.weather.presentation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import ru.wheelman.weather.R;

public class HumidityCustomView extends View {

    private Drawable drawable;
    private Rect rect;

    public HumidityCustomView(Context context) {
        super(context);
    }

    public HumidityCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public HumidityCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    public HumidityCustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
    }

    private void setRect(int left, int top, int right, int bottom) {
        int dpi = getResources().getDisplayMetrics().densityDpi;
        left = left * (dpi / 160);
        top = top * (dpi / 160);
        right = right * (dpi / 160);
        bottom = bottom * (dpi / 160);
        rect = new Rect(left, top, right, bottom);
        invalidate();
        requestLayout();
    }

    public void setDrawable(int id) {
        this.drawable = getResources().getDrawable(id);
        invalidate();
        requestLayout();
    }

    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HumidityCustomView, 0, 0);

        int width = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width").split("\\.")[0]);
        int height = Integer.parseInt(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height").split("\\.")[0]);

        setRect(0, 0, width, height);

        setDrawable(typedArray.getResourceId(R.styleable.HumidityCustomView_icon_res_id, R.drawable.not_humid));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawable.setBounds(rect);
        drawable.draw(canvas);
    }

}
