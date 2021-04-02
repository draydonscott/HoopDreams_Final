package com.example.hoopdreams;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {
    Button twentyOneButton;
    Button horseButton;
    Button worldButton;
    Button timedShootAroundButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment2_layout,container,false);
        twentyOneButton = (Button) view.findViewById(R.id.twentyOneButton);
        horseButton = (Button) view.findViewById(R.id.horseButton);
        worldButton = (Button) view.findViewById(R.id.worldButton);
        twentyOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), TwentyOne.class);
                startActivity(in);
            }
        });
        horseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), Horse.class);
                startActivity(in);
            }
        });
        worldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(),AllAroundTheWorld.class);
                startActivity(in);
            }
        });
        return view;
    }
}
