package com.example.hoopdreams;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Fragment1 extends Fragment {
    Button buttonStart;
    public MainActivity act;
    public Fragment1(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout,container,false);


        buttonStart = (Button) view.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                act = (MainActivity) getActivity();
                Intent in = new Intent(getActivity(), ShootAround.class);
                in.putExtra(MainActivity.EXTRAS_DEVICE_NAME, act.getName());
                in.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, act.getAddress());
                startActivity(in);
            }
        });
        return view;
    }



}
