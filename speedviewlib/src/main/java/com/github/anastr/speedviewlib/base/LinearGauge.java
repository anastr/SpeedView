package com.github.anastr.speedviewlib.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.R;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public abstract class LinearGauge extends Gauge {

    /** the shape */
    protected Path path = new Path();

    protected Paint frontPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
            , backPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
            , paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** to draw part of bitmap */
    private Rect rect = new Rect();
    private Bitmap foregroundBitmap;

    /** horizontal or vertical direction */
    private Orientation orientation = Orientation.HORIZONTAL;

    public LinearGauge(Context context) {
        this(context, null);
    }

    public LinearGauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearGauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /** to reset {@link #path} for horizontal shape. */
    protected abstract void updateHorizontalPath();
    /** to reset {@link #path} for vertical shape. */
    protected abstract void updateVerticalPath();

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LinearGauge, 0, 0);

        int orientation = a.getInt(R.styleable.LinearGauge_sv_orientation, -1);
        if (orientation != -1)
            setOrientation(Orientation.values()[orientation]);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (orientation == Orientation.HORIZONTAL) {
            if (h > w/2)
                setMeasuredDimension(w, w/2);
            else
                setMeasuredDimension(h*2, h);
        }
        else {
            if (w > h/2)
                setMeasuredDimension(h/2, h);
            else
                setMeasuredDimension(w, w*2);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateBackgroundBitmap();
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        updateOrientation();
        updateForegroundBitmap();

        c.translate(getPadding(), getPadding());
        c.drawPath(path, backPaint);
    }

    private void updateOrientation() {
        if (getOrientation() == Orientation.HORIZONTAL)
            updateHorizontalPath();
        else
            updateVerticalPath();
    }

    private void updateForegroundBitmap() {
        foregroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(foregroundBitmap);
        c.drawPath(path, frontPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (orientation == Orientation.HORIZONTAL)
            rect.set(0, 0
                    , (int)(getWidthPa() * getOffsetSpeed()), getHeightPa());
        else
            rect.set(0, getHeightPa() - (int)(getHeightPa() * getOffsetSpeed())
                    , getWidthPa(), getHeightPa());

        canvas.translate(getPadding(), getPadding());
        canvas.drawBitmap(foregroundBitmap, rect, rect, paint);
        canvas.translate(-getPadding(), -getPadding());

        drawSpeedUnitText(canvas);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * change fill orientation,
     * this will change view width and height.
     * @param orientation new orientation.
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        if (!isAttachedToWindow())
            return;
        requestLayout();
        updateBackgroundBitmap();
        invalidate();
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }
}
