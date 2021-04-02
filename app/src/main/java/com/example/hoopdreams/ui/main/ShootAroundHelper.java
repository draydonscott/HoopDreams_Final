package com.example.hoopdreams.ui.main;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.hoopdreams.ShotData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.lang.Math;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class ShootAroundHelper {
    private int ShotsMade;
    private int ShotsTaken;
    private int ShotStreak;
    private double ShootingPercentage;
    private Timer timer;
    private TimeTask task;
    private Random rand;
    private int bound;
    private byte[] feedback;

    public ShootAroundHelper(){
        bound = 10;
        ShotsMade = 0;
        ShotsTaken = 0;
        ShotStreak = 0;
        ShootingPercentage = 0;
        rand = new Random();
        timer = new Timer();
        task = new TimeTask();
        timer.scheduleAtFixedRate(task,0,1000);
        feedback = new byte[] {0,0,0,0,0};
    }

    public ShotData updateStats(String shot, TextView shotMade, TextView shotTaken){

        ShotData shotdata =  new ShotData();
        DecimalFormat dec = new DecimalFormat();
        dec.setMaximumFractionDigits(2);
        ShotsTaken++;

        if(shot.equals("1")){
            ShotsMade++;
            ShotStreak++;
            System.out.println("Play positive reinforcement noise "+ rand.nextInt(10));
        }
        else{
            ShotStreak = 0;
            System.out.println("Play negative reinforcement noise "+ rand.nextInt(10));
        }
        ShootingPercentage = (double) 100*ShotsMade/ShotsTaken;

        shotdata.ShotsMadeString = ("Shots Made: " + ShotsMade);
        shotdata.ShotsAttemptedString = ("Shots Attempted: "+ShotsTaken);
        shotdata.ShotStreakString = ("Shot Streak: " +ShotStreak);
        shotdata.ShootingPercentageString = ("Shooting Percentage: "+ dec.format(ShootingPercentage));
        return shotdata;

    }

    public ShotData updateStats(byte shot, TextView shotMade, TextView shotTaken){

        ShotData shotdata =  new ShotData();
        DecimalFormat dec = new DecimalFormat();
        dec.setMaximumFractionDigits(2);
        ShotsTaken++;

        if(shot==1){
            ShotsMade++;
            ShotStreak++;
            Log.d("ShootAroundHelper","Shot Made!");
            System.out.println("Play positive reinforcement noise "+ rand.nextInt(10));
            byte temp = (byte) rand.nextInt(5);
            feedback[0] = 1;
            feedback[1] = (byte) (temp+1);
            feedback[2] = 0;
            feedback[3] = 0;
            feedback[4] = 2;//lights
            Log.d("ShootAroundHelper",""+ feedback[0] + "," +feedback[1]);
        }
        else{
            ShotStreak = 0;
            System.out.println("Play negative reinforcement noise "+ rand.nextInt(10));
            byte temp = (byte) rand.nextInt(4);
            byte temp2 = (byte) rand.nextInt(4);
            feedback[0] = 2;
            feedback[1] = (byte) (1+temp);
            feedback[2] = 3;
            feedback[3] = (byte) (1+temp2);
            feedback[4] = 1; //lights
            Log.d("ShootAroundHelper",""+ feedback[0] + "," +feedback[1]);
        }
        ShootingPercentage = (double) 100*ShotsMade/ShotsTaken;

        shotdata.ShotsMadeString = ("Shots Made: " + ShotsMade);
        shotdata.ShotsAttemptedString = ("Shots Attempted: "+ShotsTaken);
        shotdata.ShotStreakString = ("Shot Streak: " +ShotStreak);
        shotdata.ShootingPercentageString = ("Shooting Percentage: "+ dec.format(ShootingPercentage));
        return shotdata;

    }

    public byte[] getFeedback(){
        return feedback;
    }



    public int getShotsMade(){
        return ShotsMade;
    }

    public int getShotsTaken(){
        return ShotsTaken;
    }
    public String getTime(){
        if(task.getTime()%60 < 10){
            return task.getTime()/60 + ":0" + task.getTime()%60;
        }
        else{
            return task.getTime()/60 + ":" + task.getTime()%60;
        }
    }
    public String getDate(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }

    public String calculateExperience(){
        String exper;
        int temp = (int) Math.log((double) task.getTime());
        int exp = temp*ShotsMade;
        //System.out.println("Experience gained: "+ exp);
        exper = "Experience gained: " + exp;
        ExperienceTracker.addExperience(exp);
        return exper;
    }
}
