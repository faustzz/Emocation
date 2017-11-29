package com.example.user.emocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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

    private String gps_latitude = null, gps_longtitude = null;
    ImageButton capture = null;
    ImageView iv = null;
    Uri mCurrentPhotoPath;
    private Activity cameraActivity = this;
    private TextView textView;


    int i = 0;
    Uri uri;
    private ImageButton btn_analysis;
    private Scores[] scores;
    private double[] savaAvgScores= new double[8];
    Bitmap image_bitmap_analysis;
    Emotion emotion;
    String name_Str;

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
                mCurrentPhotoPath = createImageFile();                           // mCurrentPhotoPath = 사진이 저장될 경로
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mCurrentPhotoPath);    // putExtra를 통해 사진의 저장 경로를 지정해준다.


                int cameraPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);     // 카메라 사용 Permission 체크를 위한 변수
                if(cameraPermissionCheck == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(cameraActivity, new String[]{android.Manifest.permission.CAMERA},2);      // 사용 권한이 없을 경우 사용 권한 Request.
                }else{
                    startActivityForResult(intent,1);                                                                               // 사용 권한이 있을 경우 카메라 실행.
                }
            }
        });

        btn_analysis = (ImageButton)findViewById(R.id.start_analysis);
        btn_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backAnalysis(image_bitmap_analysis);

                String name = "emocationImg" + i;
                LocationData locationData = new LocationData(); // 장식용
                Picture picture = new Picture(gps_latitude, gps_longtitude, emotion, name);
                ImageToDB imageToDB = new ImageToDB(uri, locationData, picture, name);
                imageToDB.saveToFirebase();
                i++;
            }
        });
    }

    private Uri createImageFile() {
        String imageFileName = "/" + System.currentTimeMillis() + ".jpg";       //사진파일명
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation");       // 사진 저장 경로
        Uri uri = Uri.fromFile(new File(storageDir, imageFileName));
        return uri;
    }

    private void setup(){
        capture = (ImageButton)findViewById(R.id.btn);
        iv = (ImageView)findViewById(R.id.iv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==1){
            if(resultCode==RESULT_OK){
                String photoPath = mCurrentPhotoPath.getPath();         // photopath 에 사진 경로 저장
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath, options);  // imageview에 이미지를 띄우기 전에 decodeFile을 통해 사진의 크기를 1/options 크기만큼 줄여준다.
                uri = getImageUri(getApplicationContext(), imageBitmap);
                name_Str = getImageNameToUri(uri);
                exiF(photoPath);
                textView = (TextView)findViewById(R.id.text11) ;
                textView.setText(   "\n image_GPS_LONG : " + gps_longtitude +
                        "\n image_GPS_LA : " + gps_latitude) ;



//                saveExifFile(imageBitmap, photoPath);
//                iv.setImageBitmap(imageBitmap);

                try{ // 카메라가 회전되어서 찍힌 경우. 카메라의 회전 각도에 따른 사진의 저장 형태를 변경시켜준다.
                    ExifInterface exif = new ExifInterface(photoPath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);     // 카메라가 현재 얼마나 회전되어있는지?
                    imageBitmap = rotate(imageBitmap, exifDegree);
                }catch (IOException e){
                    e.getStackTrace();
                }

                retro(imageBitmap);
                image_bitmap_analysis = imageBitmap;

                saveExifFile(imageBitmap, photoPath);
                iv.setImageBitmap(imageBitmap);

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
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

    public void saveExifFile(Bitmap imageBitmap, String savePath){
        FileOutputStream fos = null;
        File saveFile = null;

        try{
            saveFile = new File(savePath);
            fos = new FileOutputStream(saveFile);
            //원본형태를 유지해서 이미지 저장
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        }catch(FileNotFoundException e){
            //("FileNotFoundException", e.getMessage());
        }catch(IOException e){
            //("IOException", e.getMessage());
        }finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
    }


    private String getTagString(String tag, ExifInterface exif){
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }
    private void showExif(ExifInterface exif){

        gps_latitude = subString(getTagString(ExifInterface.TAG_GPS_LATITUDE,exif));
        gps_longtitude = subString(getTagString(ExifInterface.TAG_GPS_LONGITUDE,exif));
    }

    public String subString(String str){

        // 먼저 @ 의 인덱스를 찾는다 - 인덱스 값: 5
        int idx = str.indexOf(":");

        String str2 = str.substring(idx+2); // : 바로 뒷부분부터 추출한다.

        return str2;

    }

    public void exiF(String str){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(str);
            showExif(exif);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    public void retro(Bitmap bitmap){
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

//                Picture picture = new Picture(gps_latitude, gps_longtitude,emotion, name_Str);
//                LocationData locationData = new LocationData();
//                imageToDB = new ImageToDB(uri, locationData, picture, "사진");
//                imageToDB.saveToFirebase();

            }
        });
    }

    static String excessdouble(double emovalue){
        int exponent=0;
        String stringval=Double.toString(emovalue);  //문자열로 바꿈
        if(stringval.length()>8){

            if(stringval.indexOf("E") > -1){      //지수부분이 있으면
                exponent=(stringval.charAt(stringval.indexOf("E")+2))-'0';   //지수값 저장
                //0이하일때 처리
                stringval=stringval.substring(0,stringval.indexOf("E")-exponent);  //가수부분만 다시저장,0들어갈자리만큼 뒷자리삭제
                stringval=stringval.replace(".","");
                for(int i=0;i<exponent;i++){
                    if(i==exponent-1){stringval="."+stringval;}
                    stringval="0"+stringval;//지수만큼0을 붙임
                }
            }
            stringval=stringval.substring(0,8);    //소수점아래 6자리까지로 끊음
        }
        return stringval;
    }

    public void show(){
        textView.setText(" anger : " + excessdouble(emotion.anger) +
                " \n fear : " + excessdouble(emotion.fear) +
                " \n happiness : " + excessdouble(emotion.happiness) +
                " \n neutral : " + excessdouble(emotion.neutral) +
                " \n sadness : " +excessdouble(emotion.sadness) +
                " \n surprise : " + excessdouble(emotion.sadness) +
                "\n image_GPS_LONG : " + gps_longtitude +
                "\n image_GPS_LA : " + gps_latitude);
    }

    public void backAnalysis(Bitmap image_bitmap){

        ImageAlgo imageAlgo_to_value = new ImageAlgo(image_bitmap);
        ImageStat imageStat =imageAlgo_to_value.analysis();
        ImageAlgo imageAlgo_to_analysis = new ImageAlgo(imageStat, emotion);

        emotion = imageAlgo_to_analysis.emotion;

        show();

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
}
