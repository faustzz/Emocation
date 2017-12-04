package com.example.user.emocation.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;
import android.widget.TextView;

import com.example.user.emocation.Functions;
import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.example.user.emocation.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joo Hyun Jun on 2017-12-02.
 */

public class MarkerLocationActivity extends Activity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference("emocation/");

    private double latitude, longitude;
    private double avgAnger = 0, avgFear = 0, avgHappiness = 0, avgNeutral = 0, avgSadness = 0, avgSurprise = 0;
    private int numOfLocationImages = 0;
    private LocationData locationData = GoogleMapActivity.locationData;
    private List<Bitmap> dd = new ArrayList<>();

    private TextView txt_avgEmotion;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_images);

        Picture picture = GoogleMapActivity.picture;

        latitude = Double.parseDouble(picture.getLatitute());
        longitude = Double.parseDouble(picture.getLongitude());
        txt_avgEmotion = (TextView)findViewById(R.id.txt_avgEmotion);
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(avgNeutral<0)
            avgNeutral*=(-1);
        txt_avgEmotion.setText(" anger : " + Math.round(avgAnger*1000) +
                " \n fear : " + Math.round(avgFear*1000) +
                " \n happiness : " + Math.round(avgHappiness*1000) +
                " \n neutral : " + Math.round(avgNeutral*1000) +
                " \n sadness : " + Math.round(avgSadness*1000) +
                " \n surprise : " + Math.round(avgSurprise*1000));

    }

    public void loadImages() throws IOException {
        for(int i = 0 ; i < locationData.getPicture().size() ; i++) {
            double isINlatitude = Double.parseDouble(locationData.getPicture().get(i).getLatitute());
            double isINlongitude = Double.parseDouble(locationData.getPicture().get(i).getLongitude());
            if (isIN(latitude, longitude, isINlatitude, isINlongitude)) {
                avgAnger += locationData.getPicture().get(i).getEmotion().anger;
                avgFear += locationData.getPicture().get(i).getEmotion().fear;
                avgHappiness += locationData.getPicture().get(i).getEmotion().happiness;
                avgNeutral += locationData.getPicture().get(i).getEmotion().neutral;
                avgSadness += locationData.getPicture().get(i).getEmotion().sadness;
                avgSurprise += locationData.getPicture().get(i).getEmotion().surprise;
                numOfLocationImages++;
                final File localFile = File.createTempFile("images", "jpeg");
                storageRef.child(locationData.getPicture().get(i).getImage_name()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                        //dd.add(FUNCTION.rotate(bitmap, 270));// firebase 안의 사진이 90도 회전 해서 저장된 것이 '원본'으로 인식됨. 강제로 돌려주는 수 밖에 없다. Glide로 안돌려짐
                        dd.add(bitmap);
                        GridView gridview = (GridView) findViewById(R.id.gridView1); // firebase 가져올 때, 동기화 방식을 쓰는것 같음. 파일이 다 불려오기 전에 다음 코드가 실행된다. 그러므로 파일 하나 불러올 때 마다 그리드 뷰 생성
                        //gridview.setColumnWidth(300);
                        gridview.setAdapter(new MyAdapter(getApplicationContext(), dd));
                    }

                });

            }
        }
        avgAnger/=numOfLocationImages; // 위치 점위 안에 해당하는 사진들으 emotion 값들의 평균
        avgFear/=numOfLocationImages;
        avgHappiness/=numOfLocationImages;
        avgNeutral/=numOfLocationImages;
        avgSadness/=numOfLocationImages;
        avgSurprise/=numOfLocationImages;
    }

    public boolean isIN(double latitude, double longitude, double isINlatitude, double isINlongitude){
        if((latitude-0.002 < isINlatitude && latitude+0.002> isINlatitude && longitude-0.0025< isINlongitude && longitude+0.0025>isINlongitude) || ((latitude == isINlatitude) && (longitude == isINlongitude))){
            return true;
        }
        else
            return false;
    }

}
