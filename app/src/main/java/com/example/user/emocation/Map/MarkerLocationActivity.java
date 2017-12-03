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
    private double latitude, longitude;
    private double avgAnger = 0, avgFear = 0, avgHappiness = 0, avgNeutral = 0, avgSadness = 0, avgSurprise = 0;
    private int numOfLocationImages = 0;
    private Functions FUNCTION = new Functions();

    TextView txt_avgEmotion;
    LocationData locationData = GoogleMapActivity.locationData;
    List<Bitmap> dd = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("emocation/");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_images);

        Picture picture = GoogleMapActivity.picture;

        latitude = convert(picture.getLatitute());
        longitude = convert(picture.getLongitude());
        txt_avgEmotion = (TextView)findViewById(R.id.txt_avgEmotion);
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        txt_avgEmotion.setText(" anger : " + FUNCTION.excessdouble(avgAnger) +
                " \n fear : " + FUNCTION.excessdouble(avgFear) +
                " \n happiness : " + FUNCTION.excessdouble(avgHappiness) +
                " \n neutral : " + FUNCTION.excessdouble(avgNeutral) +
                " \n sadness : " + FUNCTION.excessdouble(avgSadness) +
                " \n surprise : " + FUNCTION.excessdouble(avgSurprise));

    }

    public void loadImages() throws IOException {
        for(int i = 0 ; i < locationData.getPicture().size() ; i++) {
            double isINlatitude = convert(locationData.getPicture().get(i).getLatitute());
            double isINlongitude = convert(locationData.getPicture().get(i).getLongitude());
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
                        dd.add(FUNCTION.rotate(bitmap, 270));// firebase 안의 사진이 90도 회전 해서 저장된 것이 '원본'으로 인식됨. 강제로 돌려주는 수 밖에 없다. Glide로 안돌려짐

                        GridView gridview = (GridView) findViewById(R.id.gridView1); // firebase 가져올 때, 동기화 방식을 쓰는것 같음. 파일이 다 불려오기 전에 다음 코드가 실행된다. 그러므로 파일 하나 불러올 때 마다 그리드 뷰 생성
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
        if(latitude-0.002 < isINlatitude && latitude+0.002> isINlatitude && longitude-0.0025< isINlongitude && longitude+0.0025>isINlongitude){
            return true;
        }
        else
            return false;
    }


    public Double convert(String LongLat){ // 도 분 초로 표현 된 위도경도값을 십진법으로 표현
        double dd=0,mm=0,ss=0;
        String[] str = LongLat.split("/1");

        for(int i=0;i<3;i++){
            if (str[i].startsWith(",")) {
                str[i]=str[i].replace(",","");
            }
        }

        dd =  Double.parseDouble(str[0]);
        mm= Double.parseDouble(str[1]);
        ss=Double.parseDouble(str[2]);
        double dec=dd+(mm/60)+(ss/3600);

        String decimal =Double.toString(dec);
        return dec;
    }
}
