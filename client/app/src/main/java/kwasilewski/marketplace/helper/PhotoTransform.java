package kwasilewski.marketplace.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;


class PhotoTransform implements com.squareup.picasso.Transformation {

    private final boolean miniature;

    public PhotoTransform(boolean miniature) {
        this.miniature = miniature;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        if (!miniature) {
            return source;
        }

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), 30, 30, paint);

        if (source != output) {
            source.recycle();
        }

        Paint paint1 = new Paint();
        paint1.setColor(Color.LTGRAY);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(10);
        canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), 35, 35, paint1);

        return output;
    }

    @Override
    public String key() {
        return "rounded_corners";
    }
}