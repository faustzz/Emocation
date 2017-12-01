package com.example.user.emocation.ImageAlgorithm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caucse on 2017-11-28.
 */
public class Emotion implements Parcelable{// Activity 간 object를 전달하기 위해 Parcelable을 implements함.
	public double anger;
	public double fear;
	public double happiness;
	public double neutral;
	public double sadness;
	public double surprise;

	public Emotion(double a, double f,double h,double n,double sad,double sur) {	//constructor
		this.anger=a;
		this.fear=f;
		this.happiness = h;
		this.neutral = n;
		this.sadness = sad;
		this.surprise = sur;
	}
	public Emotion(Emotion that){	//copy constructor
		this.anger=that.anger;
		this.fear=that.fear;
		this.happiness = that.happiness;
		this.neutral = that.neutral;
		this.sadness = that.sadness;
		this.surprise = that.surprise;
	}
	public Emotion() {	//default constructor
		this.anger=0.0;
		this.fear=0.0;
		this.happiness = 0.0;
		this.neutral = 0.0;
		this.sadness = 0.0;
		this.surprise = 0.0;
	}

	protected Emotion(Parcel in) {
		anger = in.readDouble();
		fear = in.readDouble();
		happiness = in.readDouble();
		neutral = in.readDouble();
		sadness = in.readDouble();
		surprise = in.readDouble();
	}

	public static final Creator<Emotion> CREATOR = new Creator<Emotion>() {
		@Override
		public Emotion createFromParcel(Parcel in) {
			return new Emotion(in);
		}

		@Override
		public Emotion[] newArray(int size) {
			return new Emotion[size];
		}
	};

	public void addEmotionValue(Emotion that) {	//add each emotions to original one.

		this.anger += that.anger;
		this.fear += that.fear;
		this.happiness += that.happiness;
		this.neutral += that.neutral;
		this.sadness += that.sadness;
		this.surprise += that.surprise;

		adjVal();
	}

	public void mulEmotionValue(double val) {	//multiply all emotion value by input

		this.anger *= val;
		this.fear *= val;
		this.happiness *= val;
		this.neutral *= val;
		this.sadness *= val;
		this.surprise *= val;

		adjVal();
	}

	private void adjVal() {		//adjust each values to be bigger than 0 and smaller than 1.
		if(this.anger > 1.0)
			this.anger = 1.0;
		else if(this.anger<0)
			this.anger = 0;

		if(this.fear > 1.0)
			this.fear = 1.0;
		else if(this.fear<0)
			this.fear = 0;

		if(this.happiness > 1.0)
			this.happiness = 1.0;
		else if(this.happiness<0)
			this.happiness = 0;

		if(this.neutral > 1.0)
			this.neutral = 1.0;
		else if(this.neutral<0)
			this.neutral = 0;

		if(this.sadness > 1.0)
			this.sadness = 1.0;
		else if(this.sadness<0)
			this.sadness = 0;

		if(this.surprise > 1.0)
			this.surprise = 1.0;
		else if(this.surprise<0)
			this.surprise = 0;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeDouble(anger);
		parcel.writeDouble(fear);
		parcel.writeDouble(happiness);
		parcel.writeDouble(neutral);
		parcel.writeDouble(sadness);
		parcel.writeDouble(surprise);
	}
}
