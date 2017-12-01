package com.example.user.emocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emocation.ImageAlgorithm.Emotion;
import com.example.user.emocation.ImageAlgorithm.ImageAlgo;
import com.example.user.emocation.ImageAlgorithm.ImageStat;
import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.pixelcan.emotionanalysisapi.EmotionRestClient;
import com.pixelcan.emotionanalysisapi.ResponseCallback;
import com.pixelcan.emotionanalysisapi.models.FaceAnalysis;
import com.pixelcan.emotionanalysisapi.models.Scores;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CameraActivity extends Activity {

    // 안드로이드 내부 DB사용을 위한 선언 (간단한 값이라서 내부DB 사용)
    SharedPreferences mPref;
    //Layout
    private ImageButton capture = null;
    private ImageView iv = null;
    private TextView textView;
    private ImageButton btn_analysis;

    //emotion
    private Scores[] scores;
    private double[] savaAvgScores= new double[8];
    private Emotion emotion;

    //image
    private String gps_latitude = null, gps_longtitude = null; // gps의 위도, 경도 값을 저장
    private Uri selectedImageUri;
    private Bitmap image_bitmap_analysis; // 분석된 사진
    private String selectedImageName;
    private Uri mCurrentPhotoPath;

    private Functions FUNCTION = new Functions();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        setup(); // 버튼, 이미지 뷰 세팅
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder().detectFileUriExposure();
                StrictMode.setVmPolicy(builder.build());                            // Android 7.0 이상의 버전에서는 Uri를 통해 파일 접근을 보호하고있음(보안문제). StrictMode를 통해 그 제약 무시
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mCurrentPhotoPath = FUNCTION.createImageFile();                           // mCurrentPhotoPath = 사진이 저장될 경로
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mCurrentPhotoPath);    // putExtra를 통해 사진의 저장 경로를 지정해준다.
                startActivityForResult(intent,1);

            }
        });

        btn_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backAnalysis(image_bitmap_analysis); // 배경 분석을 하고
                LocationData locationData = new LocationData(); // 장식용
                Picture picture = new Picture(gps_latitude, gps_longtitude, emotion, FUNCTION.subString(selectedImageName,"."));
                ImageToDB imageToDB = new ImageToDB(selectedImageUri, locationData, picture, FUNCTION.subString(selectedImageName,"."));
                imageToDB.saveToFirebase();
                //setSharedPref(imageNumber);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==1){
            if(resultCode==RESULT_OK){
                String photoPath = mCurrentPhotoPath.getPath();         // photopath 에 사진 경로 저장
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath, options);  // imageview에 이미지를 띄우기 전에 decodeFile을 통해 사진의 크기를 1/options 크기만큼 줄여준다.
                selectedImageUri = FUNCTION.getImageUri(getApplicationContext(), imageBitmap);
                selectedImageName = getImageNameToUri(selectedImageUri);
                get_GPS_EXIF(photoPath);


                try{ // 카메라가 회전되어서 찍힌 경우. 카메라의 회전 각도에 따른 사진의 저장 형태를 변경시켜준다.
                    ExifInterface exif = new ExifInterface(photoPath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = FUNCTION.exifOrientationToDegrees(exifOrientation);     // 카메라가 현재 얼마나 회전되어있는지?
                    imageBitmap = FUNCTION.rotate(imageBitmap, exifDegree);
                }catch (IOException e){
                    e.getStackTrace();
                }

                retro(imageBitmap);
                image_bitmap_analysis = imageBitmap;
                FUNCTION.saveExifFile(imageBitmap, photoPath);
                iv.setImageBitmap(imageBitmap); // ImageView에 사진을 넣음

            }
        }
    }

    public void retro(Bitmap bitmap){ // EMOTION API통신
        EmotionRestClient.init(getApplicationContext(),"9070f1ed33494504aaf4a6918ed21a64");
        EmotionRestClient.getInstance().detect(bitmap, new ResponseCallback() {
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                textView.setText(
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

            }
        });
    }


    public String getTagString(String tag, ExifInterface exif){
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    public void get_GPS_EXIF(String str){ // EXIF 정보에서 GPS값을 추출
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gps_latitude = FUNCTION.subString(getTagString(ExifInterface.TAG_GPS_LATITUDE,exif),":");
        gps_longtitude = FUNCTION.subString(getTagString(ExifInterface.TAG_GPS_LONGITUDE,exif),":");
    }


    public String getImageNameToUri(Uri data) // Uri 값으로 이미지의 이름을 추출한다.
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }






    public void show(){
        textView.setText(" anger : " + FUNCTION.excessdouble(emotion.anger) +
                " \n fear : " + FUNCTION.excessdouble(emotion.fear) +
                " \n happiness : " + FUNCTION.excessdouble(emotion.happiness) +
                " \n neutral : " + FUNCTION.excessdouble(emotion.neutral) +
                " \n sadness : " + FUNCTION.excessdouble(emotion.sadness) +
                " \n surprise : " + FUNCTION.excessdouble(emotion.surprise) +
                "\n image_GPS_LONG : " + gps_longtitude +
                "\n image_GPS_LA : " + gps_latitude);
    }

    public void backAnalysis(Bitmap image_bitmap){ //배경 분석

        ImageAlgo imageAlgo_to_value = new ImageAlgo(image_bitmap);
        ImageStat imageStat =imageAlgo_to_value.analysis();
        ImageAlgo imageAlgo_to_analysis = new ImageAlgo(imageStat, emotion);

        emotion = imageAlgo_to_analysis.emotion;

        show();

    }

    private void setup(){ // layout 설정
        capture = (ImageButton)findViewById(R.id.btn);
        btn_analysis = (ImageButton)findViewById(R.id.start_analysis);
        iv = (ImageView)findViewById(R.id.iv);
        textView = (TextView)findViewById(R.id.textView);
    }

}
