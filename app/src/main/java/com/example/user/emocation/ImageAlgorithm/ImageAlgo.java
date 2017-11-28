package com.example.user.emocation.ImageAlgorithm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class ImageAlgo {
    TextView textView;
    Bitmap bitmap;
    Emotion emotion;

    public ImageAlgo(){

    }

    public ImageAlgo(Bitmap bitmap) {
        this.bitmap = bitmap;
    }




    public ImageStat analysis(){
        ImageStat imgstat = null;
        try {
            imgstat = new ImageStat(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgstat;
    }

    private void AdjByMainColor(String[] mainColors) {
        Emotion temp = new Emotion();

        for (int i = 1; i < 4; i++) {
            if (mainColors[i - 1].compareTo("red") == 0) {
                temp.anger += (0.04 / i);
                temp.fear += (0.02 / i);
                temp.sadness -= (0.02 / i);
            } else if (mainColors[i - 1].compareTo("yellow") == 0) {
                temp.happiness += (0.02 / i);
                temp.surprise += (0.04 / i);
            } else if (mainColors[i - 1].compareTo("green") == 0) {
                temp.anger -= (0.03 / i);
                temp.neutral += (0.05 / i);
                temp.happiness += (0.02 / i);
            } else if (mainColors[i - 1].compareTo("blue") == 0) {
                temp.neutral += (0.05 / i);
                temp.sadness += (0.02 / i);
            } else if (mainColors[i - 1].compareTo("white") == 0) {
                temp.neutral += (0.03 / i);
                temp.fear += (0.01 / i);
            } else if (mainColors[i - 1].compareTo("black") == 0) {
                temp.fear += (0.05 / i);
                temp.sadness += (0.01 / i);
                temp.neutral += (0.01 / i);
            }
//			else if(mainColors[i-1].compareTo("gray") == 0){
//				
//			}
            else if (mainColors[i - 1].compareTo("purple") == 0) {
                temp.sadness += (0.05 / i);
                temp.surprise += (0.02 / i);
                temp.fear += (0.01 / i);
            } else if (mainColors[i - 1].compareTo("brown") == 0) {
                temp.neutral += (0.06 / i);
                temp.happiness += (0.02 / i);
            }
        }

        emotion.addEmotionValue(temp);
    }

    private void AdjByContrast(double contrast) {
        Emotion temp = new Emotion();
        temp.happiness += (0.03 * contrast);
        temp.surprise += (0.01 * contrast);

        emotion.addEmotionValue(temp);
    }

    private void AdjBySaturation(double saturation) {
        emotion.mulEmotionValue(saturation);
    }

    private void AdjByTemperature(double t) {
        Emotion temp = new Emotion();
        temp.anger -= (t / 10);
        temp.sadness -= (t / 10);
        temp.fear -= (t / 10);
    }

    public ImageAlgo(ImageStat status) {
        emotion = new Emotion();
        AdjByMainColor(status.getMainColors());
        AdjByContrast(status.getContrast(status.getHisto(0)));
        AdjBySaturation(status.getSaturation());
        AdjByTemperature(status.getTemp());
    }
}
