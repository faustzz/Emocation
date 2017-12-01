package com.example.user.emocation.ImageInfo;

import com.example.user.emocation.ImageAlgorithm.Emotion;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 2017-11-28.
 */


@SuppressWarnings("serial")
public class Picture implements Serializable {

    private String latitute;
    private String longitude;
    private Emotion emotion;
    private String image_name;


    public Picture(){}

    public Picture(String latitute, String longitude, Emotion emotion, String image_name) {
        this.latitute = latitute;
        this.longitude = longitude;
        this.emotion = emotion;
        this.image_name = image_name;
    }


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

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
