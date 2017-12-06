package com.wifirecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;
import java.util.jar.Attributes;

/**
 * Created by yuehan on 06/12/2017.
 */

public class CustomView extends View
{
    private int radius = 60;
    private int mPivotX = 0;
    private int mPivotY = 0;
    Paint paint = null;
    public static Canvas mCanvas;

    public CustomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint = new Paint();
    }

    public void drawCircle( int f_x, int f_y) {

        int minX = radius * 2;
        int maxX = getWidth() - (radius *2 );

        int minY = radius * 2;
        int maxY = getHeight() - (radius *2 );

        //Generate random numbers for x and y locations of the circle on screen
        Random random = new Random();
        //mPivotX = random.nextInt(maxX - minX + 1) + minX;
        //mPivotY = random.nextInt(maxY - minY + 1) + minY;
        mPivotX = f_x;
        mPivotY = f_y;
        //important. Refreshes the view by calling onDraw function
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //super.onDraw(canvas);
        canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 50,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
//        int x = getWidth();
//        int y = getHeight();
//        int radius;
//        radius = 20;
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        canvas.drawPaint(paint);
//        // Use Color.parseColor to define HTML colors
//        paint.setColor(Color.parseColor("#A93434"));
//        canvas.drawCircle(x / 2, y / 2, radius, paint);


        mCanvas = canvas;
        super.onDraw(mCanvas);
        canvas.drawColor(Color.GRAY);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(mPivotX, mPivotY, radius, paint);
    }
}
