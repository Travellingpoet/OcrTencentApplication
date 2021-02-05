package com.vandine.ocrtencentapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class XfermodeEraserView extends View {

    private Paint mPaint,srcPaint;
    private Bitmap mDstBmp, mSrcBmp, mBgBitmap;
    private Path mPath;
    private int width; //屏幕宽度（也是蒙版宽度）
    private int height;//屏幕高度

    public XfermodeEraserView(Context context) {
        this(context, null);
    }

    public XfermodeEraserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XfermodeEraserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(80);

//        srcPaint = new Paint();
//        srcPaint.setColor(getResources().getColor(R.color.mantle));
//        srcPaint.setStyle(Paint.Style.FILL);
        //禁用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //初始化图片对象
//        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_logo);
//        mSrcBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        mDstBmp = Bitmap.createBitmap(mSrcBmp.getWidth(), mSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);

        //路径（贝塞尔曲线）
        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSrcBmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_mantle);
        mDstBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制刮奖结果
        canvas.drawBitmap(mBgBitmap, null, new RectF(0f,0f,width,height), null);

        //使用离屏绘制
        int layerID = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);

        //先将路径绘制到 bitmap上
        Canvas dstCanvas = new Canvas(mDstBmp);
        dstCanvas.drawPath(mPath, mPaint);

        //绘制 目标图像
        canvas.drawBitmap(mDstBmp, 0, 0, mPaint);
        //设置 模式 为 SRC_OUT, 擦橡皮区域为交集区域需要清掉像素
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //绘制源图像
        canvas.drawBitmap(mSrcBmp,0,0,mPaint);
//        canvas.drawRect(new RectF(0f,0f,width,height),srcPaint);

        mPaint.setXfermode(null);

        canvas.restoreToCount(layerID);
    }

    private float mEventX, mEventY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mEventX = event.getX();
                mEventY = event.getY();
                mPath.moveTo(mEventX, mEventY);
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = (event.getX() - mEventX) / 2 + mEventX;
                float endY = (event.getY() - mEventY) / 2 + mEventY;
                //画二阶贝塞尔曲线
                mPath.quadTo(mEventX, mEventY, endX, endY);
                mEventX = event.getX();
                mEventY = event.getY();
                break;
        }
        invalidate();
        return true; //消费事件
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
        int width = getWidth();
        int height = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        canvas.saveLayer(0,0,width,height,mPaint,Canvas.ALL_SAVE_FLAG);
        draw(canvas);
        canvas.save();
        return bitmap;
    }

}