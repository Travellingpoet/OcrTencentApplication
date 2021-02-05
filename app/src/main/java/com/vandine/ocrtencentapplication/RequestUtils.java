package com.vandine.ocrtencentapplication;



import java.io.File;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;

public class RequestUtils {

//    public static Observable<AccessTokenBean> postAccessToken(String grantType, String clientId, String clientSecret){
//        return RetrofitClient.getAccessToken().getAccessToken(grantType,clientId,clientSecret);
//    }
//
//    public static Observable<OcrResultBean> postOcrRequest(String accessToken, String url){
//        return RetrofitClient.getOrcResult().getRecognitionResultByImage(accessToken,url);
//    }

    public static Observable<OcrResultBean> postOcrRequest(String ImageBase,String languageType){
        return RetrofitClient.getOrcResult().getRecognitionResultByImage("GeneralBasicOCR","2018-11-19","ap-beijing",ImageBase,languageType);
    }
    public static Observable<ResultBean> postFileRequest(MultipartBody.Part imageFile){
        return RetrofitClient.getOrcResult().postRecognitionFile(imageFile) ;
    }

}
