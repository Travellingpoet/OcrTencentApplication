package com.vandine.ocrtencentapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;
import com.tencent.ocr.sdk.common.OcrModeType;
import com.tencent.ocr.sdk.common.OcrSDKConfig;
import com.tencent.ocr.sdk.common.OcrSDKKit;
import com.tencent.ocr.sdk.common.OcrType;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.pqpo.smartcropperlib.SmartCropper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {


    private ImageView showImg;
    private Button cropScale;
    private Button cropRect;
    private Button cropRota;
    private Button gallery;
    private Button post;
    private Uri imageUri;//拍照图片uri
    private Uri cropImgUri;//裁剪图片uri
    private File tempFile;//拍照图片
    private File file_headimg;//装束图
    private int maxWidth = 400;
    private int maxHeight = 400;

    String imagePath = null;
    
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final int REQUEST_SELECT_PICTURE_FOR_FRAGMENT = 0x02;

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int GALLERY_REQUEST_CODE = 3;
    private static final int CROP_REQUEST_CODE  = 4;
    private static final int CAPTURE_RECT_REQUEST_CODE  = 5;
    private static final int CAPTURE_SCALE_REQUEST_CODE  = 100;

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";
    private static String IMAGE_PATH;
    private static final String fileName = "cropImage.jpg";
    private static final int requestMode = PERMISSIONS_REQUEST_CODE;

    private File photoFile;
    private File picFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoFile = new File(getExternalFilesDir("img"), "scan.jpg");

        Hawk.init(getApplicationContext()).build();
        SmartCropper.buildImageDetector(this);

        initView();
        initClickListener();
        initOcrSDK();
        initReceiver();
    }
    private void initReceiver(){

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bitmap bitmap = BitmapFactory.decodeFile(picFile.getPath());
                showImg.setImageBitmap(bitmap);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(DrawRectActivity.action);
        registerReceiver(broadcastReceiver,filter);
    }
    private void initOcrSDK(){
        OcrModeType modeType = OcrModeType.OCR_DETECT_AUTO_MANUAL;
        OcrType ocrType = OcrType.IDCardOCR_FRONT;
        OcrSDKConfig configBuider = OcrSDKConfig.newBuilder("AKIDZtzo56lTP5VeXMNuFJ7etr0uYv872tqX"
                ,"atuoankUu4yqX2II1SdXAcmhQoyxi9PQ"
                , null)
                .ocrType(ocrType)
                .setModeType(modeType)
                .build();
        OcrSDKKit.getInstance().initWithConfig(this.getApplicationContext(),configBuider);
    }

    private void initClickListener(){
        cropRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(requestMode);
            }
        });
        cropScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(CAPTURE_SCALE_REQUEST_CODE);
            }
        });
        cropRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(CAPTURE_RECT_REQUEST_CODE);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                takePhoto(GALLERY_REQUEST_CODE);
                startActivityForResult(CropActivity.launch(MainActivity.this, true, photoFile), CAPTURE_SCALE_REQUEST_CODE);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), photoFile);
                        MultipartBody.Part photo = MultipartBody.Part.createFormData("file", photoFile.getName(), photoRequestBody);
                        RequestUtils.postFileRequest(photo)
                                .compose(RxHelper.observableIO2Main())
                                .subscribe(new Observer<ResultBean>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {
                                    }

                                    @Override
                                    public void onNext(@NonNull ResultBean resultBean) {
                                        Log.e("==========Success",resultBean.getResult());
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("==========Error",e.toString());
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).start();
            }
        });
    }

    private void initView(){
        showImg = findViewById(R.id.show_img);
        cropRect = findViewById(R.id.crop_rect);
        cropScale = findViewById(R.id.crop_scale);
        cropRota = findViewById(R.id.crop_rota);
        gallery = findViewById(R.id.gallery);
        post = findViewById(R.id.postBtn);
    }


    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            return false;
        }else {
            return true;
        }
    }
    private void takePhoto(int type){
        if (!hasPermission()) {
            return;
        }
        if (type == CAPTURE_RECT_REQUEST_CODE || type == requestMode){
            openCamera(type);
        }else if (type == GALLERY_REQUEST_CODE) {
            openAlbum();
        }else if (type == CAPTURE_SCALE_REQUEST_CODE){
            startActivityForResult(CropActivity.launch(MainActivity.this,false,photoFile),type);
        }
    }
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE); //打开相册
    }
    private void openCamera(int requestMode){
        picFile = new File(getExternalFilesDir("img"),fileName);
        IMAGE_PATH = picFile.getPath();
        if (!picFile.exists()){
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        tempFile = new File(getExternalCacheDir(), "output_image.jpg");
//        try {
//            if (tempFile.exists()) {
//                tempFile.delete();
//            }
//            tempFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //调用系统拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, "com.vandine.ocrtencentapplication.fileprovider", picFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(picFile);
        }
        //输出到imageUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestMode);
//        cropPhoto();
    }

    /**
     * 4、裁剪
     */
    private void cropPhoto() {
        //调用系统裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("scale", true);
        //新增宽高比
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        //新增输出尺寸设定
        intent.putExtra("outputX", 150);
        intent.putExtra("outputX", 150);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("return-data", false);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"你拒绝了该权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode){
            case CAPTURE_SCALE_REQUEST_CODE:
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
                showImg.setImageBitmap(bitmap);
                break;
            case CAPTURE_RECT_REQUEST_CODE:
                DrawRectActivity.launch(this,IMAGE_PATH,fileName,picFile);
                break;
            case requestMode:
                if (imageUri != null){
                    startCrop(imageUri);
                }
                break;
            case UCrop.REQUEST_CROP:
                handleCropResult(data);
                break;
            case GALLERY_REQUEST_CODE:
                cropPhoto();
                tempFile = new File(getExternalCacheDir(),handleImageOnKitKat(data));
                break;
            case CROP_REQUEST_CODE:
                bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                showImg.setImageBitmap(bitmap);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data){
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    //将选择的图片Uri转换为路径
    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!= null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void startCrop(Uri uri){
        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),SAMPLE_CROPPED_IMAGE_NAME)));
        uCrop = baseConfig(uCrop);
        if (requestMode == REQUEST_SELECT_PICTURE_FOR_FRAGMENT){

        }else {
            uCrop.start(MainActivity.this);
        }
    }

    private UCrop baseConfig(UCrop uCrop){
        try {
            float ratioX = Float.valueOf(300);
            float ratioY = Float.valueOf(300);
            if (ratioX > 0 && ratioY > 0) {
                uCrop = uCrop.withAspectRatio(ratioX, ratioY);
            }
            int maxWidth = Integer.valueOf(500);
            int maxHeight = Integer.valueOf(500);
            if (maxWidth > UCrop.MIN_SIZE && maxHeight > UCrop.MIN_SIZE) {
                uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
            }
        } catch (NumberFormatException e) {
            Log.i("TAG", String.format("Number please: %s", e.getMessage()));
        }
        return uCrop;
    }

    private void handleCropResult(Intent result){
        Uri resultUri = UCrop.getOutput(result);
        if (result != null){
            Glide.with(this).load(cropImgUri).into(showImg);
        }
    }

    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




}