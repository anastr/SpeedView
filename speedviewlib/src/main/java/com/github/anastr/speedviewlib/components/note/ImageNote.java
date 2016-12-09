package com.github.anastr.speedviewlib.components.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class ImageNote extends Note<ImageNote> {

    private Bitmap image;
    private int width, height;
    private RectF imageRect = new RectF();
    private Paint notePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ImageNote(Context context, Bitmap image) {
        this(context, image, image.getWidth(), image.getHeight());
    }

    public ImageNote(Context context, Bitmap image, int width, int height) {
        super(context);
        if (image == null)
            throw new IllegalArgumentException("image cannot be null.");
        this.image = image;
        this.width = width;
        this.height = height;
    }

    @Override
    public void build(int viewWidth) {
        noticeContainsSizeChange(this.width, this.height);
        updateBackgroundBitmap();
    }

    @Override
    protected void drawContains(Canvas canvas, float centerX, float topY) {
        imageRect.set(centerX - (width/2f), topY, centerX + (width/2f), topY + height);
        canvas.drawBitmap(image, null, imageRect, notePaint);
    }
}
