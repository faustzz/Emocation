package com.example.user.emocation.RetrofitService;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MECSL on 2017-08-31.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url(baseUrl)
                .header("Content-Type", "application/json")
                .header("Ocp-Apim-Subscription-Key","9070f1ed33494504aaf4a6918ed21a64")
                .build();

        Response response = client.newCall(request).execute();
        response.body().close();
//        if(retrofit == null){
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
        return retrofit;
    }
}
