package com.example.user.emocation.RetrofitService;

import com.example.user.emocation.EmotionAPI_Info.EmotionInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by MECSL on 2017-08-31.
 */

public interface APIService {


    @GET("/EmotionInfo")
    Call<EmotionInfo> createEmotionInfo(@Body EmotionInfo naverInfo);



}
