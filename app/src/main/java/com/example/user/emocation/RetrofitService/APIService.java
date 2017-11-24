package com.example.user.emocation.RetrofitService;

import com.example.user.emocation.EmotionAPI_Info.EmotionInfo;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by MECSL on 2017-08-31.
 */

public interface APIService {

    @Headers({"Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: 9070f1ed33494504aaf4a6918ed21a64"})
    @POST("recognize")
    Call<EmotionInfo> createEmotionInfo();

    public static final Retrofit   retrofit = new Retrofit.Builder()
            .baseUrl("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
