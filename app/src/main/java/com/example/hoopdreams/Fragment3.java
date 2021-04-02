package com.example.hoopdreams;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hoopdreams.R;
import com.example.hoopdreams.ui.main.ExperienceItem;
import com.example.hoopdreams.ui.main.ExperienceTracker;
import com.example.hoopdreams.ui.main.HistoryAdapter;
import com.example.hoopdreams.DataBaseHelper;

public class Fragment3 extends Fragment {
    private static String TAG="FRAGMENT3";
    ProgressBar experienceBar;
    ExperienceItem item;
    TextView currentLeveltxt;
    TextView expRemtxt;
    TextView expNeededtxt;
    private Handler handler = new Handler();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3_layout, container, false);
        item = ExperienceTracker.determineBadge();
        experienceBar = view.findViewById(R.id.experienceBar);
        currentLeveltxt = view.findViewById(R.id.currentLeveltxt);
        expRemtxt = view.findViewById(R.id.expRemtxt);
        expNeededtxt = view.findViewById(R.id.expNeededtxt);
        expNeededtxt.setText("Exp to level up: " + item.ExpNeeded);
        expRemtxt.setText("Progress to next level: " + item.ExpRemaining + "%");
        currentLeveltxt.setText(item.Rank);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(view.getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        Cursor c = dataBaseHelper.getData();
        recyclerView.setAdapter(new HistoryAdapter(view.getContext(),c));


        new Thread(new Runnable() {
            @Override
            public void run() {
                item = ExperienceTracker.determineBadge();
                experienceBar.setProgress(item.ExpRemaining);
            }
        }).start();

       /* getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                item = ExperienceTracker.determineBadge();
                experienceBar.setProgress(item.ExpRemaining);
            }
        });*/
        return view;
    }
    public void onResume(){
        super.onResume();
        item = ExperienceTracker.determineBadge();
        experienceBar.setProgress(item.ExpRemaining);
    }

}
