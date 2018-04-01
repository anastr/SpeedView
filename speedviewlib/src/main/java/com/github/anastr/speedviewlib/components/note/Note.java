package com.github.anastr.speedviewlib.components.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * <p>
 * small popup dialog with simple note shown with limited or unlimited time.
 * </p>
 *
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unchecked,unused,WeakerAccess")
public abstract class Note<N extends Note> {

    /** This value used with the {@code speedometer.addNote(Note, int)} property
     *  to keep the note on the speedometer.
     *  <p>but it well be removed when call {@code speedometer.removeAllNotes()}.</p>*/
    public static final int INFINITE = -1;

    private float density;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG)
            , backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float paddingLeft, paddingTop
            , paddingRight, paddingBottom;
    private Bitmap backgroundBitmap;
    private Position position = Position.CenterIndicator;
    private Align align = Align.Top;
    private int noteW = 0, noteH = 0, containsW = 0, containsH = 0;
    private float cornersRound = 5f;
    /** dialog's triangle Height */
    private float triangleHeight;

    protected Note (Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        init();
    }

    private void init() {
        triangleHeight = dpTOpx(12f);
        backgroundPaint.setColor(0xffd6d7d7);
        setPadding(dpTOpx(7f), dpTOpx(7f), dpTOpx(7f), dpTOpx(7f));
    }

    public float dpTOpx(float dp) {
        return dp * density;
    }

    /**
     *  draw inside note's dialog.
     * @param canvas canvas to draw.
     * @param leftX left x position to start drawing.
     * @param topY top y position to start drawing.
     */
    protected abstract void drawContains(Canvas canvas, float leftX, float topY);

    /**
     * called by speedometer after create the Note.<br>
     * it must call {@link #noticeContainsSizeChange(int, int)} at the End.
     * @param viewWidth Speedometer View Width.
     */
    public abstract void build(int viewWidth);

    /**
     * this must call when contains size change or padding or {@link #triangleHeight}.
     * @param containsW contains width.
     * @param containsH contains height.
     */
    protected void noticeContainsSizeChange(int containsW, int containsH) {
        this.containsW = containsW;
        this.containsH = containsH;
        if (align == Align.Top || align == Align.Bottom) {
            this.noteW = (int) (containsW + paddingLeft + paddingRight);
            this.noteH = (int) (containsH + paddingTop + paddingBottom + triangleHeight);
        }
        else {
            this.noteW = (int) (containsW + paddingLeft + paddingRight + triangleHeight);
            this.noteH = (int) (containsH + paddingTop + paddingBottom);
        }
        updateBackgroundBitmap();
    }

    /**
     * notice that background dialog changed (color, size, Corners Round ....).
     */
    private void updateBackgroundBitmap() {
        backgroundBitmap = Bitmap.createBitmap(noteW, noteH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(backgroundBitmap);
        if (align == Align.Left)
            bitmapLeft(c);
        else if (align == Align.Top)
            bitmapTop(c);
        else if (align == Align.Right)
            bitmapRight(c);
        else if (align == Align.Bottom)
            bitmapBottom(c);
    }

    private void bitmapLeft (Canvas c) {
        RectF rectF = new RectF(0f, 0f, noteW - triangleHeight, noteH);
        Path p = new Path();
        p.moveTo(noteW, noteH/2f);
        p.lineTo(rectF.right - 1, noteH/2f - dpTOpx(9f));
        p.lineTo(rectF.right - 1, noteH/2f + dpTOpx(9f));
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    private void bitmapTop (Canvas c) {
        RectF rectF = new RectF(0f, 0f, noteW, noteH - triangleHeight);
        Path p = new Path();
        p.moveTo(noteW /2f, noteH);
        p.lineTo(noteW /2f - dpTOpx(9f), rectF.bottom - 1);
        p.lineTo(noteW /2f + dpTOpx(9f), rectF.bottom - 1);
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    private void bitmapRight (Canvas c) {
        RectF rectF = new RectF(0f + triangleHeight, 0f, noteW, noteH );
        Path p = new Path();
        p.moveTo(0f, noteH/2f);
        p.lineTo(rectF.left + 1, noteH/2f - dpTOpx(9f));
        p.lineTo(rectF.left + 1, noteH/2f + dpTOpx(9f));
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    private void bitmapBottom (Canvas c) {
        RectF rectF = new RectF(0f, 0f + triangleHeight, noteW, noteH);
        Path p = new Path();
        p.moveTo(noteW /2f, 0f);
        p.lineTo(noteW /2f - dpTOpx(9f), rectF.top + 1);
        p.lineTo(noteW /2f + dpTOpx(9f), rectF.top + 1);
        c.drawPath(p, backgroundPaint);
        c.drawRoundRect(rectF, cornersRound, cornersRound, backgroundPaint);
    }

    public void draw(Canvas canvas, float posX, float posY) {
        switch (align) {
            case Left:
                canvas.drawBitmap(backgroundBitmap, posX - noteW, posY - (noteH / 2f), paint);
                drawContains(canvas, posX - noteW + paddingLeft, posY - (noteH / 2f) + paddingTop);
                break;
            case Top:
                canvas.drawBitmap(backgroundBitmap, posX - (noteW / 2f), posY - noteH, paint);
                drawContains(canvas, posX - (containsW / 2f), posY - noteH + paddingTop);
                break;
            case Right:
                canvas.drawBitmap(backgroundBitmap, posX, posY - (noteH / 2f), paint);
                drawContains(canvas, posX + triangleHeight + paddingLeft, posY - (noteH / 2f) + paddingTop);
                break;
            case Bottom:
                canvas.drawBitmap(backgroundBitmap, posX - (noteW / 2f), posY, paint);
                drawContains(canvas, posX - (containsW / 2f), posY + triangleHeight + paddingTop);
                break;
        }

    }

    public int getBackgroundColor() {
        return backgroundPaint.getColor();
    }

    /**
     * set dialog color.
     * @param backgroundColor new color.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    public N setBackgroundColor(int backgroundColor) {
        backgroundPaint.setColor(backgroundColor);
        return (N) this;
    }

    public float getCornersRound() {
        return cornersRound;
    }

    /**
     * change Corners Round for Dialog Rect.
     * @param cornersRound new Corners Round.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    public N setCornersRound(float cornersRound) {
        if (cornersRound < 0)
            throw new IllegalArgumentException("cornersRound cannot be negative");
        this.cornersRound = cornersRound;
        return (N) this;
    }

    public Align getAlign () {
        return align;
    }

    /**
     * change dialog Align (default : <b>Top</b>)
     * @param align Enm value new dialog Align.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    public N setAlign (Align align) {
        this.align = align;
        return (N) this;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * set dialog position.
     * @param position Enm value new Position.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    public N setPosition(Position position) {
        this.position = position;
        return (N) this;
    }

    /**
     * set padding inside dialog.
     * @param left Left Padding.
     * @param top Top Padding.
     * @param right Right Padding.
     * @param bottom Bottom Padding.
     * @return This Note object to allow for chaining of calls to set methods.
     */
    public N setPadding(float left, float top, float right, float bottom){
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
        noticeContainsSizeChange(containsW, containsH);
        return (N) this;
    }

    public enum Position {
        /**
         * the top of the indicator at current speed.
         */
        TopIndicator,
        /**
         * center between [top fo indicator, bottom of indicator] at current speed.
         */
        CenterIndicator,
        /**
         * the bottom of the indicator at current speed.
         */
        BottomIndicator,
        /**
         * the top of the speedometer at current speed.
         */
        TopSpeedometer,
        /**
         * center of the speedometer (current speed ignored).
         */
        CenterSpeedometer,
        /**
         * quarter of the speedometer between [top fo speedometer, center of speedometer] at current speed.
         */
        QuarterSpeedometer
    }

    public enum Align {
        Left, Top, Right, Bottom
    }
}
