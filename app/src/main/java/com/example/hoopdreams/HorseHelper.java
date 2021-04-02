package com.example.hoopdreams;

import com.example.hoopdreams.ui.main.ExperienceTracker;
import com.example.hoopdreams.ui.main.TimeTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;
import java.util.Random;

public class HorseHelper {

    private int[] ShotsMade;
    private int[] ShotsTaken;
    private int[] ShotStreak;
    private double[] ShootingPercentage;
    private ArrayList<String> PlayerScore;
    private int PlayerTurn;
    private int New;
    private Timer timer;
    private TimeTask task;
    private Random rand;
    private int bound;
    boolean flag = false;



    public HorseHelper(){
        bound = 10;
        ShotsMade = new int[] {0,0};
        ShotsTaken = new int[] {0,0};
        ShotStreak = new int[] {0,0};
        ShootingPercentage = new double[] {0,0};
        PlayerScore = new ArrayList<String>();
        PlayerScore.add("");
        PlayerScore.add("");
        PlayerTurn = 1;
        New = 1;

        timer = new Timer();
        task = new TimeTask();
        timer.scheduleAtFixedRate(task,0,1000);
        rand = new Random();
        System.out.println("Player 1, you shoot first!");
    }
    public boolean updateStats(byte shot){
        if(New == 1){
            newShot(shot);
            if(shot==1){
                New = 0;
            }
        }
        else{
            recreateShot(shot);
            New = 1;
        }
        if(hasLost(PlayerScore.get(0))){return true;}
        else if (hasLost(PlayerScore.get(1))){return true;}
        else return false;
    }

    public void newShot(byte shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot==1){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            updatePlayerTurn();
            System.out.println("Nice Shot! Player " + PlayerTurn + " recreate that Shot!");
        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Nice Try! Player "+ PlayerTurn +" your turn to try a new shot!");
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
    }

    public void recreateShot(byte shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot==1){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            updatePlayerTurn();
            System.out.println("Nice Shot! Player " + PlayerTurn + " attempt a new shot!");

        }
        else{
            ShotStreak[PlayerTurn-1] = 0;
            updateScore(PlayerTurn);
            if (hasLost(PlayerScore.get(PlayerTurn-1))){
                gameOver();
                return;
            }
            updatePlayerTurn();
            System.out.println("Bummer:(. Player "+ PlayerTurn+ " attempt a new shot");
        }
        displayScore();
    }

    public boolean finished(){
        return hasLost(PlayerScore.get(0)) || hasLost(PlayerScore.get(1));
    }
    public boolean updateStats(String shot){
        if(New == 1){
            newShot(shot);
            if(shot.equals("1")){
                New = 0;
            }
        }
        else{
            recreateShot(shot);
            New = 1;
        }
        if(hasLost(PlayerScore.get(0))){return true;}
        else if (hasLost(PlayerScore.get(1))){return true;}
        else return false;
    }

    public void newShot(String shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot.equals("1")){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            updatePlayerTurn();
            System.out.println("Nice Shot! Player " + PlayerTurn + " recreate that Shot!");
        }

        else{
            ShotStreak[PlayerTurn-1] = 0;
            updatePlayerTurn();
            System.out.println("Nice Try! Player "+ PlayerTurn +" your turn to try a new shot!");
        }

        ShootingPercentage[PlayerTurn-1] = (double) 100*ShotsMade[PlayerTurn-1]/ShotsTaken[PlayerTurn-1];
        displayScore();
    }

    public void recreateShot(String shot){
        ShotsTaken[PlayerTurn-1]++;
        if(shot.equals("1")){
            ShotsMade[PlayerTurn-1]++;
            ShotStreak[PlayerTurn-1]++;
            updatePlayerTurn();
            System.out.println("Nice Shot! Player " + PlayerTurn + " attempt a new shot!");

        }
        else{
            ShotStreak[PlayerTurn-1] = 0;
            updateScore(PlayerTurn);
            if (hasLost(PlayerScore.get(PlayerTurn-1))){
                gameOver();
                return;
            }
            updatePlayerTurn();
            System.out.println("Bummer:(. Player "+ PlayerTurn+ " attempt a new shot");
        }
        displayScore();
    }

    private void updateScore(int turn){
        String temp = PlayerScore.get(turn-1);
        switch(temp){
            case "":
                PlayerScore.set(turn-1, "H");
                break;

            case "H":
                PlayerScore.set(turn-1, "HO");
                break;

            case "HO":
                PlayerScore.set(turn-1, "HOR");
                break;

            case "HOR":
                PlayerScore.set(turn-1, "HORS");
                break;

            case "HORS":
                PlayerScore.set(turn-1, "HORSE");
                break;
        }
    }

    public ArrayList<String> displayScore(){
        System.out.println("Player 1 Score: "+ PlayerScore.get(0));
        System.out.println("Player 2 Score: "+ PlayerScore.get(1));
        System.out.println("Elapsed Time: "+ getTime());
        return PlayerScore;
    }

    private void updatePlayerTurn(){
        PlayerTurn = (PlayerTurn%2)+1;
    }

    public boolean hasLost(String score){
        if (score.equals("HORSE")){
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