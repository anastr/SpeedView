package com.github.anastr.speedviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;
import java.util.Random;

public class SpeedView extends View {

    private Path indicatorPath, markPath;
    private Paint paint, speedometerPaint, markPaint;
    private TextPaint speedTextPaint, textPaint;
    private RectF speedometerRect;
    private float speedometerWidth = dpTOpx(30f);
    private int indicatorColor = Color.BLACK
            , centerCircleColor = Color.GRAY
            , markColor = Color.DKGRAY
            , lowSpeedColor = Color.GREEN
            , mediumSpeedColor = Color.YELLOW
            , highSpeedColor = Color.RED
            , textColor = Color.BLACK;
    private float speedTextSize = dpTOpx(18f);

    private boolean canceled = false, withTremble = true;
    private final int MIN_DEGREE = 135, MAX_DEGREE = 135+270;
    /** to rotate indicator */
    private float degree = MIN_DEGREE;
    private int maxSpeed = 100;
    private int speed = 0;
    private ValueAnimator speedAnimator, trembleAnimator;

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0);

        indicatorColor = a.getColor(R.styleable.SpeedView_indicatorColor, indicatorColor);
        centerCircleColor = a.getColor(R.styleable.SpeedView_centerCircleColor, centerCircleColor);
        markColor = a.getColor(R.styleable.SpeedView_markColor, markColor);
        lowSpeedColor = a.getColor(R.styleable.SpeedView_lowSpeedColor, lowSpeedColor);
        mediumSpeedColor = a.getColor(R.styleable.SpeedView_mediumSpeedColor, mediumSpeedColor);
        highSpeedColor = a.getColor(R.styleable.SpeedView_highSpeedColor, highSpeedColor);
        textColor = a.getColor(R.styleable.SpeedView_textColor, textColor);
        speedometerWidth = a.getDimension(R.styleable.SpeedView_speedometerWidth, speedometerWidth);
        maxSpeed = a.getInt(R.styleable.SpeedView_maxSpeed, maxSpeed);
        withTremble = a.getBoolean(R.styleable.SpeedView_withTremble, withTremble);
        speedTextSize = a.getDimension(R.styleable.SpeedView_speedTextSize, speedTextSize);
        a.recycle();
    }

    private void init() {
        indicatorPath = new Path();
        markPath = new Path();

        paint = new Paint();
        speedometerPaint = new Paint();
        markPaint = new Paint();
        speedTextPaint = new TextPaint();
        textPaint = new TextPaint();

        speedometerRect = new RectF();

        paint.setAntiAlias(true);
        speedometerPaint.setAntiAlias(true);
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setAntiAlias(true);
        markPaint.setStyle(Paint.Style.STROKE);
        speedTextPaint.setAntiAlias(true);
        speedTextPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        speedAnimator = ValueAnimator.ofFloat(0f, 1f);
        trembleAnimator = ValueAnimator.ofFloat(0f, 1f);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = speedometerWidth/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.moveTo(w/2f, 0f);
        indicatorPath.lineTo(w/2f -indW, h*2f/3f);
        indicatorPath.lineTo(w/2f +indW, h*2f/3f);
        RectF rectF = new RectF(w/2f -indW, h*2f/3f -indW, w/2f +indW, h*2f/3f +indW);
        indicatorPath.addArc(rectF, 0f, 180f);
        indicatorPath.moveTo(0f, 0f);

        float markH = h/28f;
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
        markPaint.setStrokeWidth(markH/3f);
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(speedometerWidth);
        markPaint.setColor(markColor);
        speedTextPaint.setColor(textColor);
        speedTextPaint.setTextSize(speedTextSize);
        textPaint.setColor(textColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        speedometerPaint.setColor(lowSpeedColor);
        canvas.drawArc(speedometerRect, 135f, 160f, false, speedometerPaint);
        speedometerPaint.setColor(mediumSpeedColor);
        canvas.drawArc(speedometerRect, 135f+160f, 75f, false, speedometerPaint);
        speedometerPaint.setColor(highSpeedColor);
        canvas.drawArc(speedometerRect, 135f+160f+75f, 35f, false, speedometerPaint);

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i <= 345; i+=30) {
            canvas.rotate(30f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(markPath, markPaint);
        }
        canvas.restore();

        paint.setColor(indicatorColor);
        canvas.save();
        canvas.rotate(90f +degree, getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, paint);
        canvas.restore();
        paint.setColor(centerCircleColor);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/12f, paint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("00", getWidth()/6f, getHeight()*7/8f, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.getDefault(), "%d", maxSpeed), getWidth()*5/6f, getHeight()*7/8f, textPaint);
        canvas.drawText(String.format(Locale.getDefault(), "%.1fKm/h", (degree-MIN_DEGREE) * maxSpeed/(MAX_DEGREE-MIN_DEGREE))
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

    /**
     * change speed to percent value.
     * @param percent percent value to change, should be between [0,100].
     */
    public void speedPercentTo(int percent) {
        percent = (percent > 100) ? 100 : (percent < 0) ? 0 : percent;
        speedTo(percent * maxSpeed / 100);
    }

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     */
    public void speedTo(int speed) {
        speedTo(speed, 2000);
    }

    /**
     * <p>change speed to correct {@code int},</p>
     * <p>if {@code speed > maxSpeed} speed will be maxSpeed,</p>
     * if {@code speed < 0} speed will be 0.
     * @param speed correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     */
    public void speedTo(int speed, long moveDuration) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < 0) ? 0 : speed;
        this.speed = speed;

        float newDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
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
        if (!withTremble)
            return;
        Random random = new Random();
        float mad = 4*random.nextFloat() * ((random.nextBoolean()) ? -1 :1);
        float originalDegree = (float)speed * (MAX_DEGREE - MIN_DEGREE) /maxSpeed +MIN_DEGREE;
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

    private float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    public int getPercentSpeed() {
        return speed * 100 / maxSpeed;
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    public void setSpeedometerWidth(float speedometerWidth) {
        this.speedometerWidth = speedometerWidth;
        float risk = speedometerWidth/2f;
        speedometerRect.set(risk, risk, getWidth() -risk, getHeight() -risk);
        invalidate();
    }

    public boolean isWithTremble() {
        return withTremble;
    }

    public void setWithTremble(boolean withTremble) {
        this.withTremble = withTremble;
        tremble();
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed <= 0)
            return;
        this.maxSpeed = maxSpeed;
        speedTo(speed);
        invalidate();
    }

    public float getSpeedTextSize() {
        return speedTextSize;
    }

    public void setSpeedTextSize(float speedTextSize) {
        this.speedTextSize = speedTextSize;
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public int getCenterCircleColor() {
        return centerCircleColor;
    }

    public void setCenterCircleColor(int centerCircleColor) {
        this.centerCircleColor = centerCircleColor;
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        invalidate();
    }

    public int getLowSpeedColor() {
        return lowSpeedColor;
    }

    public void setLowSpeedColor(int lowSpeedColor) {
        this.lowSpeedColor = lowSpeedColor;
        invalidate();
    }

    public int getMediumSpeedColor() {
        return mediumSpeedColor;
    }

    public void setMediumSpeedColor(int mediumSpeedColor) {
        this.mediumSpeedColor = mediumSpeedColor;
        invalidate();
    }

    public int getHighSpeedColor() {
        return highSpeedColor;
    }

    public void setHighSpeedColor(int highSpeedColor) {
        this.highSpeedColor = highSpeedColor;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }
}
