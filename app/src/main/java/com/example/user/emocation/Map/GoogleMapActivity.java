package com.example.user.emocation.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.example.user.emocation.R;
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

/**
 * Created by user on 2017-11-28.
 */

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback{
    static LocationData locationData = new LocationData(); // dialog 버튼 -> activity로 이동할 때, Serialble된 object들이 중복으로 안넘어가는 이슈 때문에 static으로 설정
    static Picture picture;
    private DatabaseReference mDatabase; //DB 받아오기
    private MarkerDialog markerDialog;
    private Marker clickedmarker1;

    //로딩창
    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mHandler = new Handler(); // 로딩창 핸들러 생성

        loading();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final com.google.android.gms.maps.GoogleMap googleMap) {
        LatLng seoul = new LatLng(37.556915, 127.006028); // 서울 중심으로 초반 지도 view 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot img_location : dataSnapshot.getChildren()){
                    Picture picture = (Picture) img_location.getValue(Picture.class); // 데이터 search, Picture 형태로 가져옴
                    locationData.setPicture(picture);
                        LatLng position = new LatLng(convert(picture.getLatitute()), convert(picture.getLongitude())); // 위치가 같으면 마커가 중복되면서 이 전 마커가 사라짐
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        googleMap.addMarker(markerOptions).setTag(picture); // 마커 추가, 정보 숨김

                }
                mProgressDialog.dismiss();
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
                clickedmarker1 = marker;
                markerDialog = new MarkerDialog(GoogleMapActivity.this, ImageClickListener, LocationClickListener);
                markerDialog.show();

                return false;
            }
        });
    }
    private View.OnClickListener ImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { // dialog 현 위치 사진 보기
            Toast.makeText(getApplicationContext(), "Image Click!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MarkerImageActivity.class);

            picture = (Picture)clickedmarker1.getTag(); // 마커의 정보를 받아온다.
            Toast.makeText(getApplicationContext(),picture.getImage_name(),Toast.LENGTH_SHORT).show();
            intent.putExtra("title",picture); // 마커의 정보를 전달

            startActivity(intent);
        }
    };

    private View.OnClickListener LocationClickListener = new View.OnClickListener() { // dialog 주변 위치 사진 보기
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Location Click!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MarkerLocationActivity.class);


            Toast.makeText(getApplicationContext(),picture.getImage_name(),Toast.LENGTH_SHORT).show();
            //intent.putExtra("title2 ",picture);

            startActivity(intent);
        }
    };

    public void loading(){
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgressDialog = ProgressDialog.show(GoogleMapActivity.this,"",
                        "지도를 불러오는 중입니다.",true);
                mHandler.postDelayed( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            if (mProgressDialog!=null&&mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                }, 100000);
            }
        } );
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
