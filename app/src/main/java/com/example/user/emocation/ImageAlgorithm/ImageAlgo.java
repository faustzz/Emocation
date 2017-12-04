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
 This class determines 3 values about total picture and adjust emtion values which is input to get final emtion value.
 It is about the (total/average) vitality, temporature, mordernity of input picture.
 The calculation result is stored in this class as 'backgroundValue' and 'totalValue'.
 */
    Bitmap bitmap;
    private Emotion emotion;
    private BGvalue backgroundValue;
    private Emotion totalValue;
    private String BGConclusion;

    public ImageAlgo() {

    }

    public ImageAlgo(Bitmap bitmap, Emotion emotion) {
        this.bitmap = bitmap;
        this.emotion = emotion;

        ImageStat status = analysis();
        backgroundValue = new BGvalue();
        totalValue = new Emotion(emotion);

        AdjByMainColor(status.getMainColors());
        AdjByContrast(status.getContrast(status.getHisto(0)));
        AdjByTemperature(status.getTemp());
        AdjBySaturation(status.getSaturation());
        BGConclusion = BGconclude();
    }


    public ImageStat analysis() {        //analize the img to find some information about img
        ImageStat imgstat = null;
        try {
            imgstat = new ImageStat(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgstat;
    }


    private String BGconclude() {
        double threshold = 0.03;
        double av = Math.abs(backgroundValue.getVitality()),
                at = Math.abs(backgroundValue.getTemperature() - 0.05),
                am = Math.abs(backgroundValue.getModernity());

        double max = Math.max(Math.max(av, at), am);
        if (max < threshold) {
            return "What a Boring Picture... ";
        } else if (max == av) {
            if (backgroundValue.getVitality() > 0)
                return "The picture looks VIGOROUS! Hoooo wooo!";
            else
                return "The picture looks CALM... ha-um";
        } else if (max == at) {
            if (backgroundValue.getTemperature() > 0)
                return "The picture looks WARM~ It made my heart warm too*_*";
            else
                return "The picture looks COLD... Is that cold out there?";
        } else if (max == am) {
            if (backgroundValue.getModernity() > 0)
                return "The picture looks MORDERN. By the way, have you ever been to Chicago? ";
            else
                return "The picture looks NATURAL! You healed me :)";
        }
        return "no description";
    }

    private void AdjByMainColor(String[] mainColors) {
        //modify backgroundValue and total emotion value by main 3 colors that is obtained by analizing
        double vital = 0, tempo = 0, mordern = 0;
        Emotion temp = new Emotion();

        for (int i = 1; i < 4; i++) {
            if (mainColors[i - 1].compareTo("red") == 0) {
                vital += (0.03 / i);
                tempo += (0.01 / i);
                mordern -= (0.02 / i);
                temp.anger += (0.004 / i);
                temp.fear += (0.002 / i);
                temp.sadness -= (0.002 / i);
            } else if (mainColors[i - 1].compareTo("yellow") == 0) {
                vital += (0.02 / i);
                tempo += (0.03 / i);
                mordern -= (0.03 / i);
                temp.happiness += (0.002 / i);
                temp.surprise += (0.004 / i);
            } else if (mainColors[i - 1].compareTo("green") == 0) {
                vital += (0.02 / i);
                tempo -= (0.01 / i);
                mordern -= (0.02 / i);
                temp.anger -= (0.003 / i);
                temp.neutral += (0.005 / i);
                temp.happiness += (0.002 / i);
            } else if (mainColors[i - 1].compareTo("blue") == 0) {
                vital -= (0.03 / i);
                tempo -= (0.05 / i);
                mordern -= (0.01 / i);
                temp.neutral += (0.005 / i);
                temp.sadness += (0.002 / i);
            } else if (mainColors[i - 1].compareTo("white") == 0) {
                vital += (0.01 / i);
                tempo -= (0.04 / i);
                mordern += (0.03 / i);
                temp.neutral += (0.003 / i);
                temp.fear += (0.001 / i);
            } else if (mainColors[i - 1].compareTo("black") == 0) {
                vital -= (0.02 / i);
                tempo -= (0.03 / i);
                mordern += (0.03 / i);
                temp.fear += (0.005 / i);
                temp.sadness += (0.001 / i);
                temp.neutral += (0.001 / i);
            }
//			else if(mainColors[i-1].compareTo("gray") == 0){
//
//			}
            else if (mainColors[i - 1].compareTo("purple") == 0) {
                vital += (0.01 / i);
                tempo -= (0.02 / i);
                mordern -= (0.03 / i);
                temp.sadness += (0.005 / i);
                temp.surprise += (0.002 / i);
                temp.fear += (0.001 / i);
            } else if (mainColors[i - 1].compareTo("brown") == 0) {
                vital -= (0.05 / i);
                tempo += (0.03 / i);
                mordern -= (0.02 / i);
                temp.neutral += (0.004 / i);
                temp.happiness += (0.002 / i);
            }
        }

        backgroundValue.add(new BGvalue(vital, tempo, mordern));
        totalValue.addEmotionValue(temp);
    }

    private void AdjByContrast(double contrast) {   //modify backgroundValue by contrast that is obtained by analizing
        double vital = 0, tempo = 0, mordern = 0;
        Emotion temp = new Emotion();

        vital -= (1 - contrast) / 10;
        tempo += (contrast - 0.5) / 10;
        mordern += (1 - contrast) / 10;
        temp.happiness += (0.003 * contrast);
        temp.surprise += (0.001 * contrast);

        backgroundValue.add(new BGvalue(vital, tempo, mordern));
        totalValue.addEmotionValue(temp);
    }

    private void AdjBySaturation(double saturation) {   //modify backgroundValue by saturation that is obtained by analizing
        double vital = 0, tempo = 0, mordern = 0;

        vital -= (1 - saturation) / 10;
        tempo -= saturation / 100;
        mordern += saturation / 10;

        backgroundValue.add(new BGvalue(vital, tempo, mordern));
        totalValue.mulEmotionValue(saturation);
    }

    private void AdjByTemperature(double t) {   //modify backgroundValue by temperature that is obtained by analizing
        double vital = 0, tempo = t / 100, mordern = 0;
        Emotion temp = new Emotion();

        temp.anger -= (t / 1000);
        temp.sadness -= (t / 1000);
        temp.fear -= (t / 1000);

        backgroundValue.add(new BGvalue(vital, tempo, mordern));
        totalValue.addEmotionValue(temp);
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public BGvalue getBackgroundValue() {
        return backgroundValue;
    }

    public Emotion getTotalValue() {
        return totalValue;
    }

    public String getBGConclusion() {
        return BGConclusion;
    }
}