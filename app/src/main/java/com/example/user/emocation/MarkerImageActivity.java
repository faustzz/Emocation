package com.example.user.emocation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.tasks.OnFailureListener;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_image);

        imageView = (ImageView)findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        picture = bundle.getParcelable("title");

        try {
            loadImageg();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
