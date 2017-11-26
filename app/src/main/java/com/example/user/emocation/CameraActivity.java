package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivity extends Activity {

    private String gps_latitude = null, gps_longtitude = null;
    Button capture = null;
    ImageView iv = null;
    Uri mCurrentPhotoPath;
    private Activity cameraActivity = this;
    private TextView textView;

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
    }

    private Uri createImageFile() {
        String imageFileName = "/" + System.currentTimeMillis() + ".jpg";       //사진파일명
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation");       // 사진 저장 경로
        Uri uri = Uri.fromFile(new File(storageDir, imageFileName));
        return uri;
    }

    private void setup(){
        capture = (Button)findViewById(R.id.btn);
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

                saveExifFile(imageBitmap, photoPath);
                iv.setImageBitmap(imageBitmap);

            }
        }
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
        gps_latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE,exif);
        gps_longtitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE,exif);
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
}
