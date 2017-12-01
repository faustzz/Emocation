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
    /*
    This class determines 3 values about total picture.
    It is about the (total/average) vitality, temporature, mordernity of input picture.
    The calculation result is stored in this class as 'backgroundValue'.
    */
    TextView textView;
    Bitmap bitmap;
    private Emotion emotion;
    private BGvalue backgroundValue; 
    private Emotion totalValue;

    public ImageAlgo(){

    }

    public ImageAlgo(Bitmap bitmap, Emotion emotion) {
        this.bitmap = bitmap;
        this.emotion = emotion;
        ImageStat status = analysis();
        backgroundValue = new BGvalue();
        AdjByMainColor(status.getMainColors());
        AdjByContrast(status.getContrast(status.getHisto(0)));
        AdjBySaturation(status.getSaturation());
        AdjByTemperature(status.getTemp());
    }

    
    

    public ImageStat analysis(){        //analize the img to find some information about img
        ImageStat imgstat = null;
        try {
            imgstat = new ImageStat(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgstat;
    }

    private void AdjByMainColor(String[] mainColors) {  //modify backgroundValue by main 3 colors that is obtained by analizing
        double vital=0,tempo=0,mordern=0;
        for (int i = 1; i < 4; i++) {
            if (mainColors[i - 1].compareTo("red") == 0) {  
                vital += (0.04 / i);
                tempo += (0.02 / i);
                mordern -= (0.02 / i);
            } else if (mainColors[i - 1].compareTo("yellow") == 0) {
                vital += (0.04 / i);
                tempo += (0.04 / i);
                mordern -= (0.03 / i);
            } else if (mainColors[i - 1].compareTo("green") == 0) {
                vital += (0.02 / i);
                tempo -= (0.03 / i);
                mordern -= (0.04 / i);
            } else if (mainColors[i - 1].compareTo("blue") == 0) {
                vital -= (0.03 / i);
                tempo -= (0.05 / i);
                mordern -= (0.03 / i);
            } else if (mainColors[i - 1].compareTo("white") == 0) {
                vital += (0.01 / i);
                tempo -= (0.04 / i);
                mordern += (0.03 / i);
            } else if (mainColors[i - 1].compareTo("black") == 0) {
                vital -= (0.03 / i);
                tempo -= (0.05 / i);
                mordern += (0.03 / i);
            }
//			else if(mainColors[i-1].compareTo("gray") == 0){
//				
//			}
            else if (mainColors[i - 1].compareTo("purple") == 0) {
                vital += (0.01 / i);
                tempo -= (0.02 / i);
                mordern += (0.02 / i);
            } else if (mainColors[i - 1].compareTo("brown") == 0) {
                vital -= (0.05 / i);
                tempo += (0.03 / i);
                mordern -= (0.03 / i);
            }
        }
        backgroundValue.add(new BGvalue(vital,tempo,mordern));
    }

    private void AdjByContrast(double contrast) {   //modify backgroundValue by contrast that is obtained by analizing
        double vital=0,tempo=0,mordern=0;
        vital -= (1-contrast)/10;
        tempo += (contrast-0.5)/10;
        mordern += (1-contrast)/10;

        backgroundValue.add(new BGvalue(vital,tempo,mordern));
    }

    private void AdjBySaturation(double saturation) {   //modify backgroundValue by saturation that is obtained by analizing
        double vital=0,tempo=0,mordern=0;
        vital -= (1-saturation)/10;
        tempo -= saturation/100;
        mordern += saturation/10;
        backgroundValue.add(new BGvalue(vital,tempo,mordern));
    }

    private void AdjByTemperature(double t) {   //modify backgroundValue by temperature that is obtained by analizing
        double vital=0,tempo=t/10,mordern=0;
        backgroundValue.add(new BGvalue(vital,tempo,mordern));
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public BGvalue getBackgroundValue() {
        return backgroundValue;
    }
    
    public BGvalue getTotalValue() {
        return totalValue;
    }
}
