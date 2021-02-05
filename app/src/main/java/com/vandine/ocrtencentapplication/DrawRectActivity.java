package com.vandine.ocrtencentapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowId;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class DrawRectActivity extends AppCompatActivity implements View.OnTouchListener {

    private String TAG = "DrawRectActivity";
    private String imageString;
    private String imagePath;
    private String fileName;
    private ArrayList<String> imageList = null;
    private int width, height;
    private LinearLayout layerViewLayout = null;

    private ImageView aiPreview;
    private CaptureRectView captureView;//绘画选择区域
    private int capX;//绘画开始的横坐标
    private int capY;//绘画开始的纵坐标
    private int capM;//绘画结束的横坐标
    private int capN;//绘画结束的纵坐标
    private Bitmap captureBitmap;

    private Button cancel;
    private Button aiCapture;
    private File croppedFile;
    private ImageView sketch;

    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    private static final int CAPTURE_RESPONSE  = 200;
    public static final String action = "android.intent.action.MY_BROADCAST";

    public static void launch(Context context,String imagePath,String fileName,File croppedFile){
        Intent intent = new Intent(context,DrawRectActivity.class);
        intent.putExtra("imagePath",imagePath);
        intent.putExtra("fileName",fileName);
        intent.putExtra("croppedFile",croppedFile);
       context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_rect);
        initParams();
        initView();
        initListener();
        initReceiver();
    }
    private void initReceiver(){

    }

    private void initParams(){
        width = ScreenUtils.getScreenWidth(this);
        height = ScreenUtils.getScreenHeight(this);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        imagePath = bundle.getString("imagePath");
        imageString = bundle.getString("imageString");
        fileName = bundle.getString("fileName");
        croppedFile = (File) getIntent().getSerializableExtra("croppedFile");
        //        imageList = parseImageString(imagePath,imageString);
    }
    private void initView(){
        frameLayout = findViewById(R.id.drawrect_frame);
        relativeLayout = findViewById(R.id.drawrect_rl);
        layerViewLayout = findViewById(R.id.image_zoom_view_layout);
        cancel = findViewById(R.id.btn_cancel);
        aiCapture = findViewById(R.id.btn_capture);
        aiPreview = findViewById(R.id.capture_preview);
        sketch = findViewById(R.id.img_sketch);

        ImageView originImage = new ImageView(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(croppedFile.getPath(),options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(croppedFile.getPath(),options);
        originImage.setImageBitmap(bitmap);
        originImage.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        layerViewLayout.addView(originImage,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        captureView = new CaptureRectView(this);
        originImage.setOnTouchListener(this);
        this.addContentView(captureView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    private void initListener(){
        sketch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SketchActivity.launch(DrawRectActivity.this,croppedFile);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent  = getIntent();
                createPendingResult(600,cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                setResult(RESULT_OK,cancelIntent);
                finish();
            }
        });
        aiCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent capIntent = new Intent(action);
                if (captureBitmap != null){
                    try {
                        FileOutputStream fOut = new FileOutputStream(croppedFile);
                        captureBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                sendBroadcast(capIntent);
                setResult(RESULT_OK);
                finish();
            }
        });

        if (frameLayout.isClickable()){
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private ArrayList<String> parseImageString(String imagePath,String imageString){
        ArrayList<String> list = new ArrayList<String>();
        String allFiles = imageString.substring(imageString.indexOf("img://") + "img://".length());
        String fileName = null;
        while (allFiles.indexOf(";") > 0){
            fileName = allFiles.substring(0,allFiles.indexOf(";"));
            allFiles = allFiles.substring(allFiles.indexOf(";" + 1));
            if (checkIsImageFile(fileName) && new File(imagePath + fileName).exists()){
                list.add(fileName);
            }
        }
        return list;
    }

    /**
     * 判断是否相应的图片格式
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFormat;
        if (fName.endsWith(".jpg") || fName.endsWith(".gif") || fName.endsWith(".png") || fName.endsWith(".jpeg") || fName.endsWith(".bmp")) {
            isImageFormat = true;
        } else {
            isImageFormat = false;
        }
        return isImageFormat;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                width = 0;
                height = 0;
                capX = (int) event.getX();
                capY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                capM = (int) event.getX();
                capN = (int) event.getY();
                captureView.setSeat(capX,capY,capM,capN);
                captureView.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() > capX){
                    width = (int) event.getX() - capX;
                }else {
                    width = (int) (capX - event.getX());
                    capX = (int) event.getX();
                }
                if (event.getY() > capY){
                    height = (int) event.getY() - capY;
                }else {
                    height = (int)(capY - event.getY());
                    capY = (int) event.getY();
                }
                captureBitmap = getCaptureView(this);
                if (captureBitmap!=null){
                    aiPreview.setImageBitmap(captureBitmap);
                }
                break;
        }
        return !captureView.isSign();
    }

    private Bitmap getCaptureView(Activity activity){
        View view =  activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int toHeight = frame.top;
        if (width >0 && height >0){
            bitmap = Bitmap.createBitmap(bitmap,capX,capY + 240,width,height);
            view.setDrawingCacheEnabled(false);
            return bitmap;
        }else {
            return null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent cancelIntent = getIntent();
                createPendingResult(600,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                cancelIntent.putExtra("imagePath",imagePath);
                cancelIntent.putExtra("todoWhat","cancel");
                setResult(RESULT_OK,cancelIntent);
                finish();
            default:
                break;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 5:

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}