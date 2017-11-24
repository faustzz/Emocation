package com.example.user.emocation.RetrofitService;

import java.io.IOException;

/**
 * Created by MECSL on 2017-08-31.
 */

public class ApiUtils {
    private ApiUtils(){}

    public static final String EMOTION_API = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize/";
    public static APIService getEmotionAPIService() throws IOException {
        return RetrofitClient.getClient(EMOTION_API).create(APIService.class);
    }
}
