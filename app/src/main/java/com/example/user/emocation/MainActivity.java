package com.example.user.emocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import com.example.user.emocation.ImageInfo.Picture;
import com.example.user.emocation.Map.GoogleMapActivity;

import java.io.File;



public class MainActivity extends AppCompatActivity {
    private ViewGroup galleryButton, cameraButton, searchButton;
    private Activity mainActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        galleryButton = (ViewGroup) findViewById(R.id.button_gallery);
        cameraButton = (ViewGroup)findViewById(R.id.button_camera);
        searchButton = (ViewGroup)findViewById(R.id.button_search);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED ){ // 갤러리에 폴더 생성을 위한 Permission 접근
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);}
        if(fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED ){ // 갤러리에 폴더 생성을 위한 Permission 접근
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);}

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 클릭 시 갤러리에 emocation 폴더생성 , FromGalleryActivity 액티비티로 이동

                int writePermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(writePermissionCheck == PackageManager.PERMISSION_DENIED ){ // 갤러리에 폴더 생성을 위한 Permission 접근
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    File emocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation"); //emocation 폴더 생성
                    if(!emocation.exists()) {
                        emocation.mkdir();
                    }
                    Intent intent = new Intent(getApplicationContext(), FromGalleryActivity.class);
                    startActivity(intent);
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cameraPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);     // 카메라 사용 Permission 체크를 위한 변수
                if(cameraPermissionCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.CAMERA},2);      // 사용 권한이 없을 경우 사용 권한 Request.
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int readPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(readPermissionCheck == PackageManager.PERMISSION_DENIED){ // 갤러리에 폴더 생성을 위한 Permission 접근
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}