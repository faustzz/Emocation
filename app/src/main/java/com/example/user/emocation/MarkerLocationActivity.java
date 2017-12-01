package com.example.user.emocation;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Joo Hyun Jun on 2017-12-02.
 */

public class MarkerLocationActivity extends Activity {
    private double latitude, longitude;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("emocation/");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_location);

        Bundle bundle = getIntent().getExtras();
        //Picture picture = bundle.getParcelable("title2");
        Picture picture = GoogleMap.picture;
        LocationData locationData = GoogleMap.locationData;

        latitude = convert(picture.getLatitute());
        longitude = convert(picture.getLongitude());

        for(int i = 0 ; i < locationData.getPicture().size() ; i++) {
            double isINlatitude = convert(locationData.getPicture().get(i).getLatitute());
            double isINlongitude = convert(locationData.getPicture().get(i).getLongitude());
            if (isIN(latitude, longitude, isINlatitude, isINlongitude)){
                storageRef.child(locationData.getPicture().get(i).getImage_name()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("사진 : ", uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

        }


    }
//15m 10m


    public boolean isIN(double latitude, double longitude, double isINlatitude, double isINlongitude){
        if(latitude-0.002 < isINlatitude && latitude+0.002> isINlatitude && longitude-0.0025< isINlongitude && longitude+0.0025>isINlongitude){
            return true;
        }
        else
            return false;
    }


    public Double convert(String LongLat){ // 도 분 초로 표현 된 위도경도값을 십진법으로 표현
        double dd=0,mm=0,ss=0;
        String[] str = LongLat.split("/1");

        for(int i=0;i<3;i++){
            if (str[i].startsWith(",")) {
                str[i]=str[i].replace(",","");
            }
        }

        dd =  Double.parseDouble(str[0]);
        mm= Double.parseDouble(str[1]);
        ss=Double.parseDouble(str[2]);
        double dec=dd+(mm/60)+(ss/3600);

        String decimal =Double.toString(dec);
        return dec;
    }
}
