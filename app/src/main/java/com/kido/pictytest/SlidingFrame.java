package com.kido.pictytest;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;


public class SlidingFrame extends RelativeLayout {

    public SlidingFrame(Context context) {
        super(context);
    }

    public SlidingFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        return (width == 0) ? 0 : getX() / width;
    }

    public void setXFraction(float xFraction) {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        setX((width > 0) ? (xFraction * width) : 0);
    }

    public float getYFraction() {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float height = size.y;
        return (height == 0) ? 0 : getY() / height;
    }

    public void setYFraction(float yFraction) {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float height = size.y;
        setY((height > 0) ? (yFraction * height) : 0);
    }
}
