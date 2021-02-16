package com.example.hoopdreams.ui.main;

import java.util.logging.Level;

public class ExperienceTracker{
    private static int exp = 0;
    private static final int[] LEVELS = {0,100,250,500,900,1500,2500,3800,5000,6500,8500};

    public ExperienceTracker(){
    }

    public static ExperienceItem determineBadge(){
        ExperienceItem item = new ExperienceItem();
        int level = 1;
        String rank = "";

        if(exp< LEVELS[1]){
            level = 1;
            rank = "Badge level 1 achieved: Basketball Newbie";
        }
        else if(exp<LEVELS[2]){
            level = 2;
            rank = ("Badge level 2 achieved: Basketball Beginner");
        }
        else if(exp<LEVELS[3]){
            level = 3;
            rank =  ("Badge level 3 achieved: Showing Promise");
        }
        else if(exp<LEVELS[4]){
            level = 4;
            rank = ("Badge level 4 achieved: High School Player");
        }
        else if(exp<LEVELS[5]){
            level = 5;
            rank =  ("Badge level 5 achieved: USports player");
        }
        else if(exp<LEVELS[6]){
            level = 6;
            rank =  ("Badge level 6 achieved: NCAA player");
        }
        else if(exp<LEVELS[7]){
            level = 7;
            rank =  ("Badge level 7 achieved: Overseas professional talen");
        }
        else if(exp<LEVELS[8]){
            level = 8;
            rank = ("Badge level 8 achieved: NBA player (bench :/)");
        }
        else if(exp<LEVELS[9]){
            level = 9;
            rank = ("Badge level 9 achieved: NBA starter");
        }
        else if(exp<LEVELS[10]){
            level = 10;
            rank =  ("Badge level 10 achieved: NBA All-Star");
        }
        else{
            level = 11;
            rank =  ("Badge level 11 achieved: Greatest of all-timeeeee!!!");
        }
        item.Level = level;
        item.Rank = rank;
        item.ExpNeeded = LEVELS[level] - exp;
        double temp1 =  LEVELS[level] - LEVELS[level-1];
        double temp = (double) (1 - ((double) item.ExpNeeded/temp1)) * 100;
        item.ExpRemaining = (int) temp;

        return item;
    }

    public static void addExperience(int additionalExp){
        exp = exp + additionalExp;
    }

    public static String getExperience(){
        return ("Experience Gained: " + exp);
    }
}
