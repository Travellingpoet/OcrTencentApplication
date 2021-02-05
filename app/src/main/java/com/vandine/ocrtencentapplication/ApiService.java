package com.vandine.ocrtencentapplication;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
//    @POST("oauth/2.0/token")
//    Observable<AccessTokenBean> getAccessToken(@Query("grant_type") String grantType, @Query("client_id") String clientId, @Query("client_secret") String clientSecret);
//
//
//    /**
//     * 通过图片URL的形式，获取图片内的文字信息
//     * @param accessToken 通过API Key和Secret Key获取的access_token
//     * @param url 图片的url
//     * @return observable对象用于rxjava,从RecognitionResultBean中可以获得图片文字识别的信息
//     */
//    @POST("rest/2.0/ocr/v1/general_basic")
//    @FormUrlEncoded
//    Observable<OcrResultBean> getRecognitionResultByUrl(@Field("access_token") String accessToken, @Field("url") String url);
//
//    /**
//     * 通过图片，获取图片内的文字信息
//     * @param accessToken 通过API Key和Secret Key获取的access_token
//     * @param image 图像数据base64编码后进行urlencode后的String
//     * @return  observable对象用于rxjava,从RecognitionResultBean中可以获得图片文字识别的信息
//     */
//    @POST("rest/2.0/ocr/v1/general_basic")
//    @FormUrlEncoded
//    Observable<OcrResultBean> getRecognitionResultByImage(@Field("access_token") String accessToken, @Field("image") String image);
    @POST
    Observable<OcrResultBean> getRecognitionResultByImage(@Query("Action") String Action
            ,@Query("Version") String Version
            ,@Query("Region") String Region
            ,@Query("ImageBase64") String ImageBase
            ,@Query("LanguageType") String languageType);

    @Multipart
    @POST("upload")
    Observable<ResultBean> postRecognitionFile(@Part MultipartBody.Part image);
}
