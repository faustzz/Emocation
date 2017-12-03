package com.example.user.emocation.ImageAlgorithm;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by caucse on 2017-11-28.
 */
public class ImageStat{

	static int[] primaryColors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.WHITE,Color.BLACK,Color.rgb(128,0,255), Color.rgb(150,75,0)};
	static String[] colorNames = {"red","yellow","green","blue","white","black","purple","brown"};
	//colors are declared static way since it is needless that every instance contains the whole colors.
	
	private int width;		//width of the image
	private int height;		//height of the image
	private int numOfPixels;	//number of pixels in the image
	private int [] histo_red,histo_green,histo_blue,histo_gray;	//histograms of the image
	private double sat,br;		//saturation and brightness of the image
	private double temperature;	//temperature of the image

	private String [] mainColors = new String[3];	//0 is main color


	public ImageStat(){}

	public ImageStat(Bitmap bfimg) throws IOException {
		
		//init part
		width = bfimg.getWidth();
		height = bfimg.getHeight();
		numOfPixels = width*height;

		histo_red = new int[256]; Arrays.fill(histo_red, 0);
		histo_green = new int[256]; Arrays.fill(histo_green, 0);
		histo_blue = new int[256]; Arrays.fill(histo_blue, 0);
		histo_gray = new int[256]; Arrays.fill(histo_gray, 0);
		sat = 0.0;
		br = 0.0;
		temperature=0.0;
		
		
		//calculating part
		calcHisto(bfimg);
		calcTemp();
	}

	private void calcHisto(Bitmap bi) throws IOException{
		/*
		Access to each pixels to :
		1. find histogram of the image(R,G,B,Gray)
		2. find saturation, brightness
		3. find 3 main colors
		*/
		long [] diff = new long[primaryColors.length];
		Arrays.fill(diff, 0);


		float sat_total=0,br_total=0;
		float[] hsb = new float[3];

		
		for(int i=0;i<numOfPixels;i++){
			int p = bi.getPixel(i%width, i/width);

			//to find 3 main colors, add all the differences(R,G,B) between all primary colors(j)
			for(int j=0;j<primaryColors.length;j++){
				diff[j] += Math.abs(Color.red(primaryColors[j]) - Color.red(p));
				diff[j] += Math.abs(Color.red(primaryColors[j]) - Color.green(p));
				diff[j] += Math.abs(Color.red(primaryColors[j]) - Color.blue(p));
			}

			//1. find histogram of the image(R,G,B,Gray)
			int red=Color.red(p),green=Color.green(p),blue=Color.blue(p);
			histo_red[red]++;
			histo_green[green]++;
			histo_blue[blue]++;
			int Y = (int)(red*0.2126 + green*0.7152 +blue*0.0722);
			histo_gray[Y]++;

			//2. find saturation, brightness
			Color.RGBToHSV(red,green,blue, hsb);
			sat_total += hsb[1];
			br_total += hsb[2];
		}
		
		//2. find saturation, brightness
		sat = sat_total/numOfPixels;
		br = br_total/numOfPixels;

		//3. find 3 main colors
		//using selection sort for only first 3 elements.
		String[] tempNames = colorNames.clone();
		for(int i=0;i<3;i++){
			int min = i;
			for(int j=i;j<diff.length;j++){
				if(diff[min]>diff[j])
					min=j;
			}
			mainColors[i] = tempNames[min];
			long tmp = diff[i];
			String s = tempNames[i];
			diff[i] = diff[min];
			tempNames[i] = tempNames[min];
			diff[min] = tmp;
			tempNames[min] = s;
		}
	}

	private void calcTemp(){
		//yellow picture is warm, cold if blue
		//We thounght red is more importat than green, so give red 60% to determine yellow value.
		double meanY = (getHistoMean(histo_red)*0.6 +getHistoMean(histo_green)*0.4);
		temperature += (meanY - getHistoMean(histo_blue))/3;	//divide by 3 to get value that is near to real temp
	}
	public float getContrast(int[] histo){
		//contrast is width of histogram
		//contrast is ratio.
		int high=255,low=0;			//index of max, min histo
		int threshold = numOfPixels/500;	//the meaningful color should be more than 2% of the total image
		for(int i=0;i<256;i++){
			if(histo[i]>threshold && low==0){
				low = i;
			}
			if(histo[255-i]>threshold && high==255){
				high = 255-i;
			}
		}

		return (float)(high-low)/255;
	}

	public int getHistoMean(int[] histo){
		//calculate mean of given histogram
		//average index value is returned
		double tot=0;
		for(int i=0;i<256;i++){
			tot+=((double)histo[i]*i)/(numOfPixels);
		}
		return (int)tot;
	}

	public double getSaturation(){
		return this.sat;
	}

	public double getBrightness(){
		return this.br;
	}

	public int[] getHisto(int n){	//0 is gray, 1 is R, 2 is G, 3 is B
		switch(n){
		case 0:
			return Arrays.copyOf(histo_gray, 256);
		case 1:
			return Arrays.copyOf(histo_red, 256);
		case 2:
			return Arrays.copyOf(histo_green, 256);
		case 3:
			return Arrays.copyOf(histo_blue, 256);
		}
		return null;
	}
	public String[] getMainColors(){
		return this.mainColors;
	}
	public double getTemp(){
		return this.temperature;
	}
}
