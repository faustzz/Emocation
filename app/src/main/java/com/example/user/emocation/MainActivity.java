package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageButton galleryButton, cameraButton, searchButton;
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
                //////갤러리에 새 폴더 생성////////
                String emo_album_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation";
                File emocation = new File(emo_album_path);
                if(!emocation.exists()) {
                    emocation.mkdir();
                }
                //////////////////////////////////////
                Intent intent = new Intent(getApplicationContext(), Exam.class);
                startActivity(intent);
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
