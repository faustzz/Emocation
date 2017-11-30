package com.example.user.emocation.ImageAlgorithm;

/**
 * Created by caucse on 2017-11-28.
 */
public class Emotion{
	public double anger;
	public double fear;
	public double happiness;
	public double neutral;
	public 	double sadness;
	public 	double surprise;

	public Emotion(double a, double f,double h,double n,double sad,double sur) {
		this.anger=a;
		this.fear=f;
		this.happiness = h;
		this.neutral = n;
		this.sadness = sad;
		this.surprise = sur;
	}
	public Emotion() {
		this.anger=0.0;
		this.fear=0.0;
		this.happiness = 0.0;
		this.neutral = 0.0;
		this.sadness = 0.0;
		this.surprise = 0.0;
	}

	public void addEmotionValue(Emotion that) {

		this.anger += that.anger;
		this.fear += that.fear;
		this.happiness += that.happiness;
		this.neutral += that.neutral;
		this.sadness += that.sadness;
		this.surprise += that.surprise;

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

		adjVal();
	}

	public void mulEmotionValue(double val) {

		this.anger *= val;
		this.fear *= val;
		this.happiness *= val;
		this.neutral *= val;
		this.sadness *= val;
		this.surprise *= val;

		adjVal();
	}

	private void adjVal() {
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


}
