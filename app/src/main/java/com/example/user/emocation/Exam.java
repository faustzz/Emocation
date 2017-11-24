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

import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2017-11-24.
 */

public class Exam extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json");
    OkHttpClient client = new OkHttpClient();
    private APIService apiService;
    private TextView textView;
    EmotionInfo emotionInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotionapi_exam);
        Uri uri = Uri.parse("https://thenypost.files.wordpress.com/2014/02/trump.jpg");

        try {
            String response = post();
            textView.setText(response);
        } catch (IOException e) {
            e.printStackTrace();
        }


        findViewById(R.id.button_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1000);
            }
        });

        textView = (TextView)findViewById(R.id.textView);

    }

    public String post() throws IOException {
        Request request = new Request.Builder()
                .url("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize/")
                .header("Content-Type", "application/json")
                .header("Ocp-Apim-Subscription-Key","9070f1ed33494504aaf4a6918ed21a64")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body().string();
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
