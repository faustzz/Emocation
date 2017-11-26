package com.example.user.emocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private ImageButton galleryButton, cameraButton, searchButton;
    private Activity mainActivity = this;
    final int PICK_FROM_ALBUM = 1000;
    private Uri mCaptureUri;
    String baseImageUrl= "http://codingexplained.com/wp-content/uploads/2015/11/Screen-Shot-2015-11-18-at-21.45.22.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galleryButton = (ImageButton)findViewById(R.id.button_gallery);
        cameraButton = (ImageButton)findViewById(R.id.button_camera);
        searchButton = (ImageButton)findViewById(R.id.button_search);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int writePermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int readPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(writePermissionCheck == PackageManager.PERMISSION_DENIED || readPermissionCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                    File emocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation");
//                    if(!emocation.exists())
//                        emocation.mkdir();
//                    Intent intent = new Intent(getApplicationContext(), Exam.class);
//                    startActivity(intent);
                }else{
                    File emocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation");
                    if(!emocation.exists()) {
                        emocation.mkdir();
                    }
                    Intent intent = new Intent(getApplicationContext(), Exam.class);
                    startActivity(intent);
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == PICK_FROM_ALBUM){
//            if(resultCode == Activity.RESULT_OK){
//                //Uri에서 이미지 이름을 얻어온다.
//                //String name_Str = getImageNameToUri(data.getData());
//
//                //이미지 데이터를 비트맵으로 받아온다.
//                Bitmap image_bitmap = null;
//                try {
//                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ImageView image = (ImageView)findViewById(R.id.imageView);
//
//                //배치해놓은 ImageView에 set
//                image.setImageBitmap(image_bitmap);
//            }
//        }
//    }
}