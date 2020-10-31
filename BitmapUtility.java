package com.canada.cardelar.application.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.ImageView;

import com.canada.cardelar.application.R;

public class BitmapUtility {

    public Bitmap getCircularBitmap(Bitmap srcBitmap) {

        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());

        Bitmap dstBitmap = Bitmap.createBitmap (
                squareBitmapWidth, // Width
                squareBitmapWidth, // Height
                Bitmap.Config.ARGB_8888 // Config
        );
        Canvas canvas = new Canvas(dstBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float left = (squareBitmapWidth-srcBitmap.getWidth())/2;
        float top = (squareBitmapWidth-srcBitmap.getHeight())/2;
        canvas.drawBitmap(srcBitmap, left, top, paint);

        srcBitmap.recycle();

        return dstBitmap;
    }
    // Custom method to add a border around circular bitmap
    public Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor) {
        int dstBitmapWidth = srcBitmap.getWidth()+borderWidth*2;
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth()/2 - borderWidth / 2, paint);
        srcBitmap.recycle();
        return dstBitmap;
    }

    public Bitmap setTopRounded(Bitmap workingBitmap, int w, int h) {
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Shader shader = new BitmapShader(workingBitmap, Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rec = new RectF(0, 0, w, h - 20);
        c.drawRect(new RectF(0, 20, w, h), paint);
        c.drawRoundRect(rec, 30, 30, paint);


        return Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
    }
    public Bitmap setSideRounded(Bitmap workingBitmap, int w, int h) {
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Shader shader = new BitmapShader(workingBitmap, Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rec = new RectF(0, 0, w-20, h );
        c.drawRect(new RectF(20, 0, w-20, h), paint);
        c.drawRoundRect(rec, 30, 30, paint);


        return Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
    }

}
