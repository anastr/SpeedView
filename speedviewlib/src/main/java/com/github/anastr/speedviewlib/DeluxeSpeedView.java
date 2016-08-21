package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;
import java.util.Random;

public class DeluxeSpeedView extends Speed {

    private Path indicatorPath, markPath, smallMarkPath;
    private Paint circlePaint, centerCirclePaint, indicatorPaint
            , speedometerPaint, markPaint, smallMarkPaint, speedBackgroundPaint;
    private TextPaint speedTextPaint, textPaint;
    private RectF speedometerRect, speedBackgroundRect;
    private int speedBackgroundColor = Color.WHITE
            , speedTextColor = Color.BLACK;

    private boolean canceled = false;
    private final int MIN_DEGREE = 135, MAX_DEGREE = 135+270;
    /** to rotate indicator */
    private float degree = MIN_DEGREE;
    private int speed = 0;
    private ValueAnimator speedAnimator, trembleAnimator;

    private boolean withEffects = true;

    public DeluxeSpeedView(Context context) {
        super(context);
        init();
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (width > height) ? height : width;
        setMeasuredDimension(size, size);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DeluxeSpeedView, 0, 0);

        setIndicatorColor(a.getColor(R.styleable.DeluxeSpeedView_indicatorColor, Color.parseColor("#00ffec")));
        setCenterCircleColor(a.getColor(R.styleable.DeluxeSpeedView_centerCircleColor, Color.parseColor("#e0e0e0")));
        setMarkColor(a.getColor(R.styleable.DeluxeSpeedView_markColor, getMarkColor()));
        setLowSpeedColor(a.getColor(R.styleable.DeluxeSpeedView_lowSpeedColor, Color.parseColor("#37872f")));
        setMediumSpeedColor(a.getColor(R.styleable.DeluxeSpeedView_mediumSpeedColor, Color.parseColor("#a38234")));
        setHighSpeedColor(a.getColor(R.styleable.DeluxeSpeedView_highSpeedColor, Color.parseColor("#9b2020")));
        setTextColor(a.getColor(R.styleable.DeluxeSpeedView_textColor, Color.WHITE));
        setBackgroundCircleColor(a.getColor(R.styleable.DeluxeSpeedView_backgroundCircleColor, Color.parseColor("#212121")));
        speedTextColor = a.getColor(R.styleable.DeluxeSpeedView_speedTextColor, speedTextColor);
        speedBackgroundColor = a.getColor(R.styleable.DeluxeSpeedView_speedBackgroundColor, speedBackgroundColor);
        setSpeedometerWidth(a.getDimension(R.styleable.DeluxeSpeedView_speedometerWidth, getSpeedometerWidth()));
        setMaxSpeed(a.getInt(R.styleable.DeluxeSpeedView_maxSpeed, getMaxSpeed()));
        setWithTremble(a.getBoolean(R.styleable.DeluxeSpeedView_withTremble, isWithTremble()));
        setWithBackgroundCircle(a.getBoolean(R.styleable.DeluxeSpeedView_withBackgroundCircle, isWithBackgroundCircle()));
        withEffects = a.getBoolean(R.styleable.DeluxeSpeedView_withEffects, withEffects);
        setSpeedTextSize(a.getDimension(R.styleable.DeluxeSpeedView_speedTextSize, getSpeedTextSize()));
        String unit = a.getString(R.styleable.DeluxeSpeedView_unit);
        a.recycle();
        setUnit( (unit != null) ? unit : getUnit() );
        setWithEffects(withEffects);
    }

    private void init() {
        indicatorPath = new Path();
        markPath = new Path();
        smallMarkPath = new Path();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        speedBackgroundPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        speedometerRect = new RectF();
        speedBackgroundRect = new RectF();

        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        smallMarkPaint.setStyle(Paint.Style.STROKE);
        speedTextPaint.setTextAlign(Paint.Align.CENTER);

        speedAnimator = ValueAnimator.ofFloat(0f, 1f);
        trembleAnimator = ValueAnimator.ofFloat(0f, 1f);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.moveTo(w/2f, h/5f);
        indicatorPath.lineTo(w/2f -indW, h*3f/5f);
        indicatorPath.lineTo(w/2f +indW, h*3f/5f);
        RectF rectF = new RectF(w/2f -indW, h*3f/5f -indW, w/2f +indW, h*3f/5f +indW);
        indicatorPath.addArc(rectF, 0f, 180f);
        indicatorPath.moveTo(0f, 0f);

        float markH = h/28f;
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
        markPaint.setStrokeWidth(markH/3f);

        float smallMarkH = h/20f;
        smallMarkPath.moveTo(w/2f, getSpeedometerWidth());
        smallMarkPath.lineTo(w/2f, getSpeedometerWidth() + smallMarkH);
        smallMarkPath.moveTo(0f, 0f);
        smallMarkPaint.setStrokeWidth(3);
    }

    private void initDraw() {
        indicatorPaint.setColor(getIndicatorColor());
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        smallMarkPaint.setColor(getMarkColor());
        speedTextPaint.setColor(speedTextColor);
        speedTextPaint.setTextSize(getSpeedTextSize());
        textPaint.setColor(getTextColor());
        speedBackgroundPaint.setColor(speedBackgroundColor);
        centerCirclePaint.setColor(getCenterCircleColor());
        circlePaint.setColor(getBackgroundCircleColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        if (isWithBackgroundCircle())
            canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/2f, circlePaint);

        speedometerPaint.setColor(getLowSpeedColor());
        canvas.drawArc(speedometerRect, 135f, 160f, false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        canvas.drawArc(speedometerRect, 135f+160f, 75f, false, speedometerPaint);
        speedometerPaint.setColor(getHighSpeedColor());
        canvas.drawArc(speedometerRect, 135f+160f+75f, 35f, false, speedometerPaint);

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i <= 345; i+=30) {
            canvas.rotate(30f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(markPath, markPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i < 395; i+=10) {
            canvas.rotate(10f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(smallMarkPath, smallMarkPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(90f +degree, getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/12f, centerCirclePaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("00", getWidth()/5f, getHeight()*6/7f, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.getDefault(), "%d", getMaxSpeed()), getWidth()*4/5f, getHeight()*6/7f, textPaint);
        String sSpeed = String.format(Locale.getDefault(), "%.1f"
                , (degree-MIN_DEGREE) * getMaxSpeed()/(MAX_DEGREE-MIN_DEGREE)) +getUnit();
        speedBackgroundRect.set(getWidth()/2f - (speedTextPaint.measureText(sSpeed)/2f) -5
                , speedometerRect.bottom - speedTextPaint.getTextSize()
                , getWidth()/2f + (speedTextPaint.measureText(sSpeed)/2f) +5
                , speedometerRect.bottom + 4);
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);
        canvas.drawText(String.format(Locale.getDefault(), "%.1f"
                , (degree-MIN_DEGREE) * getMaxSpeed()/(MAX_DEGREE-MIN_DEGREE)) +getUnit()
                , getWidth()/2f, speedometerRect.bottom, speedTextPaint);
    }

    private void cancel() {
        cancelSpeedMove();
        cancelTremble();
    }

    private void cancelTremble() {
        canceled = true;
        trembleAnimator.cancel();
        canceled = false;
    }

    private void cancelSpeedMove() {
        canceled = true;
        speedAnimator.cancel();
        canceled = false;
    }

    @Override
    public void speedPercentTo(int percent) {
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * getMaxSpeed() / 100);
    }

    @Override
    public void speedToDef() {
        speedTo(speed, 2000);
    }

    @Override
    public void speedTo(int speed) {
        speedTo(speed, 2000);
    }

    @Override
    public void speedTo(int speed, long moveDuration) {
        speed = (speed > getMaxSpeed()) ? getMaxSpeed() : (speed < 0) ? 0 : speed;
        this.speed = speed;

        float newDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /getMaxSpeed() +MIN_DEGREE;
        if (newDegree == degree)
            return;

        cancel();
        speedAnimator = ValueAnimator.ofFloat(degree, newDegree);
        speedAnimator.setInterpolator(new DecelerateInterpolator());
        speedAnimator.setDuration(moveDuration);
        speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) speedAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        speedAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        speedAnimator.start();
    }

    private void tremble() {
        cancelTremble();
        if (!isWithTremble())
            return;
        Random random = new Random();
        float mad = 4*random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        float originalDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /getMaxSpeed() +MIN_DEGREE;
        mad = (originalDegree+mad > MAX_DEGREE) ? MAX_DEGREE - originalDegree
                : (originalDegree+mad < MIN_DEGREE) ? MIN_DEGREE - originalDegree : mad;
        trembleAnimator = ValueAnimator.ofFloat(degree, originalDegree +mad);
        trembleAnimator.setInterpolator(new DecelerateInterpolator());
        trembleAnimator.setDuration(1000);
        trembleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) trembleAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        trembleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled)
                    tremble();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        trembleAnimator.start();
    }

    @Override
    public int getPercentSpeed() {
        return speed * 100 / getMaxSpeed();
    }

    @Override
    public void setSpeedometerWidth(float speedometerWidth) {
        super.setSpeedometerWidth(speedometerWidth);
        float risk = speedometerWidth/2f;
        speedometerRect.set(risk, risk, getWidth() -risk, getHeight() -risk);
        invalidate();
    }

    @Override
    public void setWithTremble(boolean withTremble) {
        super.setWithTremble(withTremble);
        tremble();
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        if (withEffects) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
            centerCirclePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
            centerCirclePaint.setMaskFilter(null);
        }
        invalidate();
    }

    public int getSpeedBackgroundColor() {
        return speedBackgroundColor;
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        this.speedBackgroundColor = speedBackgroundColor;
        invalidate();
    }

    public int getSpeedTextColor() {
        return speedTextColor;
    }

    public void setSpeedTextColor(int speedTextColor) {
        this.speedTextColor = speedTextColor;
        invalidate();
    }
}
