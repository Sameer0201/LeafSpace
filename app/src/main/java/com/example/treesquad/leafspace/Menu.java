package com.example.treesquad.leafspace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }
    private int[] pids= {0,0,0,0,0,0,0,0,0,0};
    private int currentPID=0;

    public void onImgClick(View view) {
        Intent i = new Intent(Menu.this, dataView.class);
        int pid=pids[currentPID];
        i.putExtra("id",pid);
        //database stuff
        startActivity(i);
    }


    //depending on how the database is implemented will determine how these methods are implemented in reality.
    //the onnextimg will also be put on an alarm for like 15 seconds to scroll through all of the trees
    public void onprevimg(View view) {
        if(currentPID==0){
            currentPID=9;
        }
        else{
            currentPID--;
        }
        //set the main img to current pid, then change the prev and next. This requires database stuff
    }

    public void onnextimg(View view) {
        if(currentPID==9){
            currentPID=0;
        }
        else{
            currentPID++;
        }
        //set the main img to current pid, then change the prev and next. This requires database stuff

    }
}
