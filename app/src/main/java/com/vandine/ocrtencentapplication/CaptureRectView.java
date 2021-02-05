package com.vandine.ocrtencentapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CaptureRectView extends View {
    private int x;
    private int y;
    private int m;
    private int n;
    private boolean sign;//绘画标记位
    private Paint paint;//画笔

    public CaptureRectView(Context context) {
        this(context,null);
    }

    public CaptureRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CaptureRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint(){
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (sign){
            paint.setColor(Color.TRANSPARENT);
        }else {
            paint.setColor(Color.BLUE);
            paint.setAlpha(80);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15f);
            canvas.drawRect(new Rect(x,y,m,n),paint);
        }
        super.onDraw(canvas);
    }

    public void setSeat(int x, int y, int m, int n){
        this.x = x;
        this.y = y;
        this.m = m;
        this.n = n;
    }

    public boolean isSign(){
        return  sign;
    }

    public void setSign(boolean sign){
        this.sign = sign;
    }
}
