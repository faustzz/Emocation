package com.example.user.emocation.RetrofitService;

/**
 * Created by MECSL on 2017-08-31.
 */

public class ApiUtils {
    private ApiUtils(){}

    public static final String NAVER_PROFILE = "https://openapi.naver.com/v1/nid/me/";
    public static APIService getNaverService(){
        return RetrofitClient.getClient(NAVER_PROFILE).create(APIService.class);
    }
}
