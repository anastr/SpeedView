package com.github.anastr.speedviewlib.components.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public abstract class Note<N extends Note> {

    private float density;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG)
            , backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF noteRect = new RectF();
    private float paddingLeft, paddingTop
            , paddingRight, paddingBottom;
    private Bitmap backgroundBitmap;
    private Position position = Position.CenterIndicator;
    private Align align = Align.Top;
    private int noteW = 0, noteH = 0, containsW = 0, containsH = 0;
    private float cornersRound = 5f;
    private final float triangle_Width;

    public Note (Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        triangle_Width = dpTOpx(12f);
        init();
    }

    private void init() {
        backgroundPaint.setColor(Color.parseColor("#d6d7d7"));
        setPadding(dpTOpx(7f), dpTOpx(7f), dpTOpx(7f), dpTOpx(7f));
    }

    public float dpTOpx(float dp) {
        return dp * density;
    }

    protected abstract void drawContains(Canvas canvas, float centerX, float topY);
    public abstract void build(int viewWidth);

    protected void noticeContainsSizeChange(int containsW, int containsH) {
        this.containsW = containsW;
        this.containsH = containsH;
        if (align.align == Align.Top.align || align.align == Align.Bottom.align) {
            this.noteW = (int) (containsW + paddingLeft + paddingRight);
            this.noteH = (int) (containsH + paddingTop + paddingBottom + triangle_Width);
        }
        else {
//            this.noteW = (int) (containsW + paddingLeft + paddingRight + triangle_Width);
//            this.noteH = (int) (containsH + paddingTop + paddingBottom);
        }
    }

    protected void updateBackgroundBitmap() {
        backgroundBitmap = Bitmap.createBitmap(noteW, noteH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);
        if (align.align == Align.Top.align)
            bitmapTop(c);
        else if (align.align == Align.Bottom.align)
            bitmapBottom(c);
    }

    private void bitmapTop (Canvas c) {
        RectF rectF = new RectF(0f, 0f, noteW, noteH - triangle_Width);
        Path p = new Path();
        p.moveTo(noteW /2f, noteH);
        p.lineTo(noteW /2f - dpTOpx(9f), rectF.bottom - 1);
        p.lineTo(noteW /2f + dpTOpx(9f), rectF.bottom - 1);
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    private void bitmapBottom (Canvas c) {
        RectF rectF = new RectF(0f, 0f + triangle_Width, noteW, noteH);
        Path p = new Path();
        p.moveTo(noteW /2f, 0f);
        p.lineTo(noteW /2f - dpTOpx(9f), rectF.top + 1);
        p.lineTo(noteW /2f + dpTOpx(9f), rectF.top + 1);
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    public void draw(Canvas canvas, float posX, float posY) {
        if (align.align == Align.Top.align) {
            noteRect.set(posX - (noteW / 2f), posY - noteH, posX + (noteW / 2f), posY);
            canvas.drawBitmap(backgroundBitmap, null, noteRect, paint);
            drawContains(canvas, posX, posY - noteH +paddingTop);
        }
        else {
            noteRect.set(posX - (noteW / 2f), posY, posX + (noteW / 2f), posY + noteH);
            canvas.drawBitmap(backgroundBitmap, null, noteRect, paint);
            drawContains(canvas, posX, posY + triangle_Width + paddingTop);
        }
    }

    public int getBackgroundColor() {
        return backgroundPaint.getColor();
    }

    public N setBackgroundColor(int backgroundColor) {
        backgroundPaint.setColor(backgroundColor);
        updateBackgroundBitmap();
        return (N) this;
    }

    public N setAlign (Align align) {
        this.align = align;
        updateBackgroundBitmap();
        return (N) this;
    }

    public float getCornersRound() {
        return cornersRound;
    }

    public N setCornersRound(float cornersRound) {
        if (cornersRound < 0)
            throw new IllegalArgumentException("cornersRound cannot be negative");
        this.cornersRound = cornersRound;
        return (N) this;
    }

    public Align getAlign () {
        return align;
    }

    public N setPosition(Position position) {
        this.position = position;
        return (N) this;
    }

    public Position getPosition() {
        return position;
    }

    private void setPadding(float left, float top, float right, float bottom){
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
        noticeContainsSizeChange(containsW, containsH);
    }

    protected int getContainsW() {
        return containsW;
    }

    protected int getContainsH() {
        return containsH;
    }

    public enum Position {
        TopIndicator(0), CenterIndicator(1), CenterSpeedometer(2);
        public int position;
        Position(int position) {
            this.position = position;
        }
    }

    public enum Align {
        Top(0), Bottom(1);
        public int align;
        Align(int align) {
            this.align = align;
        }
    }
}
