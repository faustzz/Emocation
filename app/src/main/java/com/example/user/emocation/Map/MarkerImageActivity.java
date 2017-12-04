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
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference("emocation/");
    private Functions FUNCTION = new Functions();
    private Picture picture = new Picture();
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
        if(picture.getEmotion().neutral<0)
            picture.getEmotion().neutral*=(-1);
        txt_avgEmotion.setText(" anger : " + Math.round(picture.getEmotion().anger) +
                " \n fear : " + Math.round(picture.getEmotion().fear) +
                " \n happiness : " + Math.round(picture.getEmotion().happiness) +
                " \n neutral : " + Math.round(picture.getEmotion().neutral) +
                " \n sadness : " + Math.round(picture.getEmotion().sadness) +
                " \n surprise : " + Math.round(picture.getEmotion().surprise));

    }

    public void loadImageg() throws IOException {

        final File localFile = File.createTempFile("images", "jpeg");
        storageRef.child(picture.getImage_name()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath()); // firebase 안의 사진이 90도 회전 해서 저장된 것이 '원본'으로 인식됨. 강제로 돌려주는 수 밖에 없다. Glide로 안돌려짐
                  //  imageView.setImageBitmap(FUNCTION.rotate(bitmap, 270));
                imageView.setImageBitmap(bitmap);

            }
        });
    }
}
