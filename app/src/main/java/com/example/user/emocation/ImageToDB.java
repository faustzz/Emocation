package com.example.user.emocation;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.user.emocation.ImageInfo.LocationData;
import com.example.user.emocation.ImageInfo.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by user on 2017-11-27.
 */

public class ImageToDB {
    private Picture picture;
    private String image_name = null;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private DatabaseReference mDatabase;

    Uri imageUri;


    public ImageToDB(){}

    public ImageToDB(Uri imageUri, Picture picture, String image_name){
        this.imageUri = imageUri;
        this.picture = picture;
        this.image_name = image_name;
    }

    public void saveToFirebase(){
        saveImageDB(picture);
        uploadFile(imageUri,image_name);
    }

    private void saveImageDB(Picture picture){ //locationData 안에 picture가 있으니 좀 더 생각해 볼 것
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("EmocationImg").child(image_name).setValue(picture);
    }


    private void uploadFile(Uri data,String filename){
        StorageReference emocationRef = storageRef.child("emocation/" + filename);
        UploadTask uploadTask = emocationRef.putFile(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


}
