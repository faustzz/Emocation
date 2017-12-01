package com.example.user.emocation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by user on 2017-12-01.
 */

public class MarkerImageActivity extends Activity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("emocation/");
    Functions FUNCTION = new Functions();

    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_image);

        imageView = (ImageView)findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        Picture picture = bundle.getParcelable("title");

        storageRef.child(picture.getImage_name()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(MarkerImageActivity.this)
                        .load(uri)
                        .transform(new MyTransformation(MarkerImageActivity.this, 90))
                        .into(imageView);

                Toast.makeText(getApplicationContext(), "dd:" + uri, Toast.LENGTH_SHORT).show();

                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
}
