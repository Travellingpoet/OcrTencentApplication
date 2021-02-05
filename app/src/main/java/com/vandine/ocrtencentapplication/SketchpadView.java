package com.vandine.ocrtencentapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import rx.internal.util.unsafe.MpmcArrayQueue;


public class SketchpadView extends View {
    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private Bitmap mBufferBitmap,mBgBitmap;
    private Canvas mBufferCanvas;
    private Rect mRect;
    private int width; //屏幕宽度（也是蒙版宽度）
    private int height;//屏幕高度


    public SketchpadView(Context context) {
        this(context,null);
    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        //防锯齿，防抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(80);
        mPaint.setAlpha(0x80);
        mPaint.setColor(getResources().getColor(R.color.transparent));
//        mPaint.setColor(Color.RED);

        //禁用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPath = new Path();

//        mBufferBitmap = Bitmap.createBitmap(mBgBitmap.getWidth(),mBgBitmap.getHeight(),Bitmap.Config.ARGB_4444);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBufferBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_4444);
//        //双缓存机制
//        mBufferBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_4444);
//        mBufferCanvas = new Canvas(mBufferBitmap);
//        mBufferCanvas.drawColor(getResources().getColor(R.color.mantle));
//        mBufferCanvas.drawBitmap(mBgBitmap,null, new RectF(0f,0f,w,h),null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制图片
        canvas.drawBitmap(mBgBitmap, null, new RectF(0f,0f,width,height), null);

        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
//
        //先将路径绘制到 bitmap上
        Canvas dstCanvas = new Canvas(mBufferBitmap);
        dstCanvas.drawColor(getResources().getColor(R.color.mantle));
        dstCanvas.drawPath(mPath,mPaint);
//
        //绘制 目标图像
        canvas.drawBitmap(mBufferBitmap,null,new RectF(0f,0f,width,height),mPaint);
        //设置 模式 为 SRC_OUT, 擦橡皮区域为交集区域需要清掉像素
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //绘制源图像
        canvas.drawBitmap(mBgBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
//        mBufferCanvas = new Canvas(mBufferBitmap);
//        mBufferCanvas.drawColor(getResources().getColor(R.color.mantle));
//        mBufferCanvas.drawPath(mPath,mPaint);

//        canvas.drawBitmap(mBufferBitmap,0,0,mPaint);

//        if (mBgBitmap != null){
//            canvas.drawBitmap(mBgBitmap,null, new RectF(0f,0f,width,height),null);
////            canvas.drawBitmap(mBgBitmap,0, 0,mPaint);
//        }
//
//
////        mPaint.setXfermode(null);
////        canvas.restoreToCount(layerId);
//        if (mBufferBitmap != null){
//            canvas.drawBitmap(mBufferBitmap,0,0,null);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.quadTo(mLastX,mLastY,(x + mLastX) / 2,(y + mLastY) / 2);
                mLastX = x;
                mLastY = y;
//                mBufferCanvas.drawPath(mPath,mPaint);
//                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.reset();
                invalidate();
                break;
        }
//        postInvalidate();
//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getMode(widthMeasureSpec)  == MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getMode(heightMeasureSpec)  == MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(heightMeasureSpec);
    }

    public Bitmap setBackgroundImg(Bitmap bitmap){
        return mBgBitmap = bitmap;
    }

    public Bitmap getBitmapResult(){
        return mBufferBitmap;
    }

}
