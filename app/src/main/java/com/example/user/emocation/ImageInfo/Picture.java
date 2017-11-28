package com.example.user.emocation.ImageInfo;

import java.util.List;

/**
 * Created by user on 2017-11-28.
 */

public class Picture {
    private String latitute;
    private String longitude;
    private List<Double> avgEmotion;

    public Picture(String latitute, String longitude, List<Double> avgEmotion, String image_name) {
        this.latitute = latitute;
        this.longitude = longitude;
        this.avgEmotion = avgEmotion;
        this.image_name = image_name;
    }

    private String image_name;

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<Double> getAvgEmotion() {
        return avgEmotion;
    }

    public void setAvgEmotion(List<Double> avgEmotion) {
        this.avgEmotion = avgEmotion;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
