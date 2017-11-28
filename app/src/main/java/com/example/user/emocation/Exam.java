package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
 * Created by user on 2017-11-24.
 */

public class Exam extends AppCompatActivity {
    private String gps_latitude = null, gps_longtitude = null;
    private TextView textView;
    private ImageButton btn_gallery;
    private Scores[] scores;
    private double[] savaAvgScores= new double[8];
    private List<Double> avgScores = new ArrayList<Double>();
    Uri uri;
    String name_Str;
    ImageToDB imageToDB;
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
                name_Str = getImageNameToUri(data.getData());
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
                    int exifDegree = exifOrientationToDegrees(exifOrientation);     // 카메라가 현재 얼마나 회전되어있는지?
                    image_bitmap = rotate(image_bitmap, exifDegree);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageView image = (ImageView)findViewById(R.id.imageView);

                //배치해놓은 ImageView에 set
                image.setImageBitmap(image_bitmap);

                uri = data.getData();
                exiF(data, name_Str);
                retro(image_bitmap);
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
        EmotionRestClient.init(getApplicationContext(),"9070f1ed33494504aaf4a6918ed21a64");
        EmotionRestClient.getInstance().detect(bitmap, new ResponseCallback() {
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                textView.setText(
                        "\n image_GPS_LONG : " + gps_longtitude +
                                "\n image_GPS_LA : " + gps_latitude) ;
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

                avgScores.add(savaAvgScores[0]);
                avgScores.add(savaAvgScores[3]);
                avgScores.add(savaAvgScores[4]);
                avgScores.add(savaAvgScores[5]);
                avgScores.add(savaAvgScores[6]);
                avgScores.add(savaAvgScores[7]);


                Picture picture = new Picture(gps_latitude, gps_longtitude,avgScores, name_Str);
                LocationData locationData = new LocationData();
                imageToDB = new ImageToDB(uri, locationData, picture, "사진");
                imageToDB.saveToFirebase();

//                textView.setText(" anger : " + avgScores[0] +
//                        " \n contempt : " + avgScores[1] +
//                        " \n disgust : " + avgScores[2] +
//                        " \n fear : " + avgScores[3] +
//                        " \n happiness : " + avgScores[4] +
//                        " \n neutral : " + avgScores[5] +
//                        " \n sadness : " + avgScores[6] +
//                        " \n surprise : " + avgScores[7] +
//                        "\n image_GPS_LONG : " + gps_longtitude +
//                        "\n image_GPS_LA : " + gps_latitude);
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
