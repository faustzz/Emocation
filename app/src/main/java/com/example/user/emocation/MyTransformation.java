package com.example.user.emocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

/**
 * Created by Joo Hyun Jun on 2017-12-02.
 */

public class MyTransformation extends BitmapTransformation {

    private int mOrientation = 0;

    public MyTransformation(Context context, int orientation) {
        super(context);
        mOrientation = orientation;
    }
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int exifOrientationDegrees = getExifOrientationDegrees(mOrientation);
        return TransformationUtils.rotateImageExif(toTransform, pool, exifOrientationDegrees);
    }

    private int getExifOrientationDegrees(int orientation) {
        int exifInt;
        switch (orientation) {
            case 90:
                exifInt = ExifInterface.ORIENTATION_ROTATE_90;
                break;
//more cases
            default:
                exifInt = ExifInterface.ORIENTATION_NORMAL;
                break;
        }
        return exifInt;

    }
    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return  "com.example.helpers.MyTransformation.";
    }
}