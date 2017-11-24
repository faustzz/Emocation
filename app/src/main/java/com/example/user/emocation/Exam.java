package com.example.user.emocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emocation.EmotionAPI_Info.EmotionInfo;
import com.example.user.emocation.RetrofitService.APIService;
import com.example.user.emocation.RetrofitService.ApiUtils;
import com.example.user.emocation.RetrofitService.RetrofitClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import retrofit2.Call;

/**
 * Created by user on 2017-11-24.
 */

public class Exam extends AppCompatActivity {
    private APIService apiService;
    private TextView textView;
    EmotionInfo emotionInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotionapi_exam);

        textView = (TextView)findViewById(R.id.textView);
        Uri uri = Uri.parse("https://thenypost.files.wordpress.com/2014/02/trump.jpg");
        APIService apiService = APIService.retrofit.create(APIService.class);
        Call<EmotionInfo> call = apiService.createEmotionInfo();

        textView.setText(call.toString());



        findViewById(R.id.button_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1000);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                //Uri에서 이미지 이름을 얻어온다.
                //String name_Str = getImageNameToUri(data.getData());

                //이미지 데이터를 비트맵으로 받아온다.
                Bitmap image_bitmap = null;

                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView image = (ImageView)findViewById(R.id.imageView);

                //배치해놓은 ImageView에 set
                image.setImageBitmap(image_bitmap);
            }
        }
    }
}
