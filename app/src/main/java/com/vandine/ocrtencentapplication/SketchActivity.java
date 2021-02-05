package com.vandine.ocrtencentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SketchActivity extends AppCompatActivity {

    private File croppedFile;
    private XfermodeEraserView sketchpadView;
    private Button bCapture;
    private Button bCancel;
    private Bitmap resourceBitmap;
    public static final String action = "android.intent.action.MY_BROADCAST";

    public static void launch(Context context, File croppedFile){
        Intent intent = new Intent(context,SketchActivity.class);
        intent.putExtra("croppedFile",croppedFile);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        croppedFile = (File)getIntent().getSerializableExtra("croppedFile");
        initView();
        initListener();
    }

    private void initView(){
        sketchpadView = findViewById(R.id.sketch);
        bCancel = findViewById(R.id.btn_cancel);
        bCapture = findViewById(R.id.btn_capture);
//        sketchBackground = findViewById(R.id.back_img);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(croppedFile.getPath(),options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(croppedFile.getPath(),options);
        sketchpadView.setBackgroundImg(bitmap);
//        sketchBackground.setImageBitmap(bitmap);
    }

    private void initListener(){
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               TestActivity.launch(SketchActivity.this);
            }
        });
        bCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resourceBitmap = sketchpadView.getBitmapResult();
                Intent capIntent = new Intent(action);
                if (resourceBitmap != null){
                    try {
                        FileOutputStream fOut = new FileOutputStream(croppedFile);
                        resourceBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                sendBroadcast(capIntent);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}