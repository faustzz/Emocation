package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emocation.ImageAlgorithm.BGvalue;
import com.example.user.emocation.ImageAlgorithm.Emotion;
import com.example.user.emocation.ImageAlgorithm.ImageAlgo;
import com.example.user.emocation.ImageAlgorithm.ImageStat;
import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.pixelcan.emotionanalysisapi.EmotionRestClient;
import com.pixelcan.emotionanalysisapi.ResponseCallback;
import com.pixelcan.emotionanalysisapi.models.FaceAnalysis;
import com.pixelcan.emotionanalysisapi.models.FaceRectangle;
import com.pixelcan.emotionanalysisapi.models.Scores;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Issue : retroifit 서비스가 실행이 끝나는걸 기다려주지 않는다. retrofit 서비스가 끝나고 나서 button 강제 처리로 사진분석을 실행한다.
 */

/**
 * Created by user on 2017-11-24.
 */

public class Exam extends AppCompatActivity {

    //Layout
    private TextView txt_totalValue, txt_emotionValue, txt_backValue;
    private ImageButton btn_gallery,btn_analysis;



    //emotion
    private Scores[] scores;
    private double[] savaAvgScores= new double[8]; // 평균 emotion 값을 저장
    private Bitmap image_bitmap_analysis;
    private Emotion emotion; // 감정 분석값
    private BGvalue backValue; //배경 분석값
    private Emotion totalEmotionValue; // 배경 + 감정 분석 값

    //image
    private Uri uri;
    private String selectedImageName;
    private String gps_latitude = null, gps_longtitude = null;

    private Functions FUNCTION = new Functions();

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
        btn_analysis = (ImageButton)findViewById(R.id.start_analysis);
        btn_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backAnalysis(image_bitmap_analysis, emotion);
            }
        });

        txt_totalValue = (TextView)findViewById(R.id.txt_totalValue);
        txt_emotionValue = (TextView)findViewById(R.id.txt_emotionValue);
        txt_backValue = (TextView)findViewById(R.id.txt_backValue);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                //Uri에서 이미지 이름을 얻어온다.
                selectedImageName = getImageNameToUri(data.getData());
                //이미지 데이터를 비트맵으로 받아온다.
                Bitmap image_bitmap = null;

                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String photoPath = getPath(data.getData());         // photopath 에 사진 경로 저장
                try {
                    ExifInterface exif = new ExifInterface(photoPath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = FUNCTION.exifOrientationToDegrees(exifOrientation);     // 카메라가 현재 얼마나 회전되어있는지?
                    image_bitmap = rotate(image_bitmap, exifDegree);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageView image = (ImageView)findViewById(R.id.imageView);
                //배치해놓은 ImageView에 set

                image.setImageBitmap(image_bitmap);

                uri = data.getData();
                exiF(data, selectedImageName);
                retro(image_bitmap);

                image_bitmap_analysis = image_bitmap;
            }
        }
    }

    public Bitmap rotate(Bitmap bitmap, int degrees){
        Bitmap retBitmap = bitmap;

        if(degrees != 0 && bitmap != null){
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    retBitmap = converted;
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            catch(OutOfMemoryError ex) {
            }
        }
        return retBitmap;
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
        EmotionRestClient.init(getApplicationContext(),"9070f1ed33494504aaf4a6918ed21a64");
        EmotionRestClient.getInstance().detect(bitmap, new ResponseCallback() {
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                txt_totalValue.setText(
                        "\n image_GPS_LONG : " + gps_longtitude +
                                "\n image_GPS_LA : " + gps_latitude) ;
                emotion = new Emotion();
            }

            @Override
            public void onSuccess(FaceAnalysis[] response) {

                Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
                FaceAnalysis[] faceAnalysises = response;
                scores = new Scores[response.length];
                for(int i = 0 ; i < response.length ; i++){
                    scores[i] = faceAnalysises[i].getScores();
                }
                for (int i = 0; i < response.length; i++) {
                    savaAvgScores[0] += scores[i].getAnger();
                    savaAvgScores[1] += scores[i].getContempt();
                    savaAvgScores[2] += scores[i].getDisgust();
                    savaAvgScores[3] += scores[i].getFear();
                    savaAvgScores[4] += scores[i].getHappiness();
                    savaAvgScores[5] += scores[i].getNeutral();
                    savaAvgScores[6] += scores[i].getSadness();
                    savaAvgScores[7] += scores[i].getSurprise();
                }


                emotion = new Emotion(savaAvgScores[0]/response.length, savaAvgScores[3]/response.length, savaAvgScores[4]/response.length, savaAvgScores[5]/response.length, savaAvgScores[6]/response.length, savaAvgScores[7]/response.length); // contemt, disgust 제외하고 저장
                btn_analysis.performClick(); //retrofit sevice가 완벽히 이뤄지고나서 사진 분석을 실행한다.

//                Picture picture = new Picture(gps_latitude, gps_longtitude,emotion, name_Str);
//                LocationData locationData = new LocationData();
//                imageToDB = new ImageToDB(uri, locationData, picture, "사진");
//                imageToDB.saveToFirebase();

            }
        });
    }

    public void show(){
        txt_totalValue.setText(" anger : " + FUNCTION.excessdouble(totalEmotionValue.anger) +
                " \n fear : " + FUNCTION.excessdouble(totalEmotionValue.fear) +
                " \n happiness : " + FUNCTION.excessdouble(totalEmotionValue.happiness) +
                " \n neutral : " + FUNCTION.excessdouble(totalEmotionValue.neutral) +
                " \n sadness : " + FUNCTION.excessdouble(totalEmotionValue.sadness) +
                " \n surprise : " + FUNCTION.excessdouble(totalEmotionValue.surprise) +
                "\n image_GPS_LONG : " + gps_longtitude +
                "\n image_GPS_LA : " + gps_latitude);
        txt_emotionValue.setText(" anger : " + FUNCTION.excessdouble(emotion.anger) +
                " \n fear : " + FUNCTION.excessdouble(emotion.fear) +
                " \n happiness : " + FUNCTION.excessdouble(emotion.happiness) +
                " \n neutral : " + FUNCTION.excessdouble(emotion.neutral) +
                " \n sadness : " + FUNCTION.excessdouble(emotion.sadness) +
                " \n surprise : " + FUNCTION.excessdouble(emotion.surprise) );

        txt_backValue.setText(" Vitality : " +(backValue.getVitality()) +
                " \n Temperature : " +(backValue.getTemperature()) +
                " \n Mordernity : " + (backValue.getModernity()));
    }

    public void backAnalysis(Bitmap image_bitmap, Emotion emotionValue){ // 배경 분석

        ImageAlgo imageAlgo_to_analysis = new ImageAlgo(image_bitmap, emotion);

        emotion = imageAlgo_to_analysis.emotion;
        backValue = imageAlgo_to_analysis.backgroundValue;
        totalEmotionValue = imageAlgo_to_analysis.totalValue;


        show();

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

    private void showExif(ExifInterface exif){
        gps_latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE,exif);
        gps_longtitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE,exif);
    }
    private String getTagString(String tag, ExifInterface exif){
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

}
