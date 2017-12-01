package com.example.user.emocation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.example.user.emocation.ImageAlgorithm.Emotion;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final com.google.android.gms.maps.GoogleMap googleMap) {
        LatLng seoul = new LatLng(37.556915, 127.006028);
        googleMap.addMarker(new MarkerOptions().position(seoul)
                .title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot img_location : dataSnapshot.getChildren()){
                    Picture picture = (Picture) img_location.getValue(Picture.class); // 데이터 search, Picture 형태로 가져옴

                    if(picture.getLatitute() != "null" || picture.getLongitude() != "null") {
                        LatLng position = new LatLng(convert(picture.getLatitute()), convert(picture.getLongitude())); // 위치가 같으면 마커가 중복되면서 이 전 마커가 사라짐
                        MarkerOptions markerOptions = new MarkerOptions();
//                      markerOptions.title(picture.getImage_name());
                        markerOptions.position(position);
                        googleMap.addMarker(markerOptions).setTag(picture); // 마커 추가, 정보 숨김
                    }

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

        googleMap.setOnMarkerClickListener(new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() { // 마커 클릭 시
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);

                Picture picture = (Picture)marker.getTag();
                intent.putExtra("title",picture);

                startActivity(intent);
                return false;
            }
        });
    }

    static Double convert(String LongLat){ // 도 분 초로 표현 된 위도경도값을 십진법으로 표현
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
