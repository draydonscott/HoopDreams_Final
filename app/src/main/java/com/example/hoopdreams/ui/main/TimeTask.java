package com.example.hoopdreams.ui.main;

import android.content.Context;
import android.widget.TextView;

import com.example.hoopdreams.R;

import java.util.TimerTask;

public class TimeTask extends TimerTask {
    int SecondsPassed;
    TextView timeElapsedtxt;

    public TimeTask(){
        SecondsPassed = 0;
    }

    public void run(){
        SecondsPassed++;
    }


    public int getTime(){
        return SecondsPassed;
    }
}
