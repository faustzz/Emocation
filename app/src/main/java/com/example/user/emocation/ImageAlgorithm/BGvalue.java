package com.example.user.emocation.ImageAlgorithm;

/**
 * Created by ttolc on 2017-12-01.
 */

public class BGvalue {
    private double vitality;
    private double temperature;
    private double mordernity;


    public BGvalue(double v, double t, double m){   //constructor
        vitality=v;
        temperature=t;
        mordernity=m;
    }

    public BGvalue(){   //default constructor
        vitality=0;
        temperature=0;
        mordernity=0;
    }


    public void add(BGvalue t){     //add v,t,m value to original one.

        this.vitality += t.vitality;
        this.temperature += t.temperature;
        this.mordernity += t.mordernity;

    }

    public double getVitality(){
        return this.vitality;
    }
    public double getTemperature(){
        return this.temperature;
    }
    public double getMordernity() {
        return this.mordernity;
    }

}
