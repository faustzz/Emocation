package com.example.user.emocation.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.user.emocation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-12-02.
 */

public class MyAdapter extends BaseAdapter { // 사진들을gridview에 보여줄 어댑터

    Context context = null;

    //-----------------------------------------------------------
    // imageIDs는 이미지 파일들의 리소스 ID들을 담는 배열입니다.
    // 이 배열의 원소들은 자식 뷰들인 ImageView 뷰들이 무엇을 보여주는지를
    // 결정하는데 활용될 것입니다.

    List<Bitmap> imageIDs = new ArrayList<>();

    public MyAdapter(Context context, List<Bitmap> imageIDs) {
        this.context = context;
        this.imageIDs = imageIDs;
    }

    public int getCount() {
        return (null != imageIDs) ? imageIDs.size() : 0;
    }

    public Object getItem(int position) {
        return (null != imageIDs) ? imageIDs.get(position) : 0;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;

        if (null != convertView)
            imageView = (ImageView)convertView;
        else {



            imageView = new ImageView(context); // GridView 뷰를 구성할 ImageView 뷰들을 정의한다.
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(imageIDs.get(position));

//Listener 객체를 만들어서 클릭 이벤트를 만듬.
//            ImageClickListener imageViewClickListener
//                    = new ImageClickListener(context, imageIDs[position]);
//            imageView.setOnClickListener(imageViewClickListener);
        }

        return imageView;
    }
}




