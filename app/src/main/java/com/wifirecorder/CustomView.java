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
    public Paint mPaint;
    public static Canvas mCanvas;
    private int mPivotX = 0;
    private int mPivotY = 0;
    private int radius = 60;

    public CustomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint();
    }

    public void drawCircle( int f_x, int f_y) {
//
//        //Generate random numbers for x and y locations of the circle on screen
//        Random random = new Random();
//        //mPivotX = random.nextInt(maxX - minX + 1) + minX;
//        //mPivotY = random.nextInt(maxY - minY + 1) + minY;
        mPivotX = f_x;
        mPivotY = f_y;
        //important. Refreshes the view by calling onDraw function
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //super.onDraw(canvas);
        canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 50, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);


        mCanvas = canvas;
        super.onDraw(mCanvas);
        canvas.drawColor(Color.GRAY);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mPivotX, mPivotY, radius, mPaint);
    }
}
