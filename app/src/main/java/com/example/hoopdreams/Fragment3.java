package com.example.hoopdreams;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoopdreams.ui.main.ExperienceItem;
import com.example.hoopdreams.ui.main.ExperienceTracker;
import com.example.hoopdreams.ui.main.HistoryAdapter;

public class Fragment3 extends Fragment {
    ProgressBar experienceBar;
    ExperienceItem item;
    TextView currentLeveltxt;
    TextView expRemtxt;
    TextView expNeededtxt;
    private Handler handler = new Handler();

    private Cursor getAllItems() {
        return database.query(
                DataBaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DataBaseHelper.COLUMN_SESSION_DATE + "DESC"
        );
    }

    //Below is for RecyclerView
    private SQLiteDatabase database;
    private HistoryAdapter adapter;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3_layout, container, false);
        item = ExperienceTracker.determineBadge();
        experienceBar = view.findViewById(R.id.experienceBar);
        currentLeveltxt = view.findViewById(R.id.currentLeveltxt);
        expRemtxt = view.findViewById(R.id.expRemtxt);
        expNeededtxt = view.findViewById(R.id.expNeededtxt);
        expNeededtxt.setText("Exp to level up" + item.ExpNeeded);
        expRemtxt.setText("Progress to next level:" + item.ExpRemaining + " %");
        currentLeveltxt.setText(item.Rank);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new HistoryAdapter(view.getContext(),getAllItems()));


        new Thread(new Runnable() {
            @Override
            public void run() {
                experienceBar.setProgress(item.ExpRemaining);
            }
        }).start();




        return view;
    }
}
