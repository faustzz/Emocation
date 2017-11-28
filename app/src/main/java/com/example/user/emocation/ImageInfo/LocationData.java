package com.example.user.emocation.ImageInfo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by user on 2017-11-28.
 */

public class LocationData {
    private String localNmae;
    private double latitude;
    private double longitude;
    private List<Double> avgEmotion; // 사진의 emotion값들의 평균치
    private Picture[] pics;

    public LocationData(){}

    public LocationData(String localNmae, double latitude, double longitude, List<Double> avgEmotion, Picture[] pics) {
        this.localNmae = localNmae;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avgEmotion = avgEmotion;
        this.pics = pics;
    }

    public String getLocalNmae() {
        return localNmae;
    }

    public void setLocalNmae(String localNmae) {
        this.localNmae = localNmae;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Double> getAvgEmotion() {
        return avgEmotion;
    }

    public void setAvgEmotion(List<Double> avgEmotion) {
        this.avgEmotion = avgEmotion;
    }

    public Picture[] getPics() {
        return pics;
    }

    public void setPics(Picture[] pics) {
        this.pics = pics;
    }
}
