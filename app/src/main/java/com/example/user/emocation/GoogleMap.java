package com.example.user.emocation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.example.user.emocation.ImageAlgorithm.Emotion;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-11-28.
 */

public class GoogleMap extends FragmentActivity implements OnMapReadyCallback{

    private DatabaseReference mDatabase; //DB 받아오기
    Picture pictures;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        FragmentManager fragmentManager = getFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final com.google.android.gms.maps.GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int i = 0;
                for(DataSnapshot img_location : dataSnapshot.getChildren()){
                    Emotion emotion = new Emotion();
                    emotion.anger = Double.parseDouble(img_location.child("0").getValue().toString());
                    emotion.fear = Double.parseDouble(img_location.child("1").getValue().toString());
                    emotion.happiness = Double.parseDouble(img_location.child("2").getValue().toString());
                    emotion.neutral = Double.parseDouble(img_location.child("3").getValue().toString());
                    emotion.sadness = Double.parseDouble(img_location.child("4").getValue().toString());
                    emotion.surprise = Double.parseDouble(img_location.child("5").getValue().toString());

                    pictures = new Picture(String.valueOf(img_location.child("latitute").getValue()),String.valueOf(img_location.child("longitude").getValue()),emotion,String.valueOf(img_location.child("image_name").getValue()));

                    LatLng position = new LatLng(convert(pictures.getLatitute()),convert(pictures.getLongitude()));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(pictures.getImage_name());
                    markerOptions.position(position);

                    googleMap.addMarker(markerOptions);
                    i++;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static Double convert(String LongLat){
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
