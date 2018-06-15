package com.abc.recorderdemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.abcpen.livemeeting.sdk.wbrecord.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * zhaocheng
 */
public class SandboxView extends View implements OnTouchListener {

    private Bitmap bitmap;
    private int width;
    private int height;
    private Matrix transform = new Matrix();

    private Vector2D position = new Vector2D();
    private float scale = 1;
    private float angle = 0;

    private TouchManager touchManager = new TouchManager(2);
    private boolean isInitialized = false;
    private Paint paint = new Paint();
    private boolean isVerticalInversion = false;
    private boolean isReverseImg = false;

    // Debug helpers to draw lines between the two touch points
    private Vector2D vca = null;
    private Vector2D vcb = null;
    private Vector2D vpa = null;
    private Vector2D vpb = null;

    public void reset() {
        isInitialized = false;
        scale = 1;
        angle = 0;
        isReverseImg = false;
        isVerticalInversion = false;
    }

    public boolean isVerticalInversion() {
        return isVerticalInversion;
    }

    public boolean isReverseImg() {
        return isReverseImg;
    }

    public SandboxView(Context context) {
        super(context);
        setBackgroundResource(R.color.abc_transparent);
        setOnTouchListener(this);

    }

    public void setBitmap(Bitmap bitmap) {
        recycleBitmap();
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }


    private static float getDegreesFromRadians(float angle) {
        return (float) (angle * 180.0 / Math.PI);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInitialized) {
            int w = getWidth();
            int h = getHeight();
            position.set(w / 2, h / 2);
            isInitialized = true;
        }

        transform.reset();

        if (isReverseImg) {
            transform.postScale(-1, 1);
            transform.postTranslate(bitmap.getWidth(), 0);
        } else {
            transform.postScale(1, 1);
        }

        if (isVerticalInversion) {
            transform.postScale(1, -1);
            transform.postTranslate(0, bitmap.getHeight());
        } else {
            transform.postScale(1, 1);
        }


        transform.postTranslate(-width / 2.0f, -height / 2.0f);
        transform.postRotate(getDegreesFromRadians(angle));
        transform.postScale(scale, scale);
        transform.postTranslate(position.getX(), position.getY());


        canvas.drawBitmap(bitmap, transform, paint);

    }

    public float getScale() {
        return scale;
    }

    public Vector2D getVector2D() {
        return position;
    }

    public float getRotate() {
        return getDegreesFromRadians(angle);
    }

    public Point getBitmapSize() {
        Point point = new Point();
        point.x = bitmap.getWidth();
        point.y = bitmap.getHeight();
        return point;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        vca = null;
        vcb = null;
        vpa = null;
        vpb = null;
        try {
            touchManager.update(event);

            if (touchManager.getPressCount() == 1) {
                vca = touchManager.getPoint(0);
                vpa = touchManager.getPreviousPoint(0);
                position.add(touchManager.moveDelta(0));
            } else {
                if (touchManager.getPressCount() == 2) {
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    vcb = touchManager.getPoint(1);
                    vpb = touchManager.getPreviousPoint(1);
                    position.add(touchManager.moveDelta(0));

                    Vector2D current = touchManager.getVector(0, 1);
                    Vector2D previous = touchManager.getPreviousVector(0, 1);
                    float currentDistance = current.getLength();
                    float previousDistance = previous.getLength();

                    if (previousDistance != 0) {
                        scale *= currentDistance / previousDistance;
                    }

                    angle -= Vector2D.getSignedAngleBetween(current, previous);
                }
            }

            invalidate();
        } catch (Throwable t) {
            // So lazy...
            t.printStackTrace();
        }
        return true;
    }

    public void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
    }


    /**
     * 垂直反转
     */
    public void verticalInversion() {
        isVerticalInversion = !isVerticalInversion;
        invalidate();
    }

    /**
     * 水平反转
     */
    public void reverseImg() {
        isReverseImg = !isReverseImg;
        invalidate();
    }


    /**
     * 平铺
     */
    public void tileImg() {
        if (bitmap.getWidth() > bitmap.getHeight()) {
            scale = getWidth() / (float) bitmap.getWidth();
        } else {
            scale = getHeight() / (float) bitmap.getHeight();
        }
        angle = 0;
        int w = getWidth();
        int h = getHeight();
        position.set(w / 2, h / 2);
        invalidate();
    }


    /***
     * 将bitmap保存在SD jpeg格式
     * @param bitmap
     *
     *            图片bitmap
     * @param filePath
     *            要保存图片路径
     * @param quality
     *            压缩质量值
     */
    public void saveImage(Bitmap bitmap, File filePath, int quality) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fileOutputStream);
            // 如果图片还没有回收，强制回收
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {

        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
