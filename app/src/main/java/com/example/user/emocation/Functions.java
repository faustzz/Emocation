package com.example.user.emocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 2017-11-30.
 */

public class Functions { // 자주쓰는 함수들 모아논 class


    public Functions(){}

    public Uri createImageFile() { // 이미지 생성
        String imageFileName = "/" + System.currentTimeMillis() + ".jpg";       //사진파일명
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/emocation");       // 사진 저장 경로
        Uri uri = Uri.fromFile(new File(storageDir, imageFileName));
        return uri;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) { // Bitmap에서 Uri를 추출한다.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public int exifOrientationToDegrees(int exifOrientation){ // 사진의 회전 정도
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
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


    public String subString(String str, String index){

        // 먼저 @ 의 인덱스를 찾는다 - 인덱스 값: 5
        int idx = str.indexOf(index);
        String str2 = str;
        if(index == ":") // exif의 gps string 분해를 위해
            str2 = str.substring(idx+2); // : 바로 뒷부분부터 추출한다.
        if(index == ".") // 사진 string 분해를 위해
            str2 = str.substring(0,idx); // : 앞부분 부터 추출한다.

        return str2;

    }

    public String excessdouble(double emovalue){ // 지수로 표현되는 double 값을 소수점 아래 6자리까지만 보여줌
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

    public Bitmap rotate(Bitmap bitmap, int degrees){ // 사진을 회전시켜 준다.
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


}
