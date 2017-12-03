package com.example.user.emocation.ImageInfo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017-11-28.
 */

public class LocationData implements Parcelable {
     List<Picture> picture = new ArrayList<Picture>(); // 사진 정보

    public LocationData(){}

    protected LocationData(Parcel in) {
    }

    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    public  List<Picture> getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture.add(picture);
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(picture);
    }
}
