package kwasilewski.marketplace.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;


public class PhotoTransform implements Transformation {

    private boolean border = false;

    public PhotoTransform() {

    }

    public PhotoTransform(boolean border) {
        this.border = border;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);


        float r = size / 8f;
        canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), r, r, paint);

        if (border) {
            addBorder(canvas, source.getWidth(), source.getHeight(), r);
        }

        squaredBitmap.recycle();
        return bitmap;
    }

    private void addBorder(Canvas canvas, int width, int height, float r) {
        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.GRAY);
        strokePaint.setStrokeWidth(50);
        canvas.drawRoundRect(new RectF(0, 0, width, height), r, r, strokePaint);
    }

    @Override
    public String key() {
        return "rounded_corners";
    }
}