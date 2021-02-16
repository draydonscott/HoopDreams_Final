package com.example.hoopdreams;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoopdreams.ui.main.ShootAroundHelper;

public class ShootAround extends AppCompatActivity {
    Button buttonShotMade;
    Button buttonShotMiss;
    Button buttonEndSession;
    TextView shotsMadetxt;
    TextView shotAttemptstxt;
    TextView shootingPercentagetxt;
    TextView shotStreaktxt;
    Chronometer timeElapsed;
    ShootAroundHelper helper;
    ShotData RecentShot;
    private boolean running = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String date;
        setContentView(R.layout.activity_shoot_around);
        buttonShotMade = findViewById(R.id.buttonShotMade);
        shotsMadetxt = findViewById(R.id.shotsMadetxt);
        shotAttemptstxt = findViewById(R.id.shotAttempstxt);
        buttonShotMiss = findViewById(R.id.buttonShotMiss);
        shotStreaktxt = findViewById(R.id.shotStreaktxt);
        timeElapsed = findViewById(R.id.timeElapsed);
        shootingPercentagetxt = findViewById(R.id.shootingPercentagetxt);
        buttonEndSession = findViewById(R.id.buttonEndSession);
        helper = new ShootAroundHelper();
        RecentShot = new ShotData();
        DataBaseHelper databasehelper = new DataBaseHelper(ShootAround.this);
        date = helper.getDate();

        if( !running ){
            timeElapsed.setBase(SystemClock.elapsedRealtime());
            timeElapsed.start();
            running = true;
        }

        buttonShotMade.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RecentShot = helper.updateStats("1", shotsMadetxt, shotAttemptstxt);
               displayStats(RecentShot);
            }
        });

        buttonShotMiss.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RecentShot = helper.updateStats("0", shotsMadetxt, shotAttemptstxt);
                displayStats(RecentShot);
            }
        });

        buttonEndSession.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v){
                boolean success = databasehelper.addOne(helper.getTime(),helper.getShotsMade(),helper.getShotsTaken(),date,"Shoot Around");
                Toast.makeText(ShootAround.this,helper.calculateExperience(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }
    private void displayStats(ShotData shot){
        shotsMadetxt.setText(shot.ShotsMadeString);
        shotAttemptstxt.setText(shot.ShotsAttemptedString);
        shootingPercentagetxt.setText(shot.ShootingPercentageString);
        shotStreaktxt.setText(shot.ShotStreakString);
    }

}