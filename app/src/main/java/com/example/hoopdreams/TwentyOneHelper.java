package com.example.hoopdreams;

import com.example.hoopdreams.ui.main.ExperienceTracker;
import com.example.hoopdreams.ui.main.TimeTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;

public class TwentyOneHelper {
    private int[] ShotsMade;
    private int[] ShotsTaken;
    private int[] ShotStreak;
    private double[] ShootingPercentage;
    private int[] PlayerScore;
    private int PlayerTurn;
    private int rebound;
    private Timer timer;
    private TimeTask task;
    private Random rand;
    private int bound;

    public TwentyOneHelper(){
        bound = 10;
        ShotsMade = new int[] {0,0};
        ShotsTaken = new int[] {0,0};
        ShotStreak = new int[] {0,0};
        ShootingPercentage = new double[] {0,0};
        PlayerScore = new int[] {0,0};
        PlayerTurn = 1;
        rebound = 0;


        timer = new Timer();
        task = new TimeTask();
        timer.scheduleAtFixedRate(task,0,1000);
        rand = new Random();
    }
    public boolean updateStats(String shot){
        if(rebound == 0){
            freeThrow(shot);
            if(!shot.equals("1")){
                rebound =1;
            }
        }
        else if (rebound == 1){
            rebound(shot);
            rebound = 0;
        }
        return hasWon(PlayerScore[0]) || hasWon(PlayerScore[1]);
    }

    public boolean updateStats(byte shot){
        if(rebound == 0){
            freeThrow(shot);
            if(shot!=1){
                rebound =1;
            }
        }
        else if (rebound == 1){
            rebound(shot);
            rebound = 0;
        }
        return hasWon(PlayerScore[0]) || hasWon(PlayerScore[1]);
    }

    public void freeThrow(String shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot.equals("1")){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            PlayerScore[PlayerTurn-1]++;

            if(hasWon(PlayerScore[PlayerTurn-1])){
                gameOver();
            }
            else{
                System.out.println("Nice Shot, Shoot Another Freethrow Player " + PlayerTurn);
            }
        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Player "+ PlayerTurn +" get that rebound and try to score!");
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
    }

    public void freeThrow(byte shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot==1){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            PlayerScore[PlayerTurn-1]++;

            if(hasWon(PlayerScore[PlayerTurn-1])){
                gameOver();
            }
            else{
                System.out.println("Nice Shot, Shoot Another Freethrow Player " + PlayerTurn);
            }
        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Player "+ PlayerTurn +" get that rebound and try to score!");
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
    }

    public void rebound(String shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot.equals("1")){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            PlayerScore[PlayerTurn-1] = PlayerScore[PlayerTurn-1] + 2;

            if(hasWon(PlayerScore[PlayerTurn-1])){
                gameOver();
            }
            else{
                System.out.println("Nice Shot, Head to the Free-Throw Line Player " + PlayerTurn);
            }
        }
        else{
            ShotStreak[PlayerTurn-1] = 0;
            System.out.println("Nice Try, Head to the Free-Thow Line Player "+ PlayerTurn);
        }
        displayScore();
    }

    public void rebound(byte shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot==1){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            PlayerScore[PlayerTurn-1] = PlayerScore[PlayerTurn-1] + 2;

            if(hasWon(PlayerScore[PlayerTurn-1])){
                gameOver();
            }
            else{
                System.out.println("Nice Shot, Head to the Free-Throw Line Player " + PlayerTurn);
            }
        }
        else{
            ShotStreak[PlayerTurn-1] = 0;
            System.out.println("Nice Try, Head to the Free-Thow Line Player "+ PlayerTurn);
        }
        displayScore();
    }

    public int[] displayScore(){

        System.out.println("Player 1 Score: "+ PlayerScore[0]);
        System.out.println("Player 2 Score: "+ PlayerScore[1]);
        return PlayerScore;
    }

    private void updatePlayerTurn(){
        PlayerTurn = (PlayerTurn%2)+1;
    }
    private boolean hasWon(int score){
        if (score>=21){
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

    public void Print(){
        System.out.println("Begin Game! PLayer 1 head to the freethrow line!");
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
        calculateExperience();
    }

    public String calculateExperience(){
        String exper;
        int temp = (int) Math.log((double) task.getTime());
        int exp = temp*(ShotsMade[0]+ShotsMade[1]);

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
