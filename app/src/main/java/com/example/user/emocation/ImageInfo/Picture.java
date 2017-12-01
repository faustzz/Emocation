package com.example.user.emocation.ImageInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.user.emocation.ImageAlgorithm.Emotion;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 2017-11-28.
 */


public class Picture implements Parcelable { // Activity 간 object를 전달하기 위해 Parcelable을 implements함.

    private String latitute;
    private String longitude;
    private String image_name;
    private Emotion emotion; // Activity 간 object를 전달하기 위해 Parcelable을 implements함.


    public Picture(){}

    public Picture(String latitute, String longitude,String image_name ,Emotion emotion) {
        this.latitute = latitute;
        this.longitude = longitude;
        this.image_name = image_name;
        this.emotion = emotion;
    }


    protected Picture(Parcel in) {
        latitute = in.readString();
        longitude = in.readString();
        image_name = in.readString();
        emotion = in.readParcelable(Emotion.class.getClassLoader());
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(latitute);
        parcel.writeString(longitude);
        parcel.writeString(image_name);
        parcel.writeParcelable(emotion, flag);
    }
}
