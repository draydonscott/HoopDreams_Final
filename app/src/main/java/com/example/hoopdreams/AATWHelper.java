package com.example.hoopdreams;

import com.example.hoopdreams.ui.main.ExperienceTracker;
import com.example.hoopdreams.ui.main.TimeTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;
import java.util.Random;

public class AATWHelper {

    private int[] ShotsMade;
    private int[] ShotsTaken;
    private int[] ShotStreak;
    private double[] ShootingPercentage;
    private int PlayerTurn;

    private Timer timer;
    private TimeTask task;
    private Random rand;
    private int bound;
    private byte[] feedback;

    public AATWHelper(){
        bound = 10;
        ShotsMade = new int[] {0,0};
        ShotsTaken = new int[] {0,0};
        ShotStreak = new int[] {0,0};
        ShootingPercentage = new double[] {0,0};
        PlayerTurn = 1;


        timer = new Timer();
        task = new TimeTask();
        timer.scheduleAtFixedRate(task,0,1000);
        rand = new Random();
        System.out.println("Player 1, you shoot first!");
        feedback = new byte[] {4,1,0,0,4};
        feedback[0] = 4;
        feedback[1] = 1 ;
        feedback[2] = 0;
        feedback[3] = 0;
        feedback[4] = 4; //this is for lights
    }

    public boolean updateStats(String shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot.equals("1")){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            if(hasWon(PlayerTurn)){
                gameOver();
                return true;
            }
            System.out.println("Great shot Player "+ PlayerTurn+ ". Move to location "+ (ShotStreak[PlayerTurn-1]+1) + " and shoot again");

        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Nice Try! Player "+ PlayerTurn +" your turn to shoot from location 1!");
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
        return false;
    }

    public boolean updateStats(byte shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot == 0x01){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            if(hasWon(PlayerTurn)){
                byte file = (byte) PlayerTurn;
                feedback[0] = 4;
                feedback[1] = file++;
                feedback[2] = 0;
                feedback[3] = 0;
                feedback[4] = 2; //this is for lights
                gameOver();
                return true;
            }
            System.out.println("Great shot Player "+ PlayerTurn+ ". Move to location "+ (ShotStreak[PlayerTurn-1]+1) + " and shoot again");

            feedback[0] = 4;
            feedback[2] = 0;
            feedback[3] = 0;
            feedback[4] = 2; //this is for lights
            if(PlayerTurn == 1){
                int temp = ShotStreak[PlayerTurn-1] +1;
                temp = temp + 2;
                byte file = (byte) temp;
                feedback[1] = file;
            }
            else{
                int temp = ShotStreak[PlayerTurn-1] +1;
                temp = temp + 8;
                byte file = (byte) temp;
                feedback[1] = file;
            }
        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Nice Try! Player "+ PlayerTurn +" your turn to shoot from location 1!");

            feedback[0] = 4;
            feedback[2] = 0;
            feedback[3] = 0;
            feedback[4] = 1; //this is for lights
            if(PlayerTurn == 1){
                feedback[1] = 16;
            }
            else{
                feedback[1] = 17;
            }
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
        return false;
    }



    public int[] displayScore(){
        System.out.println("Player 1 Score: "+ ShotStreak[0]);
        System.out.println("Player 2 Score: "+ ShotStreak[1]);
        System.out.println("Elapsed Time: "+ getTime());
        return ShotStreak;
    }
    public byte[] getFeedback(){
        return feedback;
    }

    private void updatePlayerTurn(){
        PlayerTurn = (PlayerTurn%2)+1;
    }

    public boolean finished(){
        return hasWon(1) || hasWon(2);
    }

    private boolean hasWon(int turn){
        if (ShotStreak[turn-1]>=7){
            return true;
        }
        else{
            return(false);
        }
    }

    public void displayStats(){
        System.out.println("Shots Made: "+ShotsMade);
        System.out.println("Shots Attempted: "+ShotsTaken);
        System.out.println("Shooting Percentage: "+ShootingPercentage);
        System.out.println("Elapsed Time: "+ getTime());
    }


    public String getTime(){
        if(task.getTime()%60 < 10){
            return task.getTime()/60 + ":0" + task.getTime()%60;
        }
        else{
            return task.getTime()/60 + ":" + task.getTime()%60;
        }
    }

    public void gameOver(){
        timer.cancel();
        timer.purge();
        displayScore();
        calculateExperience();
        System.out.println("Session Over");
    }

    public String calculateExperience(){
        String exper;
        int temp = (int) Math.log((double) task.getTime());
        int exp = temp*(ShotsMade[0]+ShotsMade[1]);
        System.out.println("Experience gained: "+ exp);
        ExperienceTracker.addExperience(exp);
        exper = "Experience gained: "+ exp;
        return exper;
    }
    public String getDate(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }
    public int[] getShotsMade(){
        return ShotsMade;
    }

    public int[] getShotsTaken(){
        return ShotsTaken;
    }
}
