package com.example.treesquad.leafspace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class dataView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        Intent i = getIntent();
        int id = i.getExtras().getInt("id");
        //database stuff
        String s = "Placeholder Post title";
        setTitle(s);

       // TextView textView = (TextView) findViewById(R.id.dataText);
       // textView.setMovementMethod(new ScrollingMovementMethod());

    }

    public void onDownvote(View view) {
    }

    public void onUpvote(View view) {
    }
}
