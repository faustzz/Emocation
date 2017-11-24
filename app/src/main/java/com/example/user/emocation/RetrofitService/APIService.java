package com.example.user.emocation.RetrofitService;

import com.example.user.emocation.EmotionAPI_Info.EmotionInfo;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by MECSL on 2017-08-31.
 */

public interface APIService {


    @POST("/EmotionInfo")
    Call<EmotionInfo> createEmotionInfo();


}
