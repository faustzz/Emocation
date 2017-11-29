package com.example.user.emocation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emocation.ImageAlgorithm.ImageAlgo;
import com.example.user.emocation.ImageAlgorithm.ImageStat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by leesd on 2017-11-26.
 */

public class Storage extends Activity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private ImageButton btn_gallery;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        textView = (TextView)findViewById(R.id.textView);
        btn_gallery = (ImageButton)findViewById(R.id.button_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromGallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1000){
            if(resultCode==Activity.RESULT_OK){
                //Uri에서 이미지 이름을 얻어온다.
                String name_Str = getImageNameToUri(data.getData());

              //  uploadFile(data.getData(),name_Str);
                Bitmap image_bitmap = null;
                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
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

    private void selectFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1000);
    }
    private void uploadFile(Uri data,String filename){
        StorageReference emocationRef = storageRef.child("emocation/" + filename);
        UploadTask uploadTask = emocationRef.putFile(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
