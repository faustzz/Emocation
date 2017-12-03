package com.example.user.emocation.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.user.emocation.Functions;
import com.example.user.emocation.ImageAlgorithm.Emotion;
import com.example.user.emocation.ImageInfo.Picture;
import com.example.user.emocation.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 2017-12-01.
 */

public class MarkerImageActivity extends Activity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("emocation/");
    Functions FUNCTION = new Functions();
    Picture picture = new Picture();
    private ImageView imageView;
    private TextView txt_avgEmotion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_image);

        imageView = (ImageView)findViewById(R.id.imageView);
        txt_avgEmotion = (TextView)findViewById(R.id.txt_avgEmotion);

        Bundle bundle = getIntent().getExtras();
        picture = bundle.getParcelable("title");

        try {
            loadImageg();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Emotion realValue = FUNCTION.showRealValue(picture.getEmotion()); // 산출값 * 1000
        txt_avgEmotion.setText(" anger : " + Math.round(realValue.anger) +
                " \n fear : " + Math.round(realValue.fear) +
                " \n happiness : " + Math.round(realValue.happiness) +
                " \n neutral : " + Math.round(realValue.neutral) +
                " \n sadness : " + Math.round(realValue.sadness) +
                " \n surprise : " + Math.round(realValue.surprise));

    }

    public void loadImageg() throws IOException {

        final File localFile = File.createTempFile("images", "jpeg");
        storageRef.child(picture.getImage_name()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath()); // firebase 안의 사진이 90도 회전 해서 저장된 것이 '원본'으로 인식됨. 강제로 돌려주는 수 밖에 없다. Glide로 안돌려짐
                    imageView.setImageBitmap(FUNCTION.rotate(bitmap, 270));

            }
        });
    }
}
