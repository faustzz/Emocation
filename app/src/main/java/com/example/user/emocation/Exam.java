package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelcan.emotionanalysisapi.EmotionRestClient;
import com.pixelcan.emotionanalysisapi.ResponseCallback;
import com.pixelcan.emotionanalysisapi.models.FaceAnalysis;
import com.pixelcan.emotionanalysisapi.models.FaceRectangle;
import com.pixelcan.emotionanalysisapi.models.Scores;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by user on 2017-11-24.
 */

public class Exam extends AppCompatActivity {
    private String gps_latitude = null, gps_longtitude = null;
    private TextView textView;
    private ImageButton btn_gallery;
    private FaceRectangle faceRectangle;
    private Scores scores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotionapi_exam);

        btn_gallery = (ImageButton)findViewById(R.id.button_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromGallery();
            }
        });
        textView = (TextView)findViewById(R.id.textView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                //Uri에서 이미지 이름을 얻어온다.
                String name_Str = getImageNameToUri(data.getData());
                //이미지 데이터를 비트맵으로 받아온다.
                Bitmap image_bitmap = null;

//                try {
//                    ExifInterface exif = new ExifInterface(data.getData().getPath() + "/" + name_Str);
//                    showExif(exif);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                exiF(data, name_Str);
                retro(image_bitmap);

                ImageView image = (ImageView)findViewById(R.id.imageView);

                //배치해놓은 ImageView에 set
                image.setImageBitmap(image_bitmap);
            }
        }
    }

    public String BitMapToString(Bitmap bitmap) { //bitamp을 string으로 변환시켜줌
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void showExif(ExifInterface exif){
        gps_latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE,exif);
        gps_longtitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE,exif);
    }
    private String getTagString(String tag, ExifInterface exif){
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private void selectFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1000);
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    public void retro(Bitmap bitmap){
        EmotionRestClient.init(getApplicationContext(),"e3b473b9304343649dfa8afdb1d12f06");
        EmotionRestClient.getInstance().detect(bitmap, new ResponseCallback() {
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                textView.setText(
                        "\n image_GPS_LONG : " + gps_longtitude +
                                "\n image_GPS_LA : " + gps_latitude) ;
            }

            @Override
            public void onSuccess(FaceAnalysis[] response) {
                FaceAnalysis[] faceAnalysis = response;
                faceRectangle = faceAnalysis[0].getFaceRectangle();
                scores = faceAnalysis[0].getScores();
                faceRectangle = faceAnalysis[0].getFaceRectangle();
                scores = faceAnalysis[0].getScores();
                textView.setText(" anger : " + scores.getAnger() +
                        " \n contempt : " + scores.getContempt() +
                        " \n disgust : " + scores.getDisgust() +
                        " \n fear : " + scores.getFear() +
                        " \n happiness : " + scores.getHappiness() +
                        " \n neutral : " + scores.getNeutral() +
                        " \n sadness : " + scores.getSadness() +
                        " \n surprise : " + scores.getSurprise() +
                        "\n image_GPS_LONG : " + gps_longtitude +
                        "\n image_GPS_LA : " + gps_latitude) ;
            }
        });

    }
    public String getPath(Uri uri) { //이미지 파일 경로 구하기
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
    public void exiF(Intent data, String name_Str){
        String str = getPath(data.getData());
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(str);
            showExif(exif);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
